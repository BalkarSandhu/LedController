package com.dadhwal.sdk;

import com.sun.jna.Native;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.CompletableFuture;
import com.google.gson.Gson;

public class SDKWrapper {
    static boolean g_bAPIReturn = false;
    static int g_code = 0;
    static String g_sn = "2YKA04124N1A10000710"; //BZSA07313J0350000997

    static ViplexCore instance;
    static ViplexCore.CallBack callBack;
    private static final Logger logger = Logger.getLogger(SDKWrapper.class.getName());

    static void waitAPIReturn(CountDownLatch latch) throws InterruptedException {
        latch.await();
        g_bAPIReturn = false;
    }

    public static void init() {
        try {
            System.setProperty("jna.encoding", "UTF-8");
            String rootDir = System.getProperty("user.dir") + "/temp";
            rootDir = rootDir.replaceAll("\\\\", "/");

            instance = (ViplexCore) Native.loadLibrary("src\\main\\resources\\SDK\\bin\\viplexcore.dll", ViplexCore.class);
            callBack = (code, data) -> {
                g_code = code;
                logger.log(Level.INFO, "Response: {0}\nData: {1}", new Object[]{code, data});
                g_bAPIReturn = true;

            };

            String companyInfo = "{\"company\":\"NovaStar\",\"phone\":\"029-68216000\",\"email\":\"hr@novastar.tech\"}";
            instance.nvSetDevLang("Java");
            logger.log(Level.INFO, "Initializing SDK");
            int initResult = instance.nvInit(rootDir, companyInfo);
            logger.log(Level.INFO, "Initialization result: {0}", initResult);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Initialization failed", e);
        }
    }

