package com.dadhwal.LedController.LedSDK;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dadhwal.LedController.LedSDK.Editor.Components.*;
import com.dadhwal.LedController.LedSDK.Editor.PageInfo;
import com.dadhwal.LedController.LedSDK.Editor.ProgramData;
import com.dadhwal.LedController.LedSDK.Ethernet.EthernetData;
import com.dadhwal.LedController.LedSDK.Ethernet.EthernetTaskInfo;
import com.dadhwal.LedController.LedSDK.Program.ProgramOutput;
import com.dadhwal.LedController.LedSDK.Program.ProgramPage;
import com.dadhwal.LedController.LedSDK.Program.ProgramTransfer;
import com.dadhwal.LedController.LedSDK.Program.ProgramWindow;
import com.dadhwal.LedController.LedSDK.Screen.ScreenInfo;
import com.dadhwal.LedController.controller.requests.EthernetConfig;
import com.dadhwal.LedController.controller.responses.SearchTerminal;
import com.google.gson.Gson;
import com.sun.jna.Native;

public class SDKWrapper {
    static boolean g_bAPIReturn = false;
    // static String g_sn = "2YKA04124N1A10000710";
    static String g_sn = "";
    static String ROOTDIR = (System.getProperty("user.dir") + "/temp").replaceAll("\\\\", "/");

    static ViplexCore instance;
    private static final Logger logger = Logger.getLogger(SDKWrapper.class.getName());
    static String serialNumber;
    private static final Gson gson=new Gson();

    static void waitAPIReturn(CountDownLatch latch) throws InterruptedException {
        latch.await();
        g_bAPIReturn = false;
    }

