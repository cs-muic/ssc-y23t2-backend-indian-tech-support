package io.muzoo.ssc.project.backend.history;

import org.thymeleaf.util.StringUtils;

public enum EditType {
    EDIT, DELETE, NONE;

    public static EditType parseType(String input) {
        if (StringUtils.equals(input, "EDIT")) {
            return EDIT;
        } else if (StringUtils.equals(input, "DELETE")) {
            return DELETE;
        } else {
            return NONE;
        }
    }
}
