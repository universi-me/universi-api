package me.universi.util;

import java.util.Calendar;
import java.util.Date;

import jakarta.validation.constraints.NotNull;

public class DateUtil {
    private DateUtil() {}

    public static Date removeTimezoneDifference( @NotNull Date date ) {
        var calendar = Calendar.getInstance();
        calendar.setTime( date );

        // todo: get server timezone dynamically - currently hardcoded GMT-03:00
        calendar.add( Calendar.HOUR_OF_DAY, -3 );

        return calendar.getTime();
    }
}
