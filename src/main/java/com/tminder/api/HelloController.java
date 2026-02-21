package com.tminder.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A basic REST Controller to verify the API setup.
 * In Clean Architecture, Controllers live in the 'api' (Interface) layer.
 */
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello World! Welcome to TMinder.";
    }
}
