package com.guroomsoft.icms.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;


@Slf4j
public class DateTimeUtil {
    private final static String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
    private final static String dateTimeFormat2 = "yyyyMMddHHmmss";
    private final static String dateFormat = "yyyy-MM-dd";
    private final static String dateFormat2 = "yyyyMMdd";

    public static String dateToString(LocalDate dt) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Date tmpDt = java.sql.Date.valueOf(dt);
        return formatter.format(tmpDt);
    }

    public static String dateToString(LocalDate dt, String df) {
        SimpleDateFormat formatter = new SimpleDateFormat(df);
        Date tmpDt = java.sql.Date.valueOf(dt);
        return formatter.format(tmpDt);
    }

    public static String datetimeToString(LocalDateTime dt) {
        return dt.format(DateTimeFormatter.ofPattern(dateTimeFormat));
    }

    public static String datetimeToString(LocalDateTime dt, String df) {
        return dt.format(DateTimeFormatter.ofPattern(df));
    }

    public static Date stringToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        try {
            return formatter.parse(strDate);
        } catch (ParseException e) {
            log.error("일자변환에 실패하였습니다.");
            return null;
        }
    }

    public static Date stringToDatetime(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateTimeFormat);
        try {
            return formatter.parse(strDate);
        } catch (ParseException e) {
            log.error("일자변환에 실패하였습니다.");
            return null;
        }
    }

    public static LocalDateTime stringToLocalDatetime(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateTimeFormat);
        try {
            Date t = formatter.parse(strDate);
            LocalDateTime localDateTime = LocalDateTime.ofInstant(t.toInstant(), ZoneId.systemDefault());
            return localDateTime;
        } catch (ParseException e) {
            log.error("일자변환에 실패하였습니다.");
            return null;
        }
    }

    public static boolean isAfter(String targetDatetime) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expired = stringToLocalDatetime(targetDatetime);
        return now.isAfter(expired);
    }

    public static boolean isBefore(String targetDatetime) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expired = stringToLocalDatetime(targetDatetime);
        return now.isBefore(expired);
    }

    public static String createExpiredDatetime(int minutes) {
        Date now = Timestamp.valueOf(LocalDateTime.now());
        Date tmp = DateUtils.addMinutes(now, minutes);
        LocalDateTime expired = LocalDateTime.ofInstant(tmp.toInstant(), ZoneId.systemDefault());
        return datetimeToString(expired);
    }

    /**
     * 현재 일시
     * @return
     */
    public static String currentDatetime() {
        LocalDateTime now = LocalDateTime.now();
        return datetimeToString(now);
    }

    public static String currentDatetime2() {
        LocalDateTime now = LocalDateTime.now();
        return datetimeToString(now, dateTimeFormat2);
    }

    /**
     * 오늘 일자
     * @return
     */
    public static String currentDate() {
        LocalDate today = LocalDate.now();
        return dateToString(today);
    }

    public static String currentDate2() {
        LocalDate today = LocalDate.now();
        return dateToString(today, dateFormat2);
    }


    public static void main(String[] args) {
        String t = createExpiredDatetime(5);
        log.info(t);

        return;
    }

}
