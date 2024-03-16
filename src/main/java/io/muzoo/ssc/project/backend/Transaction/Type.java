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

    public static Type parseType(String input) {
        if (StringUtils.equals(input, "EXPENDITURE")) {
            return EXPENDITURE;
        } else if (StringUtils.equals(input, "INCOME")) {
            return INCOME;
        } else {
            return NONE;
        }
    }
}





