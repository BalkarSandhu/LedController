package com.dadhwal.LedController.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    private final AppConfig appConfig;

    public PageController(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @GetMapping("/")
    public String showIndex(Model model) {
        return "index";
    }

    @GetMapping("/configuration")
    public String getConfigurationPage() {
        return "configuration"; // Will render templates/configuration.html
    }

    @GetMapping("/network")
    public String getNetworkConfigPage() {
        return "network"; // Will render templates/network.html
    }
}
