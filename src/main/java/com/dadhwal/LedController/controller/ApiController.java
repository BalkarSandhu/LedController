package com.dadhwal.LedController.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.CompletableFuture;
import com.dadhwal.sdk.SDKWrapper;

@RestController
@RequestMapping("/api")
public class ApiController {

    private static final Logger logger = Logger.getLogger(ApiController.class.getName());


    static {
        SDKWrapper.init();

    }

    @GetMapping("/searchTerminal")
    public ResponseEntity<String>  searchTerminal() {
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
        try {
            CompletableFuture<String> res= SDKWrapper.login();
            return ResponseEntity.ok(res.get());
        } catch (InterruptedException | ExecutionException e) {
            logger.log(Level.SEVERE, "Login failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login Failed");
        }
    }

    @PostMapping("/createProgram")
    public ResponseEntity<String> createProgram() {
        try {
            CompletableFuture<String> res=SDKWrapper.createProgram();
            return ResponseEntity.ok(res.get());
        } catch (InterruptedException | ExecutionException e) {
            logger.log(Level.SEVERE, "Create Program failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Create Program failed");
        }
    }

    @PostMapping("/setPageProgram")
    public ResponseEntity<String> setPageProgram(@RequestParam String message) {
        try {
            CompletableFuture<String> res= SDKWrapper.setPageProgram(message);
            return ResponseEntity.ok(res.get());
        } catch (InterruptedException | ExecutionException e) {
            logger.log(Level.SEVERE, "Set Page Program failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Set Page Program failed");
        }
    }

    @GetMapping("/makeProgram")
    public ResponseEntity<String> makeProgram() {
        try {
            CompletableFuture<String> res= SDKWrapper.makeProgram();
            return ResponseEntity.ok(res.get());
        } catch (InterruptedException | ExecutionException e) {
            logger.log(Level.SEVERE, "Make Program failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Make Program failed");
        }
    }

    @PostMapping("/transferProgram")
    public ResponseEntity<String> transferProgram() {
        try {
            CompletableFuture<String> res=SDKWrapper.transferProgram();
            return ResponseEntity.ok(res.get());
        } catch (InterruptedException | ExecutionException e) {
            logger.log(Level.SEVERE, "Transfer Program failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Transfer Program failed");
        }
    }

    @GetMapping("/getProgramInfo")
    public ResponseEntity<String> getProgramInfo() {
        try {
            CompletableFuture<String> res=SDKWrapper.getProgramInfo();
            return ResponseEntity.ok(res.get());
        } catch (InterruptedException | ExecutionException e) {
            logger.log(Level.SEVERE, "Get Program Info failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Get Program Info failed");
        }
    }

    @PostMapping("/setVolume")
    public ResponseEntity<String> setVolume() {
        try {
            CompletableFuture<String> res=SDKWrapper.setVolume();
            return ResponseEntity.ok(res.get());
        } catch (InterruptedException | ExecutionException e) {
            logger.log(Level.SEVERE, "Set Volume failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Set Volume failed");
        }
    }

    @GetMapping("/getVolume")
    public ResponseEntity<String> getVolume() {
        try {
            CompletableFuture<String> res=SDKWrapper.getVolume();
            return ResponseEntity.ok(res.get());
        } catch (InterruptedException | ExecutionException e) {
            logger.log(Level.SEVERE, "Get Volume failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Get Volume failed");
        }
    }

    @GetMapping("/getEthernetInfo")
    public ResponseEntity<String> getEthernetInfo() {
        try {
            CompletableFuture<String> res=SDKWrapper.getEthernetInfo();
            return ResponseEntity.ok(res.get());
        } catch (InterruptedException | ExecutionException e) {
            logger.log(Level.SEVERE, "Get Ethernet Info failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Get Ethernet Info failed");
        }
    }

    @PostMapping("/setEthernetInfo")
    public ResponseEntity<String> setEthernetInfo() {
        try {
            CompletableFuture<String> res=SDKWrapper.setEthernetInfo();
            return ResponseEntity.ok(res.get());
        } catch (InterruptedException | ExecutionException e) {
            logger.log(Level.SEVERE, "Set Ethernet Info failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Set Ethernet Info failed");
        }
    }
}
