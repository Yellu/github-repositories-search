package com.mapprr.gitsearch;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.compat.BuildConfig;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by panxshaz on 1/Aug/16.
 */
public class Utilities {

  public static String dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

  //  ======================== Date Utilities =================================
  public static Date getStartingTimeOfDayForDate(Date date) {
    Calendar calendar = Calendar.getInstance();
    //calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
    calendar.setTimeInMillis(date.getTime());
    int d = calendar.get(Calendar.DAY_OF_MONTH);
    int m = calendar.get(Calendar.MONTH);
    int y = calendar.get(Calendar.YEAR);
    int s = 0;//calendar.get(Calendar.SECOND);
    int min = 0;//calendar.get(Calendar.MINUTE);
    int h = 0;//calendar.get(Calendar.HOUR_OF_DAY);
    return getDateFromComponents(y, m, d, h, min, s);
  }

  public static Date getNextDay(Date date) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    c.add(Calendar.DATE, 1);
    date = c.getTime();
    return date;
  }

  public static Date getDateFromComponents(int y, int m, int d, int h, int min, int s) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.DAY_OF_MONTH, d);
    calendar.set(Calendar.MONTH, m);
    calendar.set(Calendar.YEAR, y);
    calendar.set(Calendar.MILLISECOND, 0);
    calendar.set(Calendar.SECOND, s);
    calendar.set(Calendar.MINUTE, min);
    calendar.set(Calendar.HOUR_OF_DAY, h);
    Date date = calendar.getTime();
    return date;
  }

  public enum DateField {
    Year /* = Calendar.YEAR */,
    Month /* = Calendar.MONTH */,
    Day /* = Calendar.DAY_OF_MONTH */
  }

  public static Date getDate(final int year, final int month, final int day) {
    Calendar currentCalender = Calendar.getInstance();
    currentCalender.setTime(new Date());
    currentCalender.set(year, month, day);
    return currentCalender.getTime();
  }

  ///After/Before current date
  public static Date getDateAfter(int value, DateField dateField) {
    Calendar currentCalender = Calendar.getInstance();
    currentCalender.setTime(new Date());
    int field = Calendar.DAY_OF_MONTH;
    switch (dateField) {
      case Year:
        field = Calendar.YEAR; break;
      case Month:
        field = Calendar.MONTH; break;
      case Day:
        field = Calendar.DAY_OF_MONTH; break;
    }
    currentCalender.add(field, value);
    return currentCalender.getTime();
  }

  public static Date stringToDate(String dateStr) {
    if (dateStr == null) {
      if (BuildConfig.DEBUG) {
        return new Date();
      }
      return null;
    }
    Date date = null;
    try {
      DateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
      df.setTimeZone(TimeZone.getDefault());
      date = df.parse(dateStr);
    } catch (ParseException e) {
//      MISLog.printDebug("Could Not parse dateStr : " + dateStr);
      e.printStackTrace();
    }

    return date;
  }

  public static Date stringToUTCDate(String dateStr) {
    if (dateStr == null) {
//      if (BuildConfig.DEBUG) {
//        return new Date();
//      }
      return null;
    }
    Date date = null;
    try {
      DateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
     // df.setTimeZone(TimeZone.getTimeZone("UTC"));
      date = df.parse(dateStr);
    } catch (ParseException e) {
//      MISLog.printDebug("Could Not parse dateStr : " + dateStr);
      e.printStackTrace();
    }
    return date;
  }


  public static String dateToStringForDisplay(Date date) {
    return dateToUTCString(date, "dd-MM-yyyy");
  }

  public static Boolean isDateToday(Date date) {
    Date startTimeOfDay = getStartingTimeOfDayForDate(date);
    Date startingTimeOfToday = getStartingTimeOfDayForDate(new Date());
    return startTimeOfDay == startingTimeOfToday;
  }

  public static String dateToString(Date date, String format) {
    if (date == null) {
      return "";
    }
    SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
    sdf.setTimeZone(TimeZone.getDefault());
    return sdf.format(date);
  }
  public static String dateToUTCString(Date date, String format) {
    if (date == null) {
      return "";
    }
    SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    return sdf.format(date);
  }

  ///Provide negative value for days for past dates
  public static Date dateAfterDay(Date date, int days) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.add(Calendar.DAY_OF_YEAR, days);
    return cal.getTime();
  }

  ///Provide negative value for days for past dates
  public static Date dateAfterToday(int days) {
    return dateAfterDay(new Date(), days);
  }

  public static String listToString(ArrayList<String> list){
    return TextUtils.join(",", list);
  }

  public static List<String> stringToList(String string){
    List<String> items = null;

    if (string != null){
      items = Arrays.asList(string.split("\\s*,\\s*"));
    }
    return items;
  }

  public static Date beginOfDay(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTimeZone(TimeZone.getTimeZone("UTC"));
    cal.setTimeInMillis(date.getTime());
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);

    return cal.getTime();
  }

  public static Date endOfDay(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTimeZone(TimeZone.getTimeZone("UTC"));
    cal.setTimeInMillis(date.getTime());
    cal.set(Calendar.HOUR_OF_DAY, 23);
    cal.set(Calendar.MINUTE, 59);
    cal.set(Calendar.SECOND, 59);
    cal.set(Calendar.MILLISECOND, 999);

    return cal.getTime();
  }

  public static Date getEndingTimeOfDayForDate(Date date, boolean isUTCTimeZone){
    Calendar calendar = Calendar.getInstance();
    if (isUTCTimeZone){
      calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
    calendar.setTimeInMillis(date.getTime());
    int d = calendar.get(Calendar.DAY_OF_MONTH);
    int m = calendar.get(Calendar.MONTH);
    int y = calendar.get(Calendar.YEAR);
    int s = 59;//calendar.get(Calendar.SECOND);
    int min = 59;//calendar.get(Calendar.MINUTE);
    int h = 23;//calendar.get(Calendar.HOUR_OF_DAY);
    return getDateFromComponents(y, m, d, h, min, s);
  }
}
