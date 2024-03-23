package io.muzoo.ssc.project.backend.Tag;

import io.muzoo.ssc.project.backend.User.User;
import io.muzoo.ssc.project.backend.User.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecondaryTagController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SecondaryTagRepository secondaryTagRepository;
    @GetMapping("/api/secondary_tag")
    public SecondaryTagDTO getAll(HttpServletRequest request) {
        try {
            // The line below has the potential for a NullPointException due to nesting dot notation
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal != null && principal instanceof org.springframework.security.core.userdetails.User) {
                // user is logged in
                org.springframework.security.core.userdetails.User user =
                        (org.springframework.security.core.userdetails.User) principal;
                User u = userRepository.findByUsername(user.getUsername());

                // Extracting and parsing request parameters
                String tagIdParam = request.getParameter("tagId");
                long tagId = 0; // Default value

                try {
                    // Only parse if the parameters are not null and not empty
                    if (tagIdParam != null && !tagIdParam.isEmpty()) {
                        tagId = Long.parseLong(tagIdParam);
                    }
                } catch (NumberFormatException e) {
                    // Log error or handle the case where parameters are invalid
                    System.err.println("Error parsing tagId or tagId2 from request parameters");
                }

                SecondaryTagDTO output = SecondaryTagDTO.builder().build();
                output.setLoggedIn(true);
                output.setSecondaryTags(secondaryTagRepository.findAllByTagId(tagId));
                return output;
            }
        } catch (Exception e) {
            // Ajarn just left this blank lmao
        }
        // user is not logged in
        return SecondaryTagDTO.builder()
                .loggedIn(false)
                .build();
    }
}
