package com.aaronag.resourceserver;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/public/ping")
    public String ping() { return "pong"; }

    @GetMapping("/secure/hello")
    public String hello() { return "Hello from Resource Server"; }
    //http://127.0.0.1:8080/authorized?code=EKMZCxT-N5wC2txvMV4ShF_um3TBXE1H3ldln-PIz0C7Ze-rOEQ_O_eb1c0EkyMa0s5wRoG1qWqnwWcRpdb0xKvc9Z28YC9lhgVhlXilxZqAi9XLsyd7MdjAU34hZlJo
}
