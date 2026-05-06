package com.codesoftlabs.umbral.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    private DateUtils() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    public static LocalDate today() {
        return LocalDate.now();
    }

    public static LocalDateTime toLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp),
                ZoneId.systemDefault()
        );
    }

    public static long toTimestamp(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static String format(LocalDateTime dateTime, String pattern) {
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String format(LocalDate date, String pattern) {
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static LocalDateTime parse(String dateString, String pattern) {
        return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern(pattern));
    }

    public static LocalDate parseDate(String dateString, String pattern) {
        return LocalDate.parse(dateString, DateTimeFormatter.ofPattern(pattern));
    }

    public static boolean isAfter(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return dateTime1.isAfter(dateTime2);
    }

    public static boolean isBefore(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return dateTime1.isBefore(dateTime2);
    }

    public static boolean isEqual(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return dateTime1.isEqual(dateTime2);
    }

    public static long getDaysDifference(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return java.time.temporal.ChronoUnit.DAYS.between(dateTime1, dateTime2);
    }

    public static long getHoursDifference(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return java.time.temporal.ChronoUnit.HOURS.between(dateTime1, dateTime2);
    }

    public static long getMinutesDifference(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return java.time.temporal.ChronoUnit.MINUTES.between(dateTime1, dateTime2);
    }

    public static LocalDateTime addDays(LocalDateTime dateTime, long days) {
        return dateTime.plusDays(days);
    }

    public static LocalDateTime addHours(LocalDateTime dateTime, long hours) {
        return dateTime.plusHours(hours);
    }

    public static LocalDateTime addMinutes(LocalDateTime dateTime, long minutes) {
        return dateTime.plusMinutes(minutes);
    }
}
