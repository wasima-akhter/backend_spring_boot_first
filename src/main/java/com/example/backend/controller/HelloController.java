package com.example.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String home() {
        return "Backend API is running!";
    }

    @GetMapping("/hello")
    public String sayHello() {
        return "aystd yat dya da sdy asyd aysdgyasdg ";
    }

    @GetMapping("/bool")
    public Boolean sayBool() {
        return true;
    }

    @GetMapping("/api/v1/getName/{id}")
    public long getName(@PathVariable Long id) {
        return id;
    }
}
