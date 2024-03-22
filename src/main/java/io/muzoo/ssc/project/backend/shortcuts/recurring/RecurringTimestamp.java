package io.muzoo.ssc.project.backend.shortcuts.recurring;

import java.text.BreakIterator;
import java.util.Calendar;
import java.util.Date;

import org.checkerframework.checker.units.qual.min;
import org.springframework.context.annotation.Bean;

import ch.qos.logback.core.joran.conditional.ElseAction;

public class RecurringTimestamp {
    
    public static Integer getMonthAsInteger(){
        java.util.Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH);
        return month;
    }

    public static Long getYearAsLong(){
        return (Long)(long)java.time.Year.now().getValue();
    }

    public static Integer dateOrMaxDateOfMonth(Integer date){
        return dateOrMaxDateOfMonth(date,getMonthAsInteger());
    }

    public static Integer dateOrMaxDateOfMonth(Integer date,Integer month){

        Long year = getYearAsLong();
        boolean isLeapYear = java.time.Year.isLeap(year);
        int maxDate = date;

        switch(month){
            
            case 0:
                maxDate = 31;
                break;
            case 1:
                if (isLeapYear){
                    maxDate = 29;
                }
                else {
                    maxDate = 28;
                }
                break;
            case 2:
                maxDate = 31;
                break;
            case 3:
                maxDate = 30;
                break;
            case 4:
                maxDate = 31;
                break;
            case 5:
                maxDate = 30;
                break;
            case 6:
                maxDate = 31;
                break;
            case 7:
                maxDate = 31;
                break;
            case 8:
                maxDate = 30;
                break;
            case 9:
                maxDate = 31;
                break;
            case 10:
                maxDate = 30;
                break;
            case 11:
                maxDate = 31;
                break;
            default:
                break;
        }

        return Math.min(Math.abs(date), Math.abs(maxDate));

    }     

}
