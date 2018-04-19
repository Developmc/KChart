package com.example.kchart.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Author: Clement
 * Create: 2018/4/19
 * Desc:
 */

public class TimeUtil {
    public final static String CHART_FORMAT = "MM/dd";

    /**
     * 将时间戳按照格式转为字符串
     *
     * @param time
     * @param format
     * @return
     */
    public static String long2String(long time, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.getDefault());
        return simpleDateFormat.format(new Date(time));
    }
}
