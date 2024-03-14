package io.muzoo.ssc.project.backend.Transaction;

import org.thymeleaf.util.StringUtils;

import java.util.Objects;

public enum Type {
    INCOME, EXPENDITURE, NONE;

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





