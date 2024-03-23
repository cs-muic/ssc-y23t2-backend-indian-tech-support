package io.muzoo.ssc.project.backend.Tag;

import io.muzoo.ssc.project.backend.User.User;
import io.muzoo.ssc.project.backend.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TagController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private SecondaryTagRepository secondaryTagRepository;
    @GetMapping("/api/tag")
    public TagDTO tag() {
        try {
            // The line below has the potential for a NullPointException due to nesting dot notation
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal != null && principal instanceof org.springframework.security.core.userdetails.User) {
                // user is logged in
                org.springframework.security.core.userdetails.User user =
                        (org.springframework.security.core.userdetails.User) principal;
                User u = userRepository.findByUsername(user.getUsername());
                TagDTO output = TagDTO.builder().build();
                output.setLoggedIn(true);
                List<Tag> userTags = tagRepository.findAllByUserId(u.getId());
                userTags.addAll(tagRepository.findAllByUserId(userRepository.findByUsername("admin").getId()));
                output.setTags(userTags);
                return output;
            }
        } catch (Exception e) {
            // Ajarn just left this blank lmao
        }
        // user is not logged in
        return TagDTO.builder()
                .loggedIn(false)
                .build();
    }
}
