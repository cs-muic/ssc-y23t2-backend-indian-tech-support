package io.muzoo.ssc.project.backend.Tag;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TagDTO {
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

    private List<Tag> tags;

    private List<SecondaryTag> secondaryTags;

    @Builder.Default
    private boolean loggedIn = false;
}

