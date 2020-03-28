package com.github.jaychenfe.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.TimeZone;

/**
 * @author jaychenfe
 */
public class DateUtil {

    /**
     * Base ISO 8601 Date format yyyyMMdd i.e., 20021225 for the 25th day of December in the year 2002
     */
    public static String ISO_DATE_FORMAT = "yyyyMMdd";

    /**
     * Expanded ISO 8601 Date format yyyy-MM-dd i.e., 2002-12-25 for the 25th day of December in the year 2002
     */
    public static String ISO_EXPANDED_DATE_FORMAT = "yyyy-MM-dd";

    /**
     * yyyy-MM-dd hh:mm:ss
     */
    public static String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static String DATE_PATTERN = "yyyyMMddHHmmss";

    /**
     * 则个
     */
    private static boolean LENIENT_DATE = false;


    private static Random random = new Random();
    private static final int ID_BYTES = 10;
    private static final int MONTH_OF_YEAR = 12;


    public static synchronized String generateId() {
        StringBuilder result = new StringBuilder();
        result.append(System.currentTimeMillis());
        for (int i = 0; i < ID_BYTES; i++) {
            result.append(random.nextInt(10));
        }
        return result.toString();
    }

    protected static float normalizedJulian(float julianDay) {

        return Math.round(julianDay + 0.5f) - 0.5f;
    }

    /**
     * Returns the Date from a julian. The Julian date will be converted to noon GMT,
     * such that it matches the nearest half-integer (i.e., a julian date of 1.4 gets
     * changed to 1.5, and 0.9 gets changed to 0.5.)
     *
     * @param julianDay the Julian date
     * @return the Gregorian date
     */
    public static Date toDate(float julianDay) {

        /* To convert a Julian Day Number to a Gregorian date, assume that it is for 0 hours, Greenwich time (so
         * that it ends in 0.5). Do the following calculations, again dropping the fractional part of all
         * multiplicatons and divisions. Note: This method will not give dates accurately on the
         * Gregorian Proleptic Calendar, i.e., the calendar you get by extending the Gregorian
         * calendar backwards to years earlier than 1582. using the Gregorian leap year
         * rules. In particular, the method fails if Y<400. */
        float z = (normalizedJulian(julianDay)) + 0.5f;
        float w = (int) ((z - 1867216.25f) / 36524.25f);
        float x = (int) (w / 4f);
        float a = z + 1 + w - x;
        float b = a + 1524;
        float c = (int) ((b - 122.1) / 365.25);
        float d = (int) (365.25f * c);
        float e = (int) ((b - d) / 30.6001);
        float f = (int) (30.6001f * e);
        int day = (int) (b - d - f);
        int month = (int) (e - 1);

        if (month > MONTH_OF_YEAR) {
            month = month - MONTH_OF_YEAR;
        }

        //(if Month is January or February) or C-4716 (otherwise)
        int year = (int) (c - 4715);

        if (month > 2) {
            year--;
        }

        Calendar cc = Calendar.getInstance();
        cc.set(Calendar.YEAR, year);
        // damn 0 offsets
        cc.set(Calendar.MONTH, month - 1);
        cc.set(Calendar.DATE, day);

        return cc.getTime();
    }

    /**
     * Returns the days between two dates. Positive values indicate that
     * the second date is after the first, and negative values indicate, well,
     * the opposite. Relying on specific times is problematic.
     *
     * @param early the "first date"
     * @param late  the "second date"
     * @return the days between the two dates
     */
    public static int daysBetween(Date early, Date late) {

        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(early);
        c2.setTime(late);

        return daysBetween(c1, c2);
    }

    /**
     * Returns the days between two dates. Positive values indicate that
     * the second date is after the first, and negative values indicate, well,
     * the opposite.
     *
     * @param early early
     * @param late  late
     * @return the days between two dates.
     */
    public static int daysBetween(Calendar early, Calendar late) {

        return (int) (toJulian(late) - toJulian(early));
    }