    public static CompletableFuture<String> searchTerminal() throws InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();
        try {
            CountDownLatch latch = new CountDownLatch(1);
            instance.nvSearchTerminalAsync((code, data) -> {
                if (code == 0) {
                    future.complete(data); // Complete future with data
                } else {
                    future.completeExceptionally(new Exception("Search terminal failed")); // Complete with exception
                }
                latch.countDown();
            });
            waitAPIReturn(latch);
        } catch (InterruptedException e){
            future.completeExceptionally(e);
        }
        return future;
    }

    public static CompletableFuture<String> login() throws InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();
        try {
            String loginParam = String.format("{\"sn\":\"%s\",\"username\":\"admin\",\"rememberPwd\":1,\"password\":\"SN2008@+\",\"loginType\":0}", g_sn);
            CountDownLatch latch = new CountDownLatch(1);
            instance.nvLoginAsync(loginParam, (code, data) -> {
                if (code == 0) {
                    future.complete(data);
                } else {
                    future.completeExceptionally(new Exception("Login Failed"));
                }
                latch.countDown();
            });
            waitAPIReturn(latch);
        } catch (InterruptedException e){
            future.completeExceptionally(e);
        }
        return future;
    }

    public static CompletableFuture<String> createProgram() throws InterruptedException {
        CompletableFuture<String> future =  new CompletableFuture<>();
        try {
            String createProgram = "{\"name\":\"Demo\",\"width\":100,\"height\":100,\"tplID\":1,\"winInfo\":{\"height\":50,\"width\":50,\"left\":0,\"top\":0,\"zindex\":0,\"index\":0}}";
            logger.log(Level.INFO, "Create Program");
            CountDownLatch latch = new CountDownLatch(1);
            instance.nvCreateProgramAsync(createProgram, (code, data) -> {
                if(code==0){
                    future.complete(data);
                } else {
                    future.completeExceptionally(new Exception("Create Program Failed"));
                }
                latch.countDown();
            });
            waitAPIReturn(latch);
        } catch (InterruptedException e){
            future.completeExceptionally(e);
        }
        return future;
    }

    public static CompletableFuture<String> getFileMD5() throws InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();
        try {
            String requestDatapath = "{\"filePath\":\"./test.png\"}";
            logger.log(Level.INFO, "Get File MD5");
            CountDownLatch latch = new CountDownLatch(1);
            instance.nvGetFileMD5Async(requestDatapath, (code, data) -> {
                if(code==0){
                    future.complete(data);
                } else {
                    future.completeExceptionally(new Exception("Get File MD5 Failed"));
                }
                latch.countDown();
            });
            waitAPIReturn(latch);
        } catch (InterruptedException e){
            future.completeExceptionally(e);
        }
        return future;
    }

    public static CompletableFuture<String> setPageProgram(String s) throws InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();
        try {
            String editProgram = String.format("{\"programID\":1,\"pageID\":1,\"pageInfo\":{\"name\":\"jiemu\",\"widgetContainers\":[{\"contents\":{\"widgets\":[{\"constraints\":[{\"cron\":[\"0 0 0 ? * 1,2,3,4,5,6,7\"],\"endTime\":\"4017-12-30T23:59:59Z+8:00\",\"startTime\":\"1970-01-01T00:00:00Z+8:00\"}],\"duration\":15000,\"metadata\":{\"content\":{\"autoPaging\":true,\"backgroundMusic\":{\"duration\":0,\"isTextSync\":false},\"displayStyle\":{\"scrollAttributes\":{\"effects\":{\"animation\":\"MARQUEE_LEFT\",\"speed\":1}},\"type\":\"SCROLL\"},\"paragraphs\":[{\"backgroundColor\":\"#00000000\",\"horizontalAlignment\":\"CENTER\",\"letterSpacing\":0,\"lineSpacing\":0,\"lines\":[{\"segs\":[{\"content\":\"%s\"}]}],\"verticalAlignment\":\"TOP\"}],\"textAttributes\":[{\"backgroundColor\":\"#ff000000\",\"attributes\":{\"font\":{\"family\":[\"Arial\"],\"isUnderline\":false,\"size\":20,\"style\":\"NORMAL\"},\"letterSpacing\":0,\"textColor\":\"#ffff0000\"}}]}},\"name\":\"text\",\"type\":\"ARCH_TEXT\"}]},\"id\":1,\"name\":\"widgetContainers1\"}]}}",s);
            logger.log(Level.INFO, "Set Page Program");
            CountDownLatch latch = new CountDownLatch(1);
            instance.nvSetPageProgramAsync(editProgram, (code, data) -> {
                if(code==0){
                    future.complete(data);
                } else {
                    future.completeExceptionally(new Exception("Set Page Program Failed"));
                }
                latch.countDown();
            });
            waitAPIReturn(latch);
        } catch (InterruptedException e){
            future.completeExceptionally(e);
        }
        return future;
    }

    public static CompletableFuture<String> makeProgram() throws InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();
        try {
            String rootDir = System.getProperty("user.dir") + "/temp";
            rootDir = rootDir.replaceAll("\\\\", "/");

            String genrateProgram = String.format("{\"programID\":1,\"outPutPath\":\"%s/\"}", rootDir);
            logger.log(Level.INFO, "ViplexCore Demo nvMakeProgramAsync(生成节目) begin...");
            CountDownLatch latch = new CountDownLatch(1);
            instance.nvMakeProgramAsync(genrateProgram, (code, data) -> {
                if(code == 0){
                    future.complete(data);
                } else {
                    future.completeExceptionally(new Exception("Make Program Failed"));
                }
                latch.countDown();
            });
            waitAPIReturn(latch);
        } catch (InterruptedException e){
            future.completeExceptionally(e);
        }
        return future;
    }

    public static CompletableFuture<String> transferProgram() throws InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();
        try {
            String rootDir = System.getProperty("user.dir") + "/temp";
            rootDir = rootDir.replaceAll("\\\\", "/");

            String transferProgram = String.format("{\"sn\": \"%s\",\"iconPath\": \"\",\"iconName\": \"\",\"sendProgramFilePaths\": {\"programPath\": \"%s/program1\",\"mediasPath\": {}},\"programName\": \"program1\",\"deviceIdentifier\": \"Demo\",\"startPlayAfterTransferred\": true,\"insertPlay\": true}", g_sn, rootDir);
            logger.log(Level.INFO, "Start Transfer Program Begin...");
            CountDownLatch latch = new CountDownLatch(1);
            instance.nvStartTransferProgramAsync(transferProgram, (code, data) -> {
                if(code == 0){
                    future.complete(data);
                } else {
                    future.completeExceptionally(new Exception("Transfer Program Failed"));
                }
                latch.countDown();
            });
            Thread.sleep(10000); // Simulate longer operation
            g_bAPIReturn = false;
        } catch (InterruptedException e){
            future.completeExceptionally(e);
        }
        return future;
            
    }

    public static CompletableFuture<String> getProgramInfo() throws InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();
        try {
            logger.log(Level.INFO, "Get Program Info");
            String requestDatasn = String.format("{\"sn\":\"%s\"}", g_sn);
            CountDownLatch latch = new CountDownLatch(1);
            instance.nvGetProgramInfoAsync(requestDatasn, (code, data) -> {
                if(code == 0){
                    future.complete(data);
                } else {
                    future.completeExceptionally(new Exception("Get Program Info Failed"));
                }
                latch.countDown();
            });
            waitAPIReturn(latch);
        } catch (InterruptedException e){
            future.completeExceptionally(e);
        }
        return future;
    }

    public static CompletableFuture<String> setVolume() throws InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();
        try {
            String setVolumeParam = String.format("{\"sn\":\"%s\",\"volumeInfo\":{\"ratio\":60.0}}", g_sn);
            CountDownLatch latch = new CountDownLatch(1);
            instance.nvSetVolumeAsync(setVolumeParam, (code, data) -> {
                if(code == 0){
                    future.complete(data);
                } else {
                    future.completeExceptionally(new Exception("Set Volume Failed"));
                }
                latch.countDown();
            });
            waitAPIReturn(latch);
        } catch (InterruptedException e){
            future.completeExceptionally(e);
        }
        return future;
    }

    public static CompletableFuture<String> getVolume() throws InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();
        try {
            String getVolumeParam = String.format("{\"sn\":\"%s\"}", g_sn);
            CountDownLatch latch = new CountDownLatch(1);
            instance.nvGetVolumeAsync(getVolumeParam, (code, data) -> {
                if(code == 0){
                    future.complete(data);
                } else {
                    future.completeExceptionally(new Exception("Get Volume Failed"));
                }
                latch.countDown();
            });
            waitAPIReturn(latch);
        } catch (InterruptedException e){
            future.completeExceptionally(e);
        }
        return future;
    }

    public static CompletableFuture<String> getEthernetInfo() throws InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();
        try {
            SerialNumber serialNumber = new SerialNumber(g_sn);
            Gson gson = new Gson();
            String getEthernetParam = gson.toJson(serialNumber);
            logger.log(Level.INFO,getEthernetParam);
            CountDownLatch latch = new CountDownLatch(1);
            instance.nvGetEthernetInfoAsync(getEthernetParam, (code, data) -> {
                if(code == 0){
                    future.complete(data);
                } else {
                    future.completeExceptionally(new Exception("Get Ethernet Info Failed"));
                }
                latch.countDown();
            });
            waitAPIReturn(latch);
        } catch (InterruptedException e){
            future.completeExceptionally(e);
        }
        return future;
    }

    public static CompletableFuture<String> setEthernetInfo() throws InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();
        try {
            logger.log(Level.INFO, "Setting Ethernet Info setEthernetInfoAsync(data, callBack)");
            String data = String.format("{\"sn\":\"%s\",\"taskInfo\":{\"ethernets\":[{\"scopeId\":-1,\"name\":\"eth0\",\"dhcp\":false,\"ip\":\"192.168.1.20\",\"mask\":\"255.255.255.0\",\"gateWay\":\"192.168.1.1\",\"dns\":[\"8.8.8.8\"]}]}}", g_sn);
            CountDownLatch latch = new CountDownLatch(1);
            instance.nvSetEthernetInfoAsync(data, (code, result) -> {
                if(code == 0){
                    future.complete(result);
                } else {
                    future.completeExceptionally(new Exception("Set Ethernet Info Failed"));
                }
                latch.countDown();
            });
            waitAPIReturn(latch);
        } catch (InterruptedException e){
            future.completeExceptionally(e);
        }
        return future;
    }

}
