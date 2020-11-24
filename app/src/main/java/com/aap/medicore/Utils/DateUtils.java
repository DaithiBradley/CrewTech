package com.aap.medicore.Utils;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateUtils {


    public final static String hyphenDateWithYearAtEndEU = "dd-MM-yyyy";
    public final static String hyphenDateWithYearAtEndUS = "MM-dd-yyyy";
    public final static String hyphenDateWithYearAtStartEU = "yyyy-dd-MM";
    public final static String hyphenDateWithYearAtStartUS = "yyyy-MM-dd";
    public final static String slashDateWithYearAtEndEU = "dd/MM/yyyy";
    public final static String slashDateWithYearAtEndUS = "MM/dd/yyyy";
    public final static String slashDateWithYearAtStartEU = "yyyy/dd/MM";
    public final static String slashDateWithYearAtStartUS = "yyyy/MM/dd";
    public final static String dotDateWithYearAtEndEU = "dd.MM.yyyy";
    public final static String dotDateWithYearAtEndUS = "MM.dd.yyyy";
    public final static String dotDateWithYearAtStartEU = "yyyy.dd.MM";
    public final static String dotDateWithYearAtStartUS = "yyyy.MM.dd";

    public final static String[] mostFrequentlyPatterns = {
            "dd/MM/yyyy",
            "MM-dd-yyyy",
            "yyyy-MM-dd",
            "MM/dd/yyyy",
            "dd-MM-yyyy",
            "yyyy-dd-MM",
            "yyyy/MM/dd",
            "yyyy/dd/MM"
    };

    /**
     * Function try to parse Date from String using array of patterns. First pattern which parse string
     * successfully would be used.
     *
     * @param value date string
     * @param patterns array of patterns
     * @return NULL if string was not parsed using any of patterns
     */
    public static Date tryParseDateWithPatternList(@NonNull String value, @NonNull String[] patterns) {
        for (String pattern : patterns) {
            if (isDateValidToPattern(pattern, value)) {
                return parseDateWithoutPrefer(value, pattern);
            }
        }
        return null;
    }

    /**
     * Function parse date representation string with unknown format, with selection preferred day-month
     * order. Supported delimiters {'-', '/', '.'}.
     * Supported dateFormat {
     * "dd-MM-yyyy", "MM-dd-yyyy",
     * "yyyy-dd-MM", "yyyy-MM-dd",
     * "dd/MM/yyyy", "MM/dd/yyyy",
     * "yyyy/dd/MM", "yyyy/MM/dd",
     * "dd.MM.yyyy", "MM.dd.yyyy",
     * "yyyy.dd.MM", "yyyy.MM.dd" }
     *
     * @param value date string
     * @param prefer_DD_MM day-month prefer
     * @return null if unknown format
     */
    public static Date tryParseDate(@NonNull String value, boolean prefer_DD_MM) {
        String format = determineDateFormat(value, prefer_DD_MM);
        if (format != null && isDateValidToPattern(format, value)) {
            return parseDateWithoutPrefer(value, format);
        } else
            return null;
    }

    /**
     * Function parse date representation string with unknown format, with selection preferred day-month
     * order. In case of fault function try parse value with reversed day-month order selection.
     * Supported delimiters {'-', '/', '.'}.
     * Supported dateFormat {
     * "dd-MM-yyyy", "MM-dd-yyyy",
     * "yyyy-dd-MM", "yyyy-MM-dd",
     * "dd/MM/yyyy", "MM/dd/yyyy",
     * "yyyy/dd/MM", "yyyy/MM/dd",
     * "dd.MM.yyyy", "MM.dd.yyyy",
     * "yyyy.dd.MM", "yyyy.MM.dd" }
     *
     * @param value date string
     * @param prefer_DD_MM day-month prefer
     * @return null if unknown format
     */
    public static Date tryParseDateWithInverseOrderOnFault(@NonNull String value, boolean prefer_DD_MM) {
        Date result = tryParseDate(value, prefer_DD_MM);
        if (result == null)
            result = tryParseDate(value, !prefer_DD_MM);
        return result;
    }

    /**
     * Function parse date representation string with delimiters {'-', '/', '.'}, with selection of
     * year position in this string (start - 'yyyy...'; end - '...yyyy'), and day-month order in this
     * string.
     *
     * @param value        string that represent date
     * @param delimiter    string contain one of {'-', '/', '.'} delimiters
     * @param yearAtEnd    year position selection
     * @param prefer_DD_MM day-month order selection
     * @return date or NULL if was parsing error
     */
    public static Date parseDate(@NonNull String value, @NonNull String delimiter, boolean yearAtEnd, boolean prefer_DD_MM) {
        String format = getFormat(delimiter, yearAtEnd, prefer_DD_MM);
        return parseDateWithoutPrefer(value, format);
    }

    /**
     * Function parse date representation string with delimiters {'-', '/', '.'}, with selection of
     * year position in this string (start - 'yyyy...'; end - '...yyyy'), and preferred day-month order
     * in this string. If was parse error in case of wrong day-month selection, execute same function
     * with reverse order of day-month
     *
     * @param value        string that represent date
     * @param delimiter    string contain one of {'-', '/', '.'} delimiters
     * @param yearAtEnd    year position selection
     * @param prefer_DD_MM day-month order selection
     * @return date or NULL if was parsing error
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Date parseDateWithInverseOrderOnFault(@NonNull String value, @NonNull String delimiter, boolean yearAtEnd, boolean prefer_DD_MM) {
        Date result = parseDate(value, delimiter, yearAtEnd, prefer_DD_MM);
        if (result == null)
            result = parseDate(value, delimiter, yearAtEnd, !prefer_DD_MM);
        return result;
    }

    public static Date parseSlashDateWithYearAtStart(@NonNull String value, boolean preferUS) {
        return preferUS ?
                parseDateWithPrefer(value, slashDateWithYearAtStartUS, slashDateWithYearAtStartEU) :
                parseDateWithPrefer(value, slashDateWithYearAtStartEU, slashDateWithYearAtStartUS);
    }

    public static Date parseSlashDateWithYearAtEnd(@NonNull String value, boolean preferUS) {
        return preferUS ?
                parseDateWithPrefer(value, slashDateWithYearAtEndUS, slashDateWithYearAtEndEU) :
                parseDateWithPrefer(value, slashDateWithYearAtEndEU, slashDateWithYearAtEndUS);
    }

    public static Date parseHyphenDateWithYearAtStart(@NonNull String value, boolean preferUS) {
        return preferUS ?
                parseDateWithPrefer(value, hyphenDateWithYearAtStartUS, hyphenDateWithYearAtStartEU) :
                parseDateWithPrefer(value, hyphenDateWithYearAtStartEU, hyphenDateWithYearAtStartUS);
    }

    public static Date parseHyphenDateWithYearAtEnd(@NonNull String value, boolean preferUS) {
        return preferUS ?
                parseDateWithPrefer(value, hyphenDateWithYearAtEndUS, hyphenDateWithYearAtEndEU) :
                parseDateWithPrefer(value, hyphenDateWithYearAtEndEU, hyphenDateWithYearAtEndUS);
    }

    public static Date parseDotDateWithYearAtStart(@NonNull String value, boolean preferUS) {
        return preferUS ?
                parseDateWithPrefer(value, dotDateWithYearAtStartUS, dotDateWithYearAtStartEU) :
                parseDateWithPrefer(value, dotDateWithYearAtStartEU, dotDateWithYearAtStartUS);
    }

    public static Date parseDotDateWithYearAtEnd(@NonNull String value, boolean preferUS) {
        return preferUS ?
                parseDateWithPrefer(value, dotDateWithYearAtEndUS, dotDateWithYearAtEndEU) :
                parseDateWithPrefer(value, dotDateWithYearAtEndEU, dotDateWithYearAtEndUS);
    }

    public static boolean isDateValidToPattern(@NonNull String format, @NonNull String value) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date date = sdf.parse(value);
            return value.equals(sdf.format(date));
        } catch (ParseException ex) {
            return false;
        }
    }

    private static Date parseDateWithPrefer(String value, String preferredFormat, String secondFormat) {
        SimpleDateFormat format = null;
        if (isDateValidToPattern(preferredFormat, value)) {
            format = new SimpleDateFormat(preferredFormat);
        } else if (isDateValidToPattern(secondFormat, value)) {
            format = new SimpleDateFormat(secondFormat);
        } else
            return null;

        try {
            return format.parse(value);
        } catch (ParseException e) {
            return null;
        }
    }

    private static Date parseDateWithoutPrefer(String value, String format) {
        SimpleDateFormat format1 = new SimpleDateFormat(format);
        format1.setLenient(true);
        try {
            return format1.parse(value);
        } catch (Exception e) {
            return null;
        }
    }

    private static final Map<String, DelimiterType> DATE_FORMAT_REGEXPS = new HashMap<String, DelimiterType>() {{
        put("^\\d{1,2}-\\d{1,2}-\\d{4}$", DelimiterType.HYPHEN_YEAR_AT_END);
        put("^\\d{4}-\\d{1,2}-\\d{1,2}$", DelimiterType.HYPHEN_YEAR_AT_START);
        put("^\\d{1,2}/\\d{1,2}/\\d{4}$", DelimiterType.SLASH_YEAR_AT_END);
        put("^\\d{4}/\\d{1,2}/\\d{1,2}$", DelimiterType.SLASH_YEAR_AT_START);
        put("^\\d{1,2}\\.\\d{1,2}\\.\\d{4}$", DelimiterType.DOT_YEAR_AT_END);
        put("^\\d{4}\\.\\d{1,2}\\.\\d{1,2}$", DelimiterType.DOT_YEAR_AT_START);
    }};

    private static String determineDateFormat(String dateString, boolean DD_MM) {
        DelimiterType type = null;
        String result = null;
        for (String regexp : DATE_FORMAT_REGEXPS.keySet()) {
            if (dateString.toLowerCase().matches(regexp)) {
                type = DATE_FORMAT_REGEXPS.get(regexp);
            }
        }

        if (type == null)
            return null;

        switch (type) {
            case HYPHEN_YEAR_AT_START:
                result = (DD_MM) ? hyphenDateWithYearAtStartEU : hyphenDateWithYearAtStartUS;
                break;
            case HYPHEN_YEAR_AT_END:
                result = (DD_MM) ? hyphenDateWithYearAtEndEU : hyphenDateWithYearAtEndUS;
                break;
            case SLASH_YEAR_AT_START:
                result = (DD_MM) ? slashDateWithYearAtStartEU : slashDateWithYearAtStartUS;
                break;
            case SLASH_YEAR_AT_END:
                result = (DD_MM) ? slashDateWithYearAtEndEU : slashDateWithYearAtEndUS;
                break;
            case DOT_YEAR_AT_START:
                result = (DD_MM) ? dotDateWithYearAtStartEU : dotDateWithYearAtStartUS;
                break;
            case DOT_YEAR_AT_END:
                result = (DD_MM) ? dotDateWithYearAtEndEU : dotDateWithYearAtEndUS;
                break;
        }
        return result;
    }

    private static String getFormat(@NonNull String delimiter, boolean yearAtEnd, boolean prefer_DD_MM) {
        String option = delimiter +
                (yearAtEnd ? 'E' : 'S') +
                (prefer_DD_MM ? 'D' : 'M');
        String format = null;
        switch (option) {
            case "-ED":
                format = hyphenDateWithYearAtEndEU;
                break;
            case "-EM":
                format = hyphenDateWithYearAtEndUS;
                break;
            case "-SD":
                format = hyphenDateWithYearAtStartEU;
                break;
            case "-SM":
                format = hyphenDateWithYearAtStartUS;
                break;
            case "/ED":
                format = slashDateWithYearAtEndEU;
                break;
            case "/EM":
                format = slashDateWithYearAtEndUS;
                break;
            case "/SD":
                format = slashDateWithYearAtStartEU;
                break;
            case "/SM":
                format = slashDateWithYearAtStartUS;
                break;
            case ".ED":
                format = dotDateWithYearAtEndEU;
                break;
            case ".EM":
                format = dotDateWithYearAtEndUS;
                break;
            case ".SD":
                format = dotDateWithYearAtStartEU;
                break;
            case ".SM":
                format = dotDateWithYearAtStartUS;
                break;
        }
        return format;
    }

    private enum DelimiterType {
        HYPHEN_YEAR_AT_START,
        HYPHEN_YEAR_AT_END,
        SLASH_YEAR_AT_START,
        SLASH_YEAR_AT_END,
        DOT_YEAR_AT_START,
        DOT_YEAR_AT_END
    }
}
