package com.kemiel.greenenergy;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 前端頁面路由 Controller。
 */
@Controller
public class WebController {

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/users")
    public String users() {
        return "users";
    }

    @GetMapping("/targets")
    public String targets() {
        return "targets";
    }

    @GetMapping("/solar-devices")
    public String solarDevices() {
        return "solar-devices";
    }

    @GetMapping("/contracts")
    public String contracts() {
        return "contracts";
    }
}