package com.dadhwal.LedController.controller;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.InetAddress;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.scheduling.annotation.Scheduled;

import com.dadhwal.LedController.LedSDK.SDKWrapper;
import com.dadhwal.LedController.controller.requests.MessageRequest;

@RestController
@RequestMapping("/api")
public class ApiController {

    private static final Logger logger = Logger.getLogger(ApiController.class.getName());
    private static final String CONFIG_FILE = "config.properties";
    private static String controllerIp;
    private static boolean sdkInitialized = false;

    static {
        loadConfiguration();
        initializeSDK();
    }

    private static void loadConfiguration() {
        Properties properties = new Properties();
        // Load the configuration file from the resources folder using the class loader
        try (InputStream input = ApiController.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                logger.log(Level.SEVERE, "Configuration file not found in resources");
                controllerIp = "192.168.1.50"; // Default value
            } else {
                properties.load(input);
                controllerIp = properties.getProperty("controller.ip", "192.168.1.50"); // Default value
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load configuration", e);
            controllerIp = "192.168.1.50"; // Default value
        }
    }

    private static void initializeSDK() {
        if (!sdkInitialized) {
            if (isPingable(controllerIp)) {
                try {
                    SDKWrapper.init();
                    SDKWrapper.searchTerminal();
                    SDKWrapper.login();
                    sdkInitialized = true;
                } catch (InterruptedException e) {
                    logger.log(Level.SEVERE, "SDK Initialization failed", e);
                    throw new RuntimeException("SDK Initialization failed", e);
                }
            } else {
                logger.log(Level.SEVERE, "SDK IP is not reachable: " + controllerIp);
            }
        } else {
            logger.log(Level.INFO, "SDK already initialized.");
        }
    }

    private static boolean isPingable(String ipAddress) {
        try {
            InetAddress inet = InetAddress.getByName(ipAddress);
            return inet.isReachable(3000); // 3-second timeout
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ping check failed for IP: " + ipAddress, e);
            return false;
        }
    }

    // Scheduled task to check the IP every 5 minutes and initialize the SDK if reachable
    @Scheduled(fixedRate = 300000) // 5 minutes in milliseconds
    public void checkSdkInitialization() {
        if (!sdkInitialized && isPingable(controllerIp)) {
            try {
                initializeSDK();
                logger.log(Level.INFO, "SDK Initialized successfully after IP became reachable.");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error initializing SDK", e);
            }
        }
    }

    private ResponseEntity<String> checkSdkStatus() {
        if (!sdkInitialized) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Screen is not reachable. SDK not initialized.");
        }
        return null;
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
            String result = resultFuture.get(); // This will block until the future completes
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
            CompletableFuture<String> res = SDKWrapper.login();
            return ResponseEntity.ok(res.get());
        } catch (InterruptedException | ExecutionException e) {
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
    public ResponseEntity<String> setPublishMessage(@RequestBody MessageRequest messageRequest) {
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
