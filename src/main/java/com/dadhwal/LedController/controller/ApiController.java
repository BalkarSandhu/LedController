package com.dadhwal.LedController.controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dadhwal.LedController.LedSDK.SDKWrapper;
import com.dadhwal.LedController.controller.requests.MessageRequest;
import com.dadhwal.LedController.controller.responses.SearchTerminal;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("/api")
public class ApiController {

    private static final Logger logger = Logger.getLogger(ApiController.class.getName());
    private static final String CONFIG_FILE = "config.properties";
    private static String controllerIp; // Default IP
    private static volatile boolean sdkInitialized = false;
    private static volatile boolean login = false;
    private static final Gson gson=new Gson();

    @PostConstruct
    public void initialize() {
        loadConfiguration();
        initializeSDK();
    }

    private void loadConfiguration() {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("src/main/resources/" + CONFIG_FILE)) {
            if (input == null) {
                logger.log(Level.WARNING, "Configuration file not found. Using default IP: " + controllerIp);
            } else {
                properties.load(input);
                controllerIp = properties.getProperty("controller.ip", controllerIp);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load configuration", e);
        }
    }

    private void initializeSDK() {
        try {
            if (isPingable(controllerIp)) {
                SDKWrapper.init();
                SDKWrapper.searchTerminalByIp(controllerIp);
                Boolean logined = performLogin();
                if(logined) {
                    login=true;
                    SDKWrapper.createProgramPage();
                    SDKWrapper.setPageProgram("BCCL", "#5bc0de");
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
            return inet.isReachable(3000); // 3-second timeout
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ping check failed for IP: " + ipAddress, e);
            return false;
        }
    }

    @Scheduled(fixedRate = 30000) // Retry every 30 Second
    public void checkAndInitializeSDK() {
        if(!isPingable(controllerIp)){
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
            String result = res.get(); // Block until the login result is available

            // Parse the JSON response
            JsonObject response = gson.fromJson(result, JsonObject.class);

            // Check if the "logined" field is true
            if (response.has("logined") && response.get("logined").getAsBoolean()) {
                login = true; // Set the login state to true
                return true; // Login was successful
            } else {
                login = false; // Explicitly set to false if "logined" is not true
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.log(Level.SEVERE, "Login failed", e);
            login = false; // Ensure login is set to false in case of failure
        }
        return false; // Login was not successful
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
        Properties properties = new Properties();
        // Load the configuration file from the resources folder using the class loader
        try (InputStream input = ApiController.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                logger.log(Level.SEVERE, "Configuration file not found in resources");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Configuration file not found");
            }
            properties.load(input);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to read configuration file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to read configuration file");
        }

        // Update the IP in the properties
        try (FileOutputStream output = new FileOutputStream("src/main/resources/" + CONFIG_FILE)) {
            properties.setProperty("controller.ip", newIp);
            properties.store(output, null);
            controllerIp = newIp;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to update configuration file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update configuration file");
        }

        checkAndInitializeSDK();
        return ResponseEntity.ok("Configuration updated successfully to IP: " + newIp);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Test Passed");
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
            String result = resultFuture.get(); // This will block until the future completes
            SearchTerminal response = gson.fromJson(result, SearchTerminal.class);
            if (!response.isLogined()){
                Boolean logined = performLogin();
                if(logined) {
                    login=true;
                }
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
            if(logined) {
                return ResponseEntity.ok("Login Successful");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login Failed");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Login failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login Failed");
        }
    }

    @GetMapping("/login")
    public ResponseEntity<String> getlogin() {
        ResponseEntity<String> sdkStatus = checkSdkStatus();
        if (sdkStatus != null) return sdkStatus;

        try {
            Boolean logined = performLogin();
            if(logined) {
                return ResponseEntity.ok("Login Successful");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login Failed");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Login failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login Failed");
        }
    }

    @PostMapping("/createProgram")
    public ResponseEntity<String> createProgram() {
        ResponseEntity<String> sdkStatus = checkSdkStatus();
        if (sdkStatus != null) return sdkStatus;

        try {
            CompletableFuture<String> res = SDKWrapper.createProgramPage();
            return ResponseEntity.ok(res.get());
        } catch (InterruptedException | ExecutionException e) {
            logger.log(Level.SEVERE, "Create Program failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Create Program failed");
        }
    }

    @PostMapping("/setPageProgram")
    public ResponseEntity<String> setPageProgram(@RequestParam String message) {
        ResponseEntity<String> sdkStatus = checkSdkStatus();
        if (sdkStatus != null) return sdkStatus;

        try {
            CompletableFuture<String> res = SDKWrapper.setPageProgram(message, "#339CFF");
            return ResponseEntity.ok(res.get());
        } catch (InterruptedException | ExecutionException e) {
            logger.log(Level.SEVERE, "Set Page Program failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Set Page Program failed");
        }
    }

    @GetMapping("/makeProgram")
    public ResponseEntity<String> makeProgram() {
        ResponseEntity<String> sdkStatus = checkSdkStatus();
        if (sdkStatus != null) return sdkStatus;

        try {
            CompletableFuture<String> res = SDKWrapper.makeProgram();
            return ResponseEntity.ok(res.get());
        } catch (InterruptedException | ExecutionException e) {
            logger.log(Level.SEVERE, "Make Program failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Make Program failed");
        }
    }

    @PostMapping("/transferProgram")
    public ResponseEntity<String> transferProgram() {
        ResponseEntity<String> sdkStatus = checkSdkStatus();
        if (sdkStatus != null) return sdkStatus;

        try {
            CompletableFuture<String> res = SDKWrapper.transferProgram();
            return ResponseEntity.ok(res.get());
        } catch (InterruptedException | ExecutionException e) {
            logger.log(Level.SEVERE, "Transfer Program failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Transfer Program failed");
        }
    }

     @PostMapping("/publishMessage")
     public ResponseEntity<String> setPublishMessage(@RequestBody MessageRequest messageRequest) throws InterruptedException {
        if(!login && sdkInitialized) {
            searchTerminalIp();
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
             } else {
                 SDKWrapper.setPageProgram(error, "#bb2124");
             }

             SDKWrapper.makeProgram();
             CompletableFuture<String> res = SDKWrapper.transferProgram();
             return ResponseEntity.ok(res.get());
         } catch (InterruptedException | ExecutionException e) {
             logger.log(Level.SEVERE, "Error Publishing Message", e);
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Set Page Program failed");
         }
     }

   
    @GetMapping("/publishMessage")
    public ResponseEntity<String> setPublishMessage(
        @RequestParam(required = false) String info,
        @RequestParam(required = false) String success,
        @RequestParam(required = false) String error) {
        if(!login && sdkInitialized) {
            Boolean logined = performLogin();
            if(logined) {
                login=true;
            }
        }

        ResponseEntity<String> sdkStatus = checkSdkStatus();
        if (sdkStatus != null) return sdkStatus;

        try {
            SDKWrapper.createProgramPage();
            // Check which parameter is provided and set the page program accordingly
            if (info != null) {
                SDKWrapper.setPageProgram(info, "#5bc0de");
            } else if (success != null) {
                SDKWrapper.setPageProgram(success, "#22bb33");
            } else if (error != null) {
                SDKWrapper.setPageProgram(error, "#bb2124");
            }

            SDKWrapper.makeProgram();
            CompletableFuture<String> res = SDKWrapper.transferProgram();
            return ResponseEntity.ok(res.get());
        } catch (InterruptedException | ExecutionException e) {
            logger.log(Level.SEVERE, "Error Publishing Message", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Set Page Program failed");
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
