package com.adyen.workshop.controllers.views;

import com.adyen.workshop.configurations.ApplicationConfiguration;
import com.adyen.workshop.service.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class ViewController {
    private final Logger log = LoggerFactory.getLogger(ViewController.class);

    private final ApplicationConfiguration applicationConfiguration;

    public ViewController(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/preview")
    public String preview(@RequestParam String type, Model model) {
        model.addAttribute("type", type);
        return "preview";
    }

    @GetMapping("/checkout")
    public String checkout(@RequestParam String type, Model model) {
        model.addAttribute("type", type);
        model.addAttribute("clientKey", this.applicationConfiguration.getAdyenClientKey());
        return "checkout";
    }

    @GetMapping("/result/{type}")
    public String result(@PathVariable String type, Model model) {
        model.addAttribute("type", type);
        return "result";
    }

    @GetMapping("/redirect")
    public String redirect(Model model) {
        model.addAttribute("clientKey", this.applicationConfiguration.getAdyenClientKey());
        return "redirect";
    }

    @GetMapping("/refund")
    public String refund(Model model) {
        String pspreference = SessionManager.getPspReference();
        model.addAttribute("pspreference", pspreference);
        return "refund";
    }

}