    public static void init() {
        try {
            System.setProperty("jna.encoding", "UTF-8");

            String dllPath = System.getProperty("user.dir") + "/src/main/resources/SDK/bin/viplexcore.dll";
            dllPath = dllPath.replace("/", "\\");

            // Debug the DLL path
            System.out.println("Loading DLL from: " + dllPath);


            instance = Native.loadLibrary(dllPath, ViplexCore.class);
//            instance = Native.load("src\\main\\resources\\SDK\\bin\\viplexcore.dll", ViplexCore.class);
            String companyInfo = gson.toJson(new CompanyInfo("FleetSafe India","9992391581","balkar@fleetsafeindia.com"));
            instance.nvSetDevLang("Java");
            instance.nvInit(ROOTDIR, companyInfo);
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
                    SearchTerminal response = gson.fromJson(data, SearchTerminal.class);
                    String serialNumber = response.getSn();
                    if (serialNumber.length() > 10 &&  (response.getProductName()).equals("TCC70")) {
                        System.out.println("Terminal is ready");
                        System.out.println("Terminal SN: " + serialNumber);
                        g_sn = serialNumber; 
                    }
                    logger.log(Level.INFO, "Updated serial number: {0}", g_sn);
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

    public static CompletableFuture<String> searchTerminalByIp(String ip) throws InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();
        try {
            String searchParam = gson.toJson(new SearchTerminalip(ip));
            CountDownLatch latch = new CountDownLatch(1);
            instance.nvSearchAppointIpAsync(searchParam, (code, data) -> {
                if (code == 0) {
                    SearchTerminal response = gson.fromJson(data, SearchTerminal.class);
                    String serialNumber = response.getSn();
                    if (serialNumber.length() > 10 &&  (response.getProductName()).equals("TCC70")) {
                        System.out.println("Terminal is ready");
                        System.out.println("Terminal SN: " + serialNumber);
                        g_sn = serialNumber;
                    }
                    logger.log(Level.INFO, "Updated serial number: {0}", g_sn);
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
            String loginParam = gson.toJson(new LoginReq(g_sn,"admin","SN2008@+", 0,1));
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

    public static CompletableFuture<String> createProgramPage() throws InterruptedException {
        CompletableFuture<String> future =  new CompletableFuture<>();
        try {
            ProgramWindow winInfo=new ProgramWindow(50,50,0,0,0,0);
            String programWindow = gson.toJson(new ProgramPage("Program1", 100, 100, 1, winInfo));
            CountDownLatch latch = new CountDownLatch(1);
            instance.nvCreateProgramAsync(programWindow, (code, data) -> {
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

    public static CompletableFuture<String> setPageProgram(String text, String textColor) throws InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();
        try {
            Constraint constraint=new Constraint(List.of("0 0 0 ? * 1,2,3,4,5,6,7"),"4017-12-30T23:59:59Z+8:00","1970-01-01T00:00:00Z+8:00");
            PageInfo pageInfo = getPageInfo(text, textColor, constraint);
            String editProgram= gson.toJson(new ProgramData(1,1,pageInfo));

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

    public static CompletableFuture<String> setWebPageProgram(String url) throws InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();
        try {
            Constraint constraint=new Constraint(List.of("0 0 0 ? * 1,2,3,4,5,6,7"),"4017-12-30T23:59:59Z+8:00","1970-01-01T00:00:00Z+8:00");
            WebpageInfo pageInfo = getWebPageInfo(url, constraint);
            String editProgram= gson.toJson(new WebPageProgramData(1,1,pageInfo));

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

    private static PageInfo getPageInfo(String text, String textColor, Constraint constraint) {
        BackgroundMusic backgroundMusic=new BackgroundMusic(0,false);
        Content content = getContent(text, textColor, backgroundMusic);
        MetaData metaData=new MetaData(content);
        Widget widget=new Widget(List.of(constraint),15000, metaData,"webpage","ARCH_TEXT");
        Contents contents=new Contents(List.of(widget));
        WidgetContainer widgetContainers=new WidgetContainer(contents,1 , "widgetContainers1");
        return new PageInfo("textWindow",List.of(widgetContainers));
    }

    private static WebpageInfo getWebPageInfo(String url, Constraint constraint) {
        WebpageWidget widget=new WebpageWidget(List.of(constraint),15000, url, "", "text","HTML", true);
        WebpageContents contents=new WebpageContents(List.of(widget));
        WebpageWidgetContainer widgetContainers=new WebpageWidgetContainer(contents,1 , "widgetContainers1");
        return new WebpageInfo("WebPageInfo",List.of(widgetContainers));
    }

    private static Content getContent(String s, String textColor, BackgroundMusic backgroundMusic) {
        Effects effects=new Effects("MARQUEE_LEFT",1);
        ScrollAttributes scrollAttributes=new ScrollAttributes(effects);
        DisplayStyle displayStyle;

    // Check the length of the string
        if (s.length() > 10) {
            // Use SCROLL if the string length is greater than 9
            displayStyle = new DisplayStyle(scrollAttributes, "SCROLL");
        } else {
            // Use STATIC otherwise
            displayStyle = new DisplayStyle(scrollAttributes, "STATIC");
        }
        Seg seg=new Seg(s);
        Line lines=new Line(List.of(seg));
        Paragraph paragraphs=new Paragraph("#00000000","CENTER",0,0,List.of(lines),"TOP");
        Font font=new Font(List.of("Arial"),false,14,"NORMAL");
        Attributes attributes=new Attributes(font,0,textColor);
        TextAttribute textAttribute=new TextAttribute("#ff0000",attributes);
        return new Content(true, backgroundMusic,displayStyle,List.of(paragraphs),List.of(textAttribute));
    }

    public static CompletableFuture<String> makeProgram() throws InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();
        try {

            String programOutput = gson.toJson(new ProgramOutput(1,ROOTDIR));
            CountDownLatch latch = new CountDownLatch(1);
            instance.nvMakeProgramAsync(programOutput, (code, data) -> {
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
            String transferProgram = gson.toJson(new ProgramTransfer(g_sn,"","",ROOTDIR+"/program1",new Object(),"program1","Demo",true,true));
            CountDownLatch latch = new CountDownLatch(1);
            instance.nvStartTransferProgramAsync(transferProgram, (code, data) -> {
                if(code == 0){
                    future.complete(data);
                } else {
                    future.completeExceptionally(new Exception("Transfer Program Failed"));
                }
                latch.countDown();
            });
            waitAPIReturn(latch);
        } catch (InterruptedException e){
            future.completeExceptionally(e);
        }
        return future;
            
    }

    public static CompletableFuture<String> getProgramInfo() throws InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();
        try {
            serialNumber=gson.toJson(new StringFormatter(g_sn));
            CountDownLatch latch = new CountDownLatch(1);
            instance.nvGetProgramInfoAsync(serialNumber, (code, data) -> {
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
            String volumeData= gson.toJson(new StringFormatter(g_sn,60.0));
            CountDownLatch latch = new CountDownLatch(1);
            instance.nvSetVolumeAsync(volumeData, (code, data) -> {
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
            serialNumber=gson.toJson(new StringFormatter(g_sn));
            CountDownLatch latch = new CountDownLatch(1);
            instance.nvGetVolumeAsync(serialNumber, (code, data) -> {
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
            serialNumber=gson.toJson(new StringFormatter(g_sn));
            CountDownLatch latch = new CountDownLatch(1);
            instance.nvGetEthernetInfoAsync(serialNumber, (code, data) -> {
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

    public static CompletableFuture<String> setEthernetInfo(EthernetConfig config) throws InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();
        try {

            EthernetData ethernet=new EthernetData(-1, "eth0", false, config.getIp(), config.getMask(), config.getGateway(), config.getDnsServers());
            EthernetTaskInfo taskInfo= new EthernetTaskInfo(List.of(ethernet));
            String ethernetInfo= gson.toJson(new StringFormatter(g_sn,taskInfo));
            CountDownLatch latch = new CountDownLatch(1);
            instance.nvSetEthernetInfoAsync(ethernetInfo, (code, data) -> {
                if(code == 0){
                    future.complete("Screen Ip Successfully Set to: "+ config.toString());
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

    public static CompletableFuture<String> setScreenInfo() throws  InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();
        try {
            ScreenInfo screen = new ScreenInfo(g_sn,"Taurus-10000710", 64, 16, 16602, 16604, 16603, 16605);
            System.out.println(screen.toString());
            String screenInfo= gson.toJson(screen);
            CountDownLatch latch = new CountDownLatch(1);
            instance.nvSetScreenInfoAsync(screenInfo, (code, result) -> {
                if(code == 0){
                    future.complete(result);
                } else {
                    future.completeExceptionally(new Exception("Set Screen Info Failed"));
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
