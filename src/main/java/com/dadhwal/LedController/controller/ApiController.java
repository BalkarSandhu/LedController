package com.dadhwal.LedController.controller;

import com.dadhwal.LedController.config.Config;
import com.dadhwal.LedController.controller.requests.EthernetConfig;
import com.dadhwal.LedController.controller.requests.MessageRequest;
import com.dadhwal.LedController.services.SdkService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ApiController {

    private final SdkService sdkService;
    private static final Logger logger = Logger.getLogger(ApiController.class.getName());
    private static volatile boolean isProcessingPublishMessage = false;
    private static final Gson gson = new Gson();

    public ApiController(SdkService sdkService) {
        this.sdkService = sdkService;
    }

    private ResponseEntity<String> checkSdkStatus() {
        if (!sdkService.isSdkInitialized()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("SDK not initialized yet.");
        }
        return null;
    }

    @GetMapping("/sdkStatus")
    public ResponseEntity<String> getSdkStatus() {
        return sdkService.isSdkInitialized()
                ? ResponseEntity.ok("SDK is initialized.")
                : ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("SDK not initialized yet.");
    }

    @GetMapping("/status")
    public ResponseEntity<String> getSystemStatus() {
        JsonObject status = new JsonObject();

        status.addProperty("sdkInitialized", sdkService.isSdkInitialized());
        status.addProperty("login", sdkService.isLogin());

        Config config = sdkService.readConfig();

        String ip = config.getControllerIp();
        status.addProperty("controllerIp", ip != null ? ip : "");
        status.addProperty("baseUrl", config.getBaseUrl() != null ? config.getBaseUrl() : "");
        status.addProperty("wbFile", config.getWbFile() != null ? config.getWbFile() : "");

        // Check if ip is set and not blank before pinging
        if (ip != null && !ip.isBlank()) {
            try {
                status.addProperty("ipReachable", sdkService.isControllerIpPingable());
            } catch (Exception e) {
                status.addProperty("ipReachable", false);
            }
        } else {
            status.addProperty("ipReachable", false);
        }

        return ResponseEntity.ok(status.toString());
    }

    @PostMapping("/config")
    public ResponseEntity<String> updateConfig(@RequestBody Config request) {
        try {

            sdkService.updateConfig(request.getControllerIp(), request.getWbFile(), request.getBaseUrl());
            sdkService.transferWebProgram().get();
            return ResponseEntity.ok("Configuration updated successfully.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to update configuration", e);
            return ResponseEntity.status(500).body("Failed to update configuration");
        }
    }

    @GetMapping("/config")
    public ResponseEntity<?> getConfig() {
        try {
            Config config = sdkService.readConfig();
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to read configuration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to read configuration");
        }
    }

    @GetMapping("/readable")
    public ResponseEntity<String> readFile() {
        try {
            return ResponseEntity.ok(sdkService.readWbFileContent() + " KG");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error reading file: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login() {
        boolean loggedIn = sdkService.performLogin();
        return loggedIn
                ? ResponseEntity.ok("Login Successful")
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login Failed");
    }

    @GetMapping("/searchTerminal")
    public ResponseEntity<String> searchTerminal() {
        ResponseEntity<String> sdkStatus = checkSdkStatus();
        if (sdkStatus != null)
            return sdkStatus;

        try {
            return ResponseEntity.ok(sdkService.searchTerminal().get());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Search Terminal failed", e);
            return ResponseEntity.status(500).body("Search Terminal failed");
        }
    }

    @GetMapping("/searchScreen")
    public ResponseEntity<String> searchTerminalIp() {
        ResponseEntity<String> sdkStatus = checkSdkStatus();
        if (sdkStatus != null)
            return sdkStatus;

        try {
            String result = sdkService.searchTerminalByIp().get();
            JsonObject response = gson.fromJson(result, JsonObject.class);
            if (response.has("logined") && !response.get("logined").getAsBoolean()) {
                sdkService.performLogin();
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Search Terminal by IP failed", e);
            return ResponseEntity.status(500).body("Search Terminal by IP failed");
        }
    }

    @PostMapping("/publishMessage")
    public ResponseEntity<String> setPublishMessage(@RequestBody MessageRequest messageRequest) {
        if (isProcessingPublishMessage)
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Another message is being processed. Try again.");

        isProcessingPublishMessage = true;
        try {
            if (!sdkService.isLogin())
                sdkService.performLogin();
            ResponseEntity<String> sdkStatus = checkSdkStatus();
            if (sdkStatus != null)
                return sdkStatus;

            String color;
            String message;
            if (messageRequest.getInfo() != null) {
                message = messageRequest.getInfo();
                color = "#5bc0de";
            } else if (messageRequest.getSuccess() != null) {
                message = messageRequest.getSuccess();
                color = "#22bb33";
            } else if (messageRequest.getError() != null) {
                message = messageRequest.getError();
                color = "#bb2124";
            } else {
                return ResponseEntity.badRequest().body("No valid message content provided.");
            }

            String result = sdkService.publishMessage(message, color).get();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error Publishing Message", e);
            return ResponseEntity.status(500).body("Set Page Program failed");
        } finally {
            isProcessingPublishMessage = false;
        }
    }

    @GetMapping("/publishMessage")
    public ResponseEntity<String> setPublishMessageViaParams(
            @RequestParam(required = false) String info,
            @RequestParam(required = false) String success,
            @RequestParam(required = false) String error) {

        if (isProcessingPublishMessage)
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Another message is being processed. Try again.");

        isProcessingPublishMessage = true;
        int retryCount = 3;
        int attempt = 0;

        try {
            if (!sdkService.isLogin()) {
                if (!sdkService.performLogin()) {
                    return ResponseEntity.status(500).body("Login failed before publish");
                }
            }

            ResponseEntity<String> sdkStatus = checkSdkStatus();
            if (sdkStatus != null)
                return sdkStatus;

            String message = info != null ? info : success != null ? success : error != null ? error : null;
            String color = info != null ? "#5bc0de" : success != null ? "#22bb33" : "#bb2124";

            while (attempt < retryCount) {
                try {
                    if (message != null) {
                        return ResponseEntity.ok(sdkService.publishMessage(message, color).get());
                    } else {
                        return ResponseEntity.ok(sdkService.transferWebProgram().get());
                    }
                } catch (Exception e) {
                    attempt++;
                    logger.log(Level.SEVERE, "Retry attempt " + attempt, e);
                    sdkService.performLogin(); // retry login
                    Thread.sleep(2000);
                }
            }

            return ResponseEntity.status(500).body("Set Page Program failed after retries.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Unexpected error: " + e.getMessage());
        } finally {
            isProcessingPublishMessage = false;
        }
    }

    @GetMapping("/getProgramInfo")
    public ResponseEntity<String> getProgramInfo() {
        try {
            return ResponseEntity.ok(sdkService.getProgramInfo().get());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Get Program Info failed");
        }
    }

    @PostMapping("/setVolumeData")
    public ResponseEntity<String> setVolume() {
        try {
            return ResponseEntity.ok(sdkService.setVolume().get());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Set Volume failed");
        }
    }

    @GetMapping("/getVolume")
    public ResponseEntity<String> getVolume() {
        try {
            return ResponseEntity.ok(sdkService.getVolume().get());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Get Volume failed");
        }
    }

    @GetMapping("/getEthernetInfo")
    public ResponseEntity<String> getEthernetInfo() {
        try {
            String result = sdkService.searchTerminalByIp().get();
            JsonObject response = gson.fromJson(result, JsonObject.class);
            if (response.has("logined") && !response.get("logined").getAsBoolean()) {
                sdkService.performLogin();
            }
            return ResponseEntity.ok(sdkService.getEthernetInfo().get());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Get Ethernet Info failed");
        }
    }

    @PostMapping("/setEthernetInfo")
    public ResponseEntity<String> setEthernetInfo(@RequestBody EthernetConfig config) {
        try {
            String result = sdkService.setEthernetInfo(config).get(10, TimeUnit.SECONDS);
            sdkService.updateConfig(config.getIp(), null, null); // Update config with new IP
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Set Ethernet Info failed: " + e.getMessage());
        }
    }

    @PostMapping("/setScreenInfo")
    public ResponseEntity<String> setScreenInfo() {
        try {
            return ResponseEntity.ok(sdkService.setScreenInfo().get());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Set Screen Info Failed");
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Test Passed");
    }


}
