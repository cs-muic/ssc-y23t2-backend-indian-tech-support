package io.muzoo.ssc.project.backend;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

//Class used to create objet for a message with a 'success' status
//Used in AuthenticationController.java
@Getter
@Setter
@Builder
public class SimpleResponseDTO {

    private boolean success;
    private String message;

}
