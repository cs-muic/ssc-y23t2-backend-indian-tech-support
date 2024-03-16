package io.muzoo.ssc.project.backend.Transaction;

import org.thymeleaf.util.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Type {

    INCOME("INCOME"), 
    EXPENDITURE("EXPENDITURE"), 
    NONE("NONE"),
    ;

    @Getter
    private final String name;

    public static Type parseType(final String input) {

        try {
            final Type type = Type.valueOf(input);
            return type;
        }
        catch (IllegalArgumentException | NullPointerException e){
            // happens if no match or input is null
            e.printStackTrace();
            return NONE;
        }

    }
    
}





