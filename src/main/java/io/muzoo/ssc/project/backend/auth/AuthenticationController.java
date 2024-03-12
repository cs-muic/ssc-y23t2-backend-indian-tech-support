package io.muzoo.ssc.project.backend.auth;

import io.muzoo.ssc.project.backend.SimpleResponseDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
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


    // This method is used to login the user returns SimpleResponseDTO object as a JSON string
    @PostMapping("/api/login")
    public SimpleResponseDTO login(HttpServletRequest request) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof org.springframework.security.core.userdetails.User) {
                request.logout();
            }
            request.login(username, password);
            return SimpleResponseDTO.builder().success(true).message("Login Successful").build();
        } catch (ServletException e) {
            return SimpleResponseDTO.builder().success(false).message("Incorrect username/password").build();
        }
    }

    // This method is used to logout the user returns SimpleResponseDTO object as a JSON string
    @GetMapping("/api/logout")
    public SimpleResponseDTO logout(HttpServletRequest request) {
        try {
            request.logout();
            return SimpleResponseDTO.builder().success(true).message("Logout Successful").build();
        } catch (ServletException e){
            return SimpleResponseDTO.builder().success(true).message("Logout Failed").build();
        }
    }
}
