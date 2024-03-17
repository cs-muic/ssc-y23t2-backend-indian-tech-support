package io.muzoo.ssc.project.backend.shortcuts.transactionblueprints;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Type {
    
    SHORTCUTS("SHORTCUTS"),
    RECURRING("RECURRING"),
    NONE("NONE"),
    ;
    @Getter
    private final String name;

    public Type getType(final String type){

        try {
            return valueOf(type);
        }
        catch(NullPointerException | IllegalArgumentException e) {
            e.printStackTrace();
            return NONE;
        }

    }

}
