package io.muzoo.ssc.project.backend.timestamp;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;

public class Timestamper {
    
    public static Timestamp getTimestamp(){
        Timestamp timestamp;
        timestamp = Timestamp.from(Instant.now());   
        return timestamp;
    }


}
