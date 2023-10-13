package com.example.whocanplay;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class WhocanplayController {

    @GetMapping("/")
    public String index () {
        return "Greetings from Spring Boot!";
    }
}
