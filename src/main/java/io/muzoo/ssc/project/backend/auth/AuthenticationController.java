package io.muzoo.ssc.project.backend.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    // If this message is shown that means login is successful because we didn't set to permit this path
    @GetMapping("api/test")
    public String test() {
        return "Test";
    }


    @PostMapping("/api/login")
    public String login(HttpServletRequest request) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        try {
            request.login(username, password);
            return "Login Successful";
        } catch (ServletException e) {
            return "Login Failed";
        }
    }

    @GetMapping("/api/logout")
    public String logout(HttpServletRequest request) {
        try {
            request.logout();
            return "Logout Successful";
        } catch (ServletException e){
            return "Logout Failed";
        }
    }
}
