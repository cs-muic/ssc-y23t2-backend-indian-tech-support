package io.muzoo.ssc.project.backend.whoami;

import io.muzoo.ssc.project.backend.User.User;
import io.muzoo.ssc.project.backend.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A controller to retrieve current logged-in user.
 */
@RestController
public class WhoamiController {

    @Autowired
    private UserRepository userRepository;

    @Value("${space.endpoint}")
    private String spaceEndpoint;

    @Value("${space.name}")
    private String spaceName;

    /**
     * Make sure that all API path begins with /api. This ends up being useful for when we do proxy
     */
    @GetMapping("/api/whoami")
    public WhoAmIDTO whoami() {
        try {
            // The line below has the potential for a NullPointException due to nesting dot notation
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal != null && principal instanceof org.springframework.security.core.userdetails.User) {
                // user is logged in
                org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) principal;
                User u = userRepository.findByUsername(user.getUsername());
                String avatarUrl = "";
                if (u.getAvatarId() != null && !u.getAvatarId().isEmpty()) {
                    avatarUrl = spaceEndpoint + "/" + spaceName + "/" + u.getAvatarId();
                }
                return WhoAmIDTO.builder()
                        .loggedIn(true)
                        .displayName(u.getDisplayName())
                        .role(u.getRole())
                        .username(u.getUsername())
                        .avatarId(avatarUrl)
                        .build();
            }
        } catch (Exception e) {
            // Ajarn just left this blank lmao
        }
        // user is not logged in
        return WhoAmIDTO.builder()
                .loggedIn(false)
                .build();
    }
}
