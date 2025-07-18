package com.dadhwal.LedController.services;

import com.dadhwal.LedController.LedSDK.SDKWrapper;
import com.dadhwal.LedController.config.Config;
import com.dadhwal.LedController.controller.requests.EthernetConfig;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class SdkService {

    private static final Logger logger = Logger.getLogger(SdkService.class.getName());

    private static final String CONFIG_FILE = "config.properties";
    private static String controllerIp;
    private static String WbFile;
    private static String baseUrl;
    private final Config config;

    private static final Gson gson = new Gson();
    private static volatile boolean sdkInitialized = false;
    private static volatile boolean login = false;

    public SdkService(Config config) {
        this.config = config;
    }

    public boolean isSdkInitialized() {
        return sdkInitialized;
    }

    public boolean isLogin() {
        return login;
    }

    public String getWbFile() {
        return WbFile;
    }

    public void setWbFile(String wbFile) {
        WbFile = wbFile;
    }

    @PostConstruct
    public void initialize() {
        loadConfiguration();
        initializeSDK();
    }

    private void loadConfiguration() {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("src/main/resources/" + CONFIG_FILE)) {
            properties.load(input);
            controllerIp = properties.getProperty("controllerIp");
            WbFile = properties.getProperty("wbFile");
            baseUrl = properties.getProperty("baseUrl");
            config.setControllerIp(controllerIp);
            config.setWbFile(WbFile);
            config.setBaseUrl(baseUrl);
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
                if (logined) {
                    login = true;
                    SDKWrapper.createProgramPage();
                    SDKWrapper.setWebPageProgram(baseUrl + "/page");
                    SDKWrapper.makeProgram();
                    SDKWrapper.transferProgram();
                }
                sdkInitialized = true;
            } else {
                logger.log(Level.WARNING, "Controller IP is not reachable: " + controllerIp);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "SDK initialization failed", e);
        }
    }

    @Scheduled(fixedRate = 30000)
    public void checkAndInitializeSDK() {
        if (!isPingable(controllerIp)) {
            login = false;
            return;
        }
        if (!sdkInitialized) {
            initialize();
        }
    }

    public boolean isControllerIpPingable() {
        if (controllerIp == null || controllerIp.isBlank()) {
            logger.warning("Controller IP is not set.");
            return false;
        }
        return isPingable(controllerIp);
    }

    private boolean isPingable(String ipAddress) {
        try {
            InetAddress inet = InetAddress.getByName(ipAddress);
            return inet.isReachable(3000);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ping check failed", e);
            return false;
        }
    }

    public boolean performLogin() {
        try {
            CompletableFuture<String> res = SDKWrapper.loginByIp(controllerIp);
            String result = res.get();
            JsonObject response = gson.fromJson(result, JsonObject.class);

            if (response.has("logined") && response.get("logined").getAsBoolean()) {
                login = true;
                return true;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Login failed", e);
        }
        login = false;
        return false;
    }

    public String readWbFileContent() throws IOException {
    if (WbFile == null || WbFile.isBlank()) {
        return "Invalid Path";
    }

    Path filePath = Paths.get(WbFile);

    if (!Files.exists(filePath)) {
        return "Invalid Path";
    }

    return Files.readString(filePath);
}


    public CompletableFuture<String> publishMessage(String type, String color)
            throws ExecutionException, InterruptedException {
        SDKWrapper.createProgramPage();
        SDKWrapper.setPageProgram(type, color);
        SDKWrapper.makeProgram();
        return SDKWrapper.transferProgram();
    }

    public CompletableFuture<String> transferWebProgram() throws ExecutionException, InterruptedException {
        SDKWrapper.setWebPageProgram(baseUrl + "/page");
        SDKWrapper.makeProgram();
        return SDKWrapper.transferProgram();
    }

    public CompletableFuture<String> searchTerminal() throws InterruptedException {
        return SDKWrapper.searchTerminal();
    }

    public CompletableFuture<String> searchTerminalByIp() throws InterruptedException {
        return SDKWrapper.searchTerminalByIp(controllerIp);
    }

    public CompletableFuture<String> getProgramInfo() throws InterruptedException {
        return SDKWrapper.getProgramInfo();
    }

    public CompletableFuture<String> setVolume() throws InterruptedException {
        return SDKWrapper.setVolume();
    }

    public CompletableFuture<String> getVolume() throws InterruptedException {
        return SDKWrapper.getVolume();
    }

    public CompletableFuture<String> getEthernetInfo() throws InterruptedException {
        return SDKWrapper.getEthernetInfo();
    }

    public CompletableFuture<String> setEthernetInfo(EthernetConfig config) throws InterruptedException {
        return SDKWrapper.setEthernetInfo(config);
    }

    public CompletableFuture<String> setScreenInfo() throws InterruptedException {
        return SDKWrapper.setScreenInfo();
    }

    public void updateConfig(String newIp, String newWbFile, String newBaseUrl) throws IOException {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("src/main/resources/" + CONFIG_FILE)) {
            properties.load(input);
        }

        if (newIp != null && !newIp.isEmpty()) {
            properties.setProperty("controllerIp", newIp);
            config.setControllerIp(newIp);
            controllerIp = newIp;
        }
        if (newWbFile != null && !newWbFile.isEmpty()) {
            properties.setProperty("wbFile", newWbFile);
            config.setWbFile(newWbFile);
            WbFile = newWbFile;
        }
        if (newBaseUrl != null && !newBaseUrl.isEmpty()) {
            properties.setProperty("baseUrl", newBaseUrl);
            config.setBaseUrl(newBaseUrl);
            baseUrl = newBaseUrl;
        }

        try (FileOutputStream output = new FileOutputStream("src/main/resources/" + CONFIG_FILE)) {
            properties.store(output, null);
        }
    }

    public Config readConfig() {
        return config;
    }
}
