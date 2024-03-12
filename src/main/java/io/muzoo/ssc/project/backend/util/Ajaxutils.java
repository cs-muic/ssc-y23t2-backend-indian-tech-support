package io.muzoo.ssc.project.backend.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.StringWriter;

public class Ajaxutils {

    // Converts object into JSON String representation
    // Used with SimpleResponseDTO in AuthenticationController
    public static String convertToString(Object objectValue){
        ObjectMapper mapper = new ObjectMapper();
        StringWriter sw = new StringWriter();
        try {
            mapper.writeValue(sw, objectValue);
            return sw.toString();
        } catch (Exception e) {
            return "[bad object/conversion]";
        }
    }
}
