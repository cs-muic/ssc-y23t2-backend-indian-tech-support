package io.muzoo.ssc.project.backend.User;

import io.muzoo.ssc.project.backend.SimpleResponseDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private StorageService storageService;

    @PostMapping("/api/signup")
    public SimpleResponseDTO register(HttpServletRequest request,  @RequestParam("avatar") MultipartFile avatarFile) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String displayName = request.getParameter("display_name");
        System.out.println(username + " " + password + " " + displayName);

        try {
            // Check if the username already exists
            User existingUser = userRepository.findByUsername(username);
            if (existingUser != null) {
                return SimpleResponseDTO.builder()
                        .success(false)
                        .message("Username already taken")
                        .build();
            }

            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof org.springframework.security.core.userdetails.User) {
                request.logout();
            }

            String avatarId;
            if (!avatarFile.isEmpty()) {
                avatarId = storageService.uploadFile(avatarFile, username);
            } else {
                avatarId = "default_avatar.jpeg";
            }

            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setDisplayName(displayName);
            user.setRole("USER");
            user.setAvatarId(avatarId); // Set avatar ID after successful upload

            userRepository.save(user);

            request.login(username, password);

            return SimpleResponseDTO.builder().success(true).message("Register Successful").build();
        } catch (ServletException e) {
            return SimpleResponseDTO.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        }
    }
}
