package com.github.charlemaznable.core.lang.time;

import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;

import static java.util.Calendar.getInstance;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class Now {

    public static String now() {
        return now("yyyy-MM-dd HH:mm:ss");
    }

    public static String millis() {
        return now("yyyy-MM-dd HH:mm:ss.SSS");
    }

    public static String now(String format) {
        return now(new SimpleDateFormat(format));
    }

    public static String now(SimpleDateFormat simpleDateFormat) {
        return simpleDateFormat.format(date());
    }

    public static String now(DateFormatter dateFormatter) {
        return dateFormatter.format(date());
    }

    public static Date date() {
        return getInstance().getTime();
    }
}