    /**
     * Return a Julian date based on the input parameter. This is
     * based from calculations found at
     * <a href="http://quasar.as.utexas.edu/BillInfo/JulianDatesG.html">Julian Day Calculations
     * (Gregorian Calendar)</a>, provided by Bill Jeffrys.
     *
     * @param c a calendar instance
     * @return the julian day number
     */
    public static float toJulian(Calendar c) {

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DATE);
        int a = year / 100;
        int b = a / 4;
        int cc = 2 - a + b;
        float e = (int) (365.25f * (year + 4716));
        float f = (int) (30.6001f * (month + 1));
        float julianDay = cc + day + e + f - 1524.5f;

        return julianDay;
    }

    /**
     * Return a Julian date based on the input parameter. This is
     * based from calculations found at
     * <a href="http://quasar.as.utexas.edu/BillInfo/JulianDatesG.html">Julian Day Calculations
     * (Gregorian Calendar)</a>, provided by Bill Jeffrys.
     *
     * @param date date
     * @return the julian day number
     */
    public static float toJulian(Date date) {

        Calendar c = Calendar.getInstance();
        c.setTime(date);

        return toJulian(c);
    }

    /**
     * @param isoString isoString
     * @param fmt       fmt
     * @param field     Calendar.YEAR/Calendar.MONTH/Calendar.DATE
     * @param amount    amount
     * @return String
     */
    public static String dateIncrease(String isoString, String fmt,
                                      int field, int amount) {

        try {
            Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(
                    "GMT"));
            cal.setTime(stringToDate(isoString, fmt, true));
            cal.add(field, amount);

            return dateToString(cal.getTime(), fmt);

        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Time Field Rolling function.
     * Rolls (up/down) a single unit of time on the given time field.
     *
     * @param isoString isoString
     * @param field     the time field.
     * @param up        Indicates if rolling up or rolling down the field value.
     * @param fmt       use formating char's
     * @return String
     * @throws ParseException if an unknown field value is given.
     */
    public static String roll(String isoString, String fmt, int field,
                              boolean up) throws ParseException {

        Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(
                "GMT"));
        cal.setTime(stringToDate(isoString, fmt));
        cal.roll(field, up);

        return dateToString(cal.getTime(), fmt);
    }

    /**
     * Time Field Rolling function.
     * Rolls (up/down) a single unit of time on the given time field.
     *
     * @param isoString isoString
     * @param field     the time field.
     * @param up        Indicates if rolling up or rolling down the field value.
     * @return String
     * @throws ParseException if an unknown field value is given.
     */
    public static String roll(String isoString, int field, boolean up) throws
            ParseException {

        return roll(isoString, DATETIME_PATTERN, field, up);
    }

    /**
     * java.util.Date
     *
     * @param dateText dateText
     * @param format   format
     * @param lenient  lenient
     * @return Date
     */
    public static Date stringToDate(String dateText, String format,
                                    boolean lenient) {

        if (dateText == null) {

            return null;
        }

        DateFormat df;

        try {

            if (format == null) {
                df = new SimpleDateFormat();
            } else {
                df = new SimpleDateFormat(format);
            }

            // setLenient avoids allowing dates like 9/32/2001
            // which would otherwise parse to 10/2/2001
            df.setLenient(false);

            return df.parse(dateText);
        } catch (ParseException e) {

            return null;
        }
    }

    /**
     * @return Timestamp
     */
    public static java.sql.Timestamp getCurrentTimestamp() {
        return new java.sql.Timestamp(System.currentTimeMillis());
    }

    /**
     * java.util.Date
     *
     * @param dateString dateString
     * @param format     format
     * @return Date
     */
    public static Date stringToDate(String dateString, String format) {

        return stringToDate(dateString, format, LENIENT_DATE);
    }

    /**
     * java.util.Date
     *
     * @param dateString dateString
     * @return Date
     */
    public static Date stringToDate(String dateString) {
        return stringToDate(dateString, ISO_EXPANDED_DATE_FORMAT, LENIENT_DATE);
    }

    /**
     * @param pattern pattern
     * @param date    date
     * @return String
     */
    public static String dateToString(Date date, String pattern) {

        if (date == null) {

            return null;
        }

        try {

            SimpleDateFormat sfDate = new SimpleDateFormat(pattern);
            sfDate.setLenient(false);

            return sfDate.format(date);
        } catch (Exception e) {

            return null;
        }
    }

    /**
     * yyyy-MM-dd
     *
     * @param date Date
     * @return String
     */
    public static String dateToString(Date date) {
        return dateToString(date, ISO_EXPANDED_DATE_FORMAT);
    }

    /**
     * @return Date
     */
    public static Date getCurrentDateTime() {
        return Calendar.getInstance().getTime();
    }

    /**
     * @param pattern pattern
     * @return String
     */
    public static String getCurrentDateString(String pattern) {
        return dateToString(getCurrentDateTime(), pattern);
    }

    /**
     * yyyy-MM-dd
     *
     * @return String
     */
    public static String getCurrentDateString() {
        return dateToString(getCurrentDateTime(), ISO_EXPANDED_DATE_FORMAT);
    }

    /**
     * 返回固定格式的当前时间
     * yyyy-MM-dd hh:mm:ss
     *
     * @return String
     */
    public static String dateToStringWithTime() {

        return dateToString(new Date(), DATETIME_PATTERN);
    }


    /**
     * yyyy-MM-dd hh:mm:ss
     *
     * @param date Date
     * @return String
     */
    public static String dateToStringWithTime(Date date) {

        return dateToString(date, DATETIME_PATTERN);
    }

    /**
     * @param date Date
     * @param days days
     * @return java.util.Date
     */
    public static Date dateIncreaseByDay(Date date, int days) {

        Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(
                "GMT"));
        cal.setTime(date);
        cal.add(Calendar.DATE, days);

        return cal.getTime();
    }

    /**
     * @param date Date
     * @param mnt  days
     * @return java.util.Date
     */
    public static Date dateIncreaseByMonth(Date date, int mnt) {

        Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(
                "GMT"));
        cal.setTime(date);
        cal.add(Calendar.MONTH, mnt);

        return cal.getTime();
    }

    /**
     * @param date Date
     * @param mnt  mnt
     * @return java.util.Date
     */
    public static Date dateIncreaseByYear(Date date, int mnt) {

        Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(
                "GMT"));
        cal.setTime(date);
        cal.add(Calendar.YEAR, mnt);

        return cal.getTime();
    }

    /**
     * @param date yyyy-MM-dd
     * @param days days
     * @return yyyy-MM-dd
     */
    public static String dateIncreaseByDay(String date, int days) {
        return dateIncreaseByDay(date, ISO_DATE_FORMAT, days);
    }

    /**
     * @param date Date
     * @param fmt  fmt
     * @param days days
     * @return String
     */
    public static String dateIncreaseByDay(String date, String fmt, int days) {
        return dateIncrease(date, fmt, Calendar.DATE, days);
    }

    /**
     * @param src    src
     * @param srcfmt srcfmt
     * @param desfmt desfmt
     * @return String
     */
    public static String stringToString(String src, String srcfmt,
                                        String desfmt) {
        return dateToString(stringToDate(src, srcfmt), desfmt);
    }

    /**
     * @param date Date
     * @return string
     */
    public static String getYear(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy");
        return formatter.format(date);
    }

    /**
     * @param date Date
     * @return string
     */
    public static String getMonth(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "MM");
        return formatter.format(date);
    }

    /**
     * @param date Date
     * @return string
     */
    public static String getDay(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "dd");
        return formatter.format(date);
    }

    public static int getDayInt(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd");
        String curDay = formatter.format(date);
        return Integer.parseInt(curDay);
    }

    /**
     * @param date Date
     * @return string
     */
    public static String getHour(Date date) {
        SimpleDateFormat formater = new SimpleDateFormat("HH");
        return formater.format(date);
    }

    public static int getMinsFromDate(Date dt) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(dt);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        return ((hour * 60) + min);
    }

    /**
     * Function to convert String to Date Object. If invalid input then current or next day date
     * is returned (Added by Ali Naqvi on 2006-5-16).
     *
     * @param str      String input in YYYY-MM-DD HH:MM[:SS] format.
     * @param isExpiry boolean if set and input string is invalid then next day date is returned
     * @return Date
     */
    public static Date convertToDate(String str, boolean isExpiry) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date dt = null;
        try {
            dt = fmt.parse(str);
        } catch (ParseException ex) {
            Calendar cal = Calendar.getInstance();
            if (isExpiry) {
                cal.add(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
            } else {
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
            }
            dt = cal.getTime();
        }
        return dt;
    }

    public static Date convertToDate(String str) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date dt = null;
        try {
            dt = fmt.parse(str);
        } catch (ParseException ex) {
            dt = new Date();
        }
        return dt;
    }

    public static String dateFromat(Date date, int minute) {
        String dateFormat = null;
        int year = Integer.parseInt(getYear(date));
        int month = Integer.parseInt(getMonth(date));
        int day = Integer.parseInt(getDay(date));
        int hour = minute / 60;
        int min = minute % 60;
        dateFormat = String.valueOf(year)
                +
                (month > 9 ? String.valueOf(month) :
                        "0" + String.valueOf(month))
                +
                (day > 9 ? String.valueOf(day) : "0" + String.valueOf(day))
                + " "
                +
                (hour > 9 ? String.valueOf(hour) : "0" + String.valueOf(hour))
                +
                (min > 9 ? String.valueOf(min) : "0" + String.valueOf(min))
                + "00";
        return dateFormat;
    }

    public static String sDateFormat() {
        return new SimpleDateFormat(DATE_PATTERN).format(Calendar.getInstance().getTime());
    }

    /**
     * @return String
     * @Description: 获得本月的第一天日期
     */
    public static String getFirstDateOfThisMonth() {

        SimpleDateFormat format = new SimpleDateFormat(ISO_EXPANDED_DATE_FORMAT);

        Calendar calendarFirst = Calendar.getInstance();
        calendarFirst.add(Calendar.MONTH, 0);
        calendarFirst.set(Calendar.DAY_OF_MONTH, 1);

        return format.format(calendarFirst.getTime());
    }

    /**
     * @return String
     * @Description: 获得本月的最后一天日期
     * @author leechenxiang
     * @date 2017年5月31日 下午1:37:50
     */
    public static String getLastDateOfThisMonth() {
        SimpleDateFormat format = new SimpleDateFormat(ISO_EXPANDED_DATE_FORMAT);

        Calendar calendarLast = Calendar.getInstance();
        calendarLast.setTime(new Date());
        calendarLast.getActualMaximum(Calendar.DAY_OF_MONTH);

        return format.format(calendarLast.getTime());
    }

    /**
     * @param strDate   strDate
     * @param formatter formatter
     * @return 是否有效
     * @Description: 判断字符串日期是否匹配指定的格式化日期
     */
    public static boolean isValidDate(String strDate, String formatter) {
        SimpleDateFormat sdf;
        ParsePosition pos = new ParsePosition(0);

        if (StringUtils.isBlank(strDate) || StringUtils.isBlank(formatter)) {
            return false;
        }
        try {
            sdf = new SimpleDateFormat(formatter);
            sdf.setLenient(false);
            Date date = sdf.parse(strDate, pos);
            if (date == null) {
                return false;
            }
            return pos.getIndex() <= sdf.format(date).length();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
