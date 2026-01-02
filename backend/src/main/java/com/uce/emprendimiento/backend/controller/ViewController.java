package com.uce.emprendimiento.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }

    @GetMapping("/profile")
    public String profile() {
        return "forward:/pages/profile.html";
    }

    @GetMapping("/products")
    public String products() {
        return "forward:/pages/products.html";
    }

    @GetMapping("/history")
    public String history() {
        return "forward:/pages/history.html";
    }

    @GetMapping("/invoice")
    public String invoice() {
        return "forward:/pages/invoice.html";
    }

    @GetMapping("/signup")
    public String signup() {
        return "forward:/pages/signup.html";
    }
}
