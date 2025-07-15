package com.dadhwal.LedController.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    private final AppConfig appConfig;

    public PageController(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @GetMapping("/page")
    public String getHtmlTemplate(Model model) {
        model.addAttribute("url", appConfig.getBaseurl());
        return "textView"; // Will render templates/textView.html
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
