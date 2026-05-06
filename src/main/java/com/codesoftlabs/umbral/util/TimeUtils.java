package com.codesoftlabs.umbral.util;

/**
 * Utility class for time-related operations.
 * <p>
 * This class provides helper methods to parse human-readable duration strings
 * into their equivalent value in milliseconds.
 * </p>
 *
 * <p>
 * Supported formats:
 * <ul>
 *   <li>Plain numeric values (e.g., {@code "5000"}) are interpreted as milliseconds.</li>
 *   <li>Values with suffix:
 *     <ul>
 *       <li>{@code s} - seconds (e.g., {@code "10s"})</li>
 *       <li>{@code m} - minutes (e.g., {@code "5m"})</li>
 *       <li>{@code h} - hours (e.g., {@code "2h"})</li>
 *       <li>{@code d} - days (e.g., {@code "1d"})</li>
 *     </ul>
 *   </li>
 * </ul>
 * </p>
 *
 * <p>
 * If the input is {@code null} or empty, the method returns {@code 0}.
 * </p>
 */
public class TimeUtils {

    /**
     * Parses a duration string and converts it into milliseconds.
     *
     * <p>
     * The input string can be either:
     * <ul>
     *   <li>A numeric value representing milliseconds (e.g., {@code "1500"})</li>
     *   <li>A numeric value followed by a time unit suffix:
     *     <ul>
     *       <li>{@code s} for seconds</li>
     *       <li>{@code m} for minutes</li>
     *       <li>{@code h} for hours</li>
     *       <li>{@code d} for days</li>
     *     </ul>
     *   </li>
     * </ul>
     * </p>
     *
     * <p>
     * Any non-digit characters are ignored when extracting the numeric value.
     * </p>
     *
     * @param duration the duration string to parse (e.g., {@code "10s"}, {@code "5m"}, {@code "1000"})
     * @return the equivalent duration in milliseconds, or {@code 0} if the input is {@code null} or empty
     * @throws NumberFormatException if the numeric portion cannot be parsed into a {@code long}
     */
    public static long parseDuration(String duration) {
        if (duration == null || duration.isEmpty()) return 0;
        if (duration.matches("\\d+")) return Long.parseLong(duration);

        long multiplier = 1;
        if (duration.endsWith("s")) multiplier = 1000;
        else if (duration.endsWith("m")) multiplier = 60000;
        else if (duration.endsWith("h")) multiplier = 3600000;
        else if (duration.endsWith("d")) multiplier = 86400000L;

        String number = duration.replaceAll("[^\\d]", "");
        return Long.parseLong(number) * multiplier;
    }
}
