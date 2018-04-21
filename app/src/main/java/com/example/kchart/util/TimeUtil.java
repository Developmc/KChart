package com.example.kchart.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Author: Clement
 * Create: 2018/4/19
 * Desc:
 */

public class TimeUtil {
    public final static String CHART_FORMAT = "MM/dd";

    /**
     * 将时间戳按照格式转为字符串
     */
    public static String long2String(long time, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.getDefault());
        return simpleDateFormat.format(new Date(time));
    }

    /**
     * 根据时间格式获取Date实例
     */
    public static Date string2Date(String time, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
        Date date = null;
        try {
            date = formatter.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 根据时间格式获取Date实例
     */
    public static Date string2Date(String time, String timeZone, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
        formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
        Date date = null;
        try {
            date = formatter.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 将时间字符串转为timeMillis
     */
    public static long string2Long(String time, String format) {
        Date date = string2Date(time, format);
        return date.getTime();
    }
}
