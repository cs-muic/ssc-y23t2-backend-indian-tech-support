package io.muzoo.ssc.project.backend.auth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    @PostMapping("/api/login")
    public String login() {
        return "Login";
    }

    @GetMapping("/api/logout")
    public String logout() {
        return "Logout";
    }
}
