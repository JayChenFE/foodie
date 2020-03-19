package com.github.jaychenfe.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jaychenfe
 */
@RestController
public class HelloController {

    @GetMapping("/hello")
    public Object hello() {
        return "hello world！";
    }
}
