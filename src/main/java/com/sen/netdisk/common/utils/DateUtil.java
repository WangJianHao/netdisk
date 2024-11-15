package com.sen.netdisk.common.utils;


import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 日期工具类
 *
 * @author sensen
 * @date 2021-01-01
 */
public class DateUtil {

    public static String YYYY = "yyyy";

    public static String YYYY_MM = "yyyy-MM";

    public static String YYYYMM = "yyyyMM";

    public static String YYYY_MM_DD = "yyyy-MM-dd";

    public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public static final String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM",
            "yyyy:MM:dd"
    };

    /**
     * 获取当前时间，默认格式yyyy-MM-dd HH:mm:ss
     *
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS));
    }

    /**
     * 获取当前LocalTime型 时间
     *
     * @return LocalTime 当前时间
     */
    public static LocalTime getNowTime() {
        return LocalTime.now();
    }

    /**
     * 获取当前LocalDate型日期
     * yyyy-MM-dd
     *
     * @return LocalDate 当前日期
     */
    public static LocalDate getNowDate() {
        return LocalDate.now();
    }

    /**
     * 获取当前LocalDateTime型日期时间
     *
     * @return LocalDateTime 当前日期时间
     */
    public static LocalDateTime getNowDateTime() {
        return LocalDateTime.now();
    }

    /**
     * 获取指定日期的毫秒/日期转换为long类型时间戳
     *
     * @param localDateTime 输入日期
     * @return 毫秒 13位时间戳
     */
    public static Long getMillis(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }


    /**
     * 获取指定日期的指定格式
     *
     * @param localDateTime 指定日期
     * @param pattern       格式
     * @return String型日期
     */
    public static String formatTime(LocalDateTime localDateTime, String pattern) {
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 获取指定日期的指定格式
     *
     * @param timestamp 指定日期
     * @return String型日期
     */
    public static String formatTime(Timestamp timestamp) {
        if (Objects.isNull(timestamp)) {
            return null;
        }
        return timestamp.toLocalDateTime().format(DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS));
    }

    /**
     * 指定日期转换为String类型时间
     *
     * @param localDateTime 输入日期
     * @param pattern       输出日期的时间格式
     * @return String
     */
    public static String localDateTimeToString(LocalDateTime localDateTime, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return localDateTime.format(formatter);
    }

    /**
     * String类型时间转换为localDateTime日期
     *
     * @param dateTime 输入日期
     * @param pattern  输入日期的时间格式
     * @return String
     */
    public static LocalDateTime stringToLocalDateTime(String dateTime, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(dateTime, formatter);
    }

    /**
     * String类型时间转换为localDateTime日期
     *
     * @param date    输入日期
     * @param pattern 输入日期的时间格式
     * @return String
     */
    public static LocalDate stringToLocalDate(String date, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDate.parse(date, formatter);
    }

    /**
     * String类型时间转换为localDateTime日期
     *
     * @param time    输入日期
     * @param pattern 输入日期的时间格式
     * @return String
     */
    public static LocalTime stringToLocalTime(String time, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalTime.parse(time, formatter);
    }

    /**
     * Date转换为LocalDateTime
     *
     * @param date 输入Date日期
     * @return LocalDateTime日期
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * LocalDateTime转换为Date
     *
     * @param localDateTime 输入LocalDateTime日期
     * @return 输出Date日期
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 获取两个日期的差  field参数为ChronoUnit.
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param field     单位(年月日时分秒) ChronoUnit.HOURS  ChronoUnit.DAYS 枚举
     * @return 差值
     */
    public static long betweenTwoTime(LocalDateTime startTime, LocalDateTime endTime, ChronoUnit field) {
        Period period = Period.between(LocalDate.from(startTime), LocalDate.from(endTime));

        if (field == ChronoUnit.YEARS) return period.getYears();
        if (field == ChronoUnit.MONTHS) return period.getYears() * 12L + period.getMonths();
        return field.between(startTime, endTime);
    }

    /**
     * 计算日期
     *
     * @param date    日期
     * @param pattern 格式
     * @param addDay  天数
     * @return 计算之后的日期
     */
    public static String getNextDay(String date, String pattern, int addDay) {
        LocalDate localDate = stringToLocalDate(date, pattern);
        return localDate.plusDays(addDay).format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 计算日期
     *
     * @param localDateTime 日期
     * @param addDay        天数
     * @return 计算之后的日期
     */
    public static Timestamp getNextDay(LocalDateTime localDateTime, int addDay) {
        return Timestamp.valueOf(localDateTime.plusDays(addDay));
    }


    /**
     * 计算日期时间
     *
     * @param dateTime 日期时间
     * @param pattern  格式
     * @param number   数量
     * @param unit     单位 ChronoUnit
     * @return 计算之后的日期时间
     */
    public static String plus(String dateTime, String pattern, long number, TemporalUnit unit) {
        LocalDateTime localDateTime = stringToLocalDateTime(dateTime, pattern);
        return localDateTime.plus(number, unit).format(DateTimeFormatter.ofPattern(pattern));
    }


    /**
     * 日 开始时间、结束时间
     *
     * @param localDateTime 输入日期
     * @return todayTime
     */
    public static Map<String, LocalDateTime> dayTime(LocalDateTime localDateTime) {
        Map<String, LocalDateTime> todayTime = new HashMap<>();
        todayTime.put("beginTime", localDateTime.with(LocalTime.MIN));
        todayTime.put("endTime", localDateTime.with(LocalTime.MAX));
        return todayTime;
    }

    /**
     * 周 开始时间、结束时间
     *
     * @param localDateTime 输入日期
     * @return weekTime
     */
    public static Map<String, LocalDateTime> weekTime(LocalDateTime localDateTime) {
        Map<String, LocalDateTime> weekTime = new HashMap<>();
        int dayOfWeek = localDateTime.getDayOfWeek().getValue();
        LocalDateTime weekStart = localDateTime.minusDays(dayOfWeek - 1).with(LocalTime.MIN);
        LocalDateTime weekEnd = localDateTime.plusDays(7 - dayOfWeek).with(LocalTime.MAX);
        weekTime.put("beginTime", weekStart);
        weekTime.put("endTime", weekEnd);
        return weekTime;
    }

    /**
     * 月 开始时间、结束时间
     *
     * @param localDateTime 输入日期
     * @return monthTime
     */
    public static Map<String, LocalDateTime> monthTime(LocalDateTime localDateTime) {
        Map<String, LocalDateTime> monthTime = new HashMap<>();
        LocalDateTime monthStart = localDateTime.with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
        LocalDateTime monthEnd = localDateTime.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
        monthTime.put("beginTime", monthStart);
        monthTime.put("endTime", monthEnd);
        return monthTime;
    }

    /**
     * 季 开始时间、结束时间
     *
     * @param localDateTime 输入日期
     * @return seasonTime
     */
    public static Map<String, LocalDateTime> seasonTime(LocalDateTime localDateTime) {
        Map<String, LocalDateTime> seasonTime = new HashMap<>();
        LocalDate seasonStartDate = LocalDate.of(localDateTime.getYear(), localDateTime.getMonth().firstMonthOfQuarter(), 1);
        LocalDate seasonEndDate = LocalDate.of(localDateTime.getYear(), localDateTime.getMonth().firstMonthOfQuarter().plus(2), localDateTime.getMonth().firstMonthOfQuarter().plus(2).maxLength());
        LocalDateTime seasonStart = LocalDateTime.of(seasonStartDate, LocalTime.MIN);
        LocalDateTime seasonEnd = LocalDateTime.of(seasonEndDate, LocalTime.MAX);
        seasonTime.put("beginTime", seasonStart);
        seasonTime.put("endTime", seasonEnd);
        return seasonTime;
    }

    /**
     * 年 开始时间、结束时间
     *
     * @param localDateTime 输入日期
     * @return yearTime
     */
    public static Map<String, LocalDateTime> yearTime(LocalDateTime localDateTime) {
        Map<String, LocalDateTime> yearTime = new HashMap<>();
        LocalDateTime yearStart = localDateTime.with(TemporalAdjusters.firstDayOfYear()).with(LocalTime.MIN);
        LocalDateTime yearEnd = localDateTime.with(TemporalAdjusters.lastDayOfYear()).with(LocalTime.MAX);
        yearTime.put("beginTime", yearStart);
        yearTime.put("endTime", yearEnd);
        return yearTime;
    }

    /**
     * 获取当前时间戳，单位毫秒
     *
     * @return 时间戳
     */
    public static Long getCurrentTimestamp() {
        //   System.currentTimeMillis()
        return Instant.now().toEpochMilli();
    }

    /**
     * 获取当前时间戳，单位秒
     *
     * @return 时间戳
     */
    public static Long getCurrentSecondTimestamp() {
        return Instant.now().getEpochSecond();
    }
}

