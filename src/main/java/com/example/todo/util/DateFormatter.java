package com.example.todo.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {

    public static final String TIME_ZONE = "GMT+8";

    public static final String YEAR_MONTH_DAY_HOUR_MINUTE_SECOND = "yyyy-MM-dd kk:mm:ss";

    public static String format(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(YEAR_MONTH_DAY_HOUR_MINUTE_SECOND);
        return sdf.format(date);
    }
}