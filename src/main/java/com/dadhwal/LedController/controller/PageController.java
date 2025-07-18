package com.dadhwal.LedController.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.dadhwal.LedController.config.Config;

@Controller
public class PageController {

    private final Config config;

    public PageController(Config config) {
        this.config = config;
    }


    @GetMapping("/")
    public String showIndex(Model model) {
        return "index";
    }

    @GetMapping("/page")
    public String getHtmlTemplate(Model model) {
        model.addAttribute("url", config.getBaseUrl());
        return "textview"; // Will render templates/textView.html
    }

    @GetMapping("/network")
    public String getNetworkConfigPage() {
        return "network"; // Will render templates/network.html
    }
}
