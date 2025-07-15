package com.dadhwal.LedController.controller;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import com.dadhwal.LedController.LedSDK.SDKWrapper;
import com.dadhwal.LedController.controller.requests.MessageRequest;
import com.dadhwal.LedController.controller.responses.SearchTerminal;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ApiController {

    private static final Logger logger = Logger.getLogger(ApiController.class.getName());
    private static volatile boolean isProcessingPublishMessage = false;
    private static volatile boolean sdkInitialized = false;
    private static volatile boolean login = false;
    private static final Gson gson = new Gson();

    private static String controllerIp;
    private static String wbFile;
    private static String baseurl;

    private final AppConfig appConfig;

    public ApiController(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @PostConstruct
    public void initialize() {
        System.out.println("Initializing SDK");
        controllerIp = appConfig.getControllerIp();
        wbFile = appConfig.getWbFile();
        baseurl = appConfig.getBaseurl();

        System.out.println("Base URL: " + baseurl);
        System.out.println("Controller IP: " + controllerIp);
        System.out.println("WB File: " + wbFile);

        initializeSDK();
    }

    private void initializeSDK() {
        try {
            if (isPingable(controllerIp)) {
                SDKWrapper.init();
                SDKWrapper.searchTerminalByIp(controllerIp);
                Boolean logined = performLogin();
                if (logined) {
                    login = true;
                    SDKWrapper.createProgramPage();
                    SDKWrapper.setWebPageProgram(baseurl + "/page");
                    SDKWrapper.makeProgram();
                    SDKWrapper.transferProgram();
                }
                sdkInitialized = true;
            } else {
                logger.log(Level.WARNING, "Controller IP is not reachable: " + controllerIp);
            }
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "SDK Initialization interrupted", e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error during SDK initialization", e);
        }
    }

    private boolean isPingable(String ipAddress) {
        try {
            InetAddress inet = InetAddress.getByName(ipAddress);
            return inet.isReachable(3000);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ping check failed for IP: " + ipAddress, e);
            return false;
        }
    }

    @Scheduled(fixedRate = 30000)
    public void checkAndInitializeSDK() {
        if (!isPingable(controllerIp)) {
            login = false;
            return;
        }
        if (!sdkInitialized) {
            logger.log(Level.INFO, "Retrying SDK initialization...");
            initialize();
        }
    }

    private ResponseEntity<String> checkSdkStatus() {
        if (!sdkInitialized) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("SDK not initialized yet.");
        }
        return null;
    }

    private boolean performLogin() {
        try {
            CompletableFuture<String> res = SDKWrapper.login();
            String result = res.get();
            JsonObject response = gson.fromJson(result, JsonObject.class);
            if (response.has("logined") && response.get("logined").getAsBoolean()) {
                login = true;
                return true;
            } else {
                login = false;
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.log(Level.SEVERE, "Login failed", e);
            login = false;
        }
        return false;
    }

    @GetMapping("/sdkStatus")
    public ResponseEntity<String> getSdkStatus() {
        if (sdkInitialized) {
            return ResponseEntity.ok("SDK is initialized.");
        } else {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("SDK not initialized yet.");
        }
    }

    @GetMapping("/updateConfig")
    public ResponseEntity<String> updateConfig(@RequestParam String newIp) {
        controllerIp = newIp;
        initialize(); // re-init SDK with new IP
        return ResponseEntity.ok("IP updated to: " + newIp + " (Note: Not persisted to properties file)");
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Test Passed");
    }

    @GetMapping("/updateWbFile")
    public ResponseEntity<String> updateWbFile(@RequestParam String newPath) {
        wbFile = newPath;
        return ResponseEntity.ok("WbFile path updated to: " + newPath + " (Note: Not persisted to properties file)");
    }

    @GetMapping("/readable")
    public ResponseEntity<String> readFile() {
        try {
            Path filePath = Paths.get(wbFile);
            String fileContent = Files.readString(filePath);
            return ResponseEntity.ok(fileContent);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error reading file: " + e.getMessage());
        }
    }

    @GetMapping("/searchTerminal")
    public ResponseEntity<String> searchTerminal() {
        ResponseEntity<String> sdkStatus = checkSdkStatus();
        if (sdkStatus != null) return sdkStatus;

        try {
            CompletableFuture<String> resultFuture = SDKWrapper.searchTerminal();
            String result = resultFuture.get();
            return ResponseEntity.ok(result);
        } catch (InterruptedException | ExecutionException e) {
            logger.log(Level.SEVERE, "Search Terminal failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Search Terminal failed");
        }
    }

    @GetMapping("/searchScreen")
    public ResponseEntity<String> searchTerminalIp() {
        ResponseEntity<String> sdkStatus = checkSdkStatus();
        if (sdkStatus != null) return sdkStatus;

        try {
            CompletableFuture<String> resultFuture = SDKWrapper.searchTerminalByIp(controllerIp);
            String result = resultFuture.get();
            SearchTerminal response = gson.fromJson(result, SearchTerminal.class);
            if (!response.isLogined()) {
                Boolean logined = performLogin();
                if (logined) login = true;
            }
            return ResponseEntity.ok(result);
        } catch (InterruptedException | ExecutionException e) {
            logger.log(Level.SEVERE, "Search Terminal failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Search Terminal failed");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login() {
        ResponseEntity<String> sdkStatus = checkSdkStatus();
        if (sdkStatus != null) return sdkStatus;

        try {
            Boolean logined = performLogin();
            return logined ? ResponseEntity.ok("Login Successful") : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login Failed");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Login failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login Failed");
        }
    }

    @GetMapping("/login")
    public ResponseEntity<String> getlogin() {
        return login();
    }

    @PostMapping("/publishMessage")
    public ResponseEntity<String> setPublishMessage(@RequestBody MessageRequest messageRequest) throws InterruptedException {
        if (isProcessingPublishMessage) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Previous publish message is still being processed.");
        }

        isProcessingPublishMessage = true;

        if (!login && sdkInitialized) {
            if (performLogin()) login = true;
        }

        ResponseEntity<String> sdkStatus = checkSdkStatus();
        if (sdkStatus != null) return sdkStatus;

        try {
            String info = messageRequest.getInfo();
            String success = messageRequest.getSuccess();
            String error = messageRequest.getError();
            if (info != null) {
                SDKWrapper.setPageProgram(info, "#5bc0de");
            } else if (success != null) {
                SDKWrapper.setPageProgram(success, "#22bb33");
            } else if (error != null) {
                SDKWrapper.setPageProgram(error, "#bb2124");
            } else {
                SDKWrapper.setWebPageProgram(baseurl + "/page");
            }

            SDKWrapper.makeProgram();
            CompletableFuture<String> res = SDKWrapper.transferProgram();
            return ResponseEntity.ok(res.get());
        } catch (InterruptedException | ExecutionException e) {
            logger.log(Level.SEVERE, "Error Publishing Message", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Set Page Program failed");
        } finally {
            isProcessingPublishMessage = false;
        }
    }

    @GetMapping("/publishMessage")
    public ResponseEntity<String> setPublishMessage(
            @RequestParam(required = false) String info,
            @RequestParam(required = false) String success,
            @RequestParam(required = false) String error) throws InterruptedException {

        if (isProcessingPublishMessage) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Previous publish message is still being processed.");
        }

        isProcessingPublishMessage = true;

        if (!login && sdkInitialized) {
            if (!performLogin()) {
                isProcessingPublishMessage = false;
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Initial login failed.");
            }
        }

        ResponseEntity<String> sdkStatus = checkSdkStatus();
        if (sdkStatus != null) {
            isProcessingPublishMessage = false;
            return sdkStatus;
        }

        int retryCount = 3;
        int attempt = 0;

        try {
            while (attempt < retryCount) {
                try {
                    SDKWrapper.createProgramPage();

                    if (info != null) {
                        SDKWrapper.setPageProgram(info, "#5bc0de");
                    } else if (success != null) {
                        SDKWrapper.setPageProgram(success, "#22bb33");
                    } else if (error != null) {
                        SDKWrapper.setPageProgram(error, "#bb2124");
                    } else {
                        SDKWrapper.setWebPageProgram(baseurl + "/page");
                    }

                    SDKWrapper.makeProgram();
                    CompletableFuture<String> res = SDKWrapper.transferProgram();
                    return ResponseEntity.ok(res.get());

                } catch (InterruptedException | ExecutionException e) {
                    attempt++;
                    logger.log(Level.SEVERE, "Error Publishing Message, Attempt " + attempt, e);

                    if (attempt == retryCount) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Set Page Program failed after retries.");
                    }

                    performLogin();
                    Thread.sleep(2000);
                }
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred.");
        } finally {
            isProcessingPublishMessage = false;
        }
    }

    @GetMapping("/getProgramInfo")
    public ResponseEntity<String> getProgramInfo() {
        ResponseEntity<String> sdkStatus = checkSdkStatus();
        if (sdkStatus != null) return sdkStatus;

        try {
            CompletableFuture<String> res = SDKWrapper.getProgramInfo();
            return ResponseEntity.ok(res.get());
        } catch (InterruptedException | ExecutionException e) {
            logger.log(Level.SEVERE, "Get Program Info failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Get Program Info failed");
        }
    }

    @PostMapping("/setVolumeData")
    public ResponseEntity<String> setVolume() {
        ResponseEntity<String> sdkStatus = checkSdkStatus();
        if (sdkStatus != null) return sdkStatus;

        try {
            CompletableFuture<String> res = SDKWrapper.setVolume();
            return ResponseEntity.ok(res.get());
        } catch (InterruptedException | ExecutionException e) {
            logger.log(Level.SEVERE, "Set Volume failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Set Volume failed");
        }
    }

    @GetMapping("/getVolume")
    public ResponseEntity<String> getVolume() {
        ResponseEntity<String> sdkStatus = checkSdkStatus();
        if (sdkStatus != null) return sdkStatus;

        try {
            CompletableFuture<String> res = SDKWrapper.getVolume();
            return ResponseEntity.ok(res.get());
        } catch (InterruptedException | ExecutionException e) {
            logger.log(Level.SEVERE, "Get Volume failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Get Volume failed");
        }
    }

    @GetMapping("/getEthernetInfo")
    public ResponseEntity<String> getEthernetInfo() {
        ResponseEntity<String> sdkStatus = checkSdkStatus();
        if (sdkStatus != null) return sdkStatus;

        try {
            CompletableFuture<String> res = SDKWrapper.getEthernetInfo();
            return ResponseEntity.ok(res.get());
        } catch (InterruptedException | ExecutionException e) {
            logger.log(Level.SEVERE, "Get Ethernet Info failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Get Ethernet Info failed");
        }
    }

    @GetMapping("/setEthernetInfo")
    public ResponseEntity<String> setEthernetInfo(@RequestParam String newIp) {
        ResponseEntity<String> sdkStatus = checkSdkStatus();
        if (sdkStatus != null) return sdkStatus;

        try {
            CompletableFuture<String> res = SDKWrapper.setEthernetInfo(newIp);
            return ResponseEntity.ok(res.get());
        } catch (InterruptedException | ExecutionException e) {
            logger.log(Level.SEVERE, "Set Ethernet Info failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Set Ethernet Info failed");
        }
    }

    @PostMapping("/setScreenInfo")
    public ResponseEntity<String> setScreenInfo() {
        ResponseEntity<String> sdkStatus = checkSdkStatus();
        if (sdkStatus != null) return sdkStatus;

        try {
            CompletableFuture<String> res = SDKWrapper.setScreenInfo();
            return ResponseEntity.ok(res.get());
        } catch (InterruptedException | ExecutionException e) {
            logger.log(Level.SEVERE, "Set Screen Info Failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Set Screen Info Failed");
        }
    }
}
