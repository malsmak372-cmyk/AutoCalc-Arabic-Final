package com.dreamfish.com.autocalc.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public final class DateUtils {
  
  public static String FORMAT_SHORT = "yyyy-MM-dd";
  
  public static String FORMAT_LONG = "yyyy-MM-dd HH:mm:ss";
  
  public static String FORMAT_FULL = "yyyy-MM-dd HH:mm:ss.S";
  
  public static String FORMAT_SHORT_CN = "yyyy 年 MM 月 dd 日";
  
  public static String FORMAT_LONG_CN = "yyyy 年 MM 月 dd 日 HH 时 mm 分 ss 秒";
  public static String FORMAT_LONG_CN_FOR_TICK = "y 年 M 月 d 天 H 时 m 分 s 秒";
  
  public static String FORMAT_FULL_CN = "yyyy年MM月dd日  HH时mm分ss秒SSS毫秒";

  
  public static String getDatePattern() {
    return FORMAT_LONG;
  }

  
  public static String getNow() {
    return format(new Date());
  }

  
  public static String getNow(String format) {
    return format(new Date(), format);
  }

  
  public static String format(Date date) {
    return format(date, getDatePattern());
  }

  
  public static String format(Date date, String pattern) {
    String returnValue = "";
    if (date != null) {
      SimpleDateFormat df = new SimpleDateFormat(pattern);
      returnValue = df.format(date);
    }
    return (returnValue);
  }

  
  public static Date parse(String strDate) {
    return parse(strDate, getDatePattern());
  }

  
  public static Date parse(String strDate, String pattern) {
    SimpleDateFormat df = new SimpleDateFormat(pattern);
    try {
      return df.parse(strDate);
    } catch (ParseException e) {
      e.printStackTrace();
      return null;
    }
  }

  
  public static Date addMonth(Date date, int n) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.add(Calendar.MONTH, n);
    return cal.getTime();
  }

  
  public static Date addDay(Date date, int n) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.add(Calendar.DATE, n);
    return cal.getTime();
  }

  
  public static String getTimeString() {
    SimpleDateFormat df = new SimpleDateFormat(FORMAT_FULL);
    Calendar calendar = Calendar.getInstance();
    return df.format(calendar.getTime());
  }

  
  public static String getYear(Date date) {
    return format(date).substring(0, 4);
  }

  
  public static int countDays(String date) {
    long t = Calendar.getInstance().getTime().getTime();
    Calendar c = Calendar.getInstance();
    c.setTime(parse(date));
    long t1 = c.getTime().getTime();
    return (int) (t / 1000 - t1 / 1000) / 3600 / 24;
  }

  
  public static int countDays(Date date) {
    long t = Calendar.getInstance().getTime().getTime();
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    long t1 = c.getTime().getTime();
    return (int) (t / 1000 - t1 / 1000) / 3600 / 24;
  }

  
  public static int countDays(Date dateStart, Date dateEnd) {
    Calendar calendar1 = Calendar.getInstance();
    calendar1.setTime(dateStart);
    Calendar calendar2 = Calendar.getInstance();
    calendar2.setTime(dateEnd);

    int day1 = calendar1.get(Calendar.DAY_OF_YEAR);
    int day2 = calendar2.get(Calendar.DAY_OF_YEAR);
    int year1 = calendar1.get(Calendar.YEAR);
    int year2 = calendar2.get(Calendar.YEAR);

    if (year1 != year2)  
    {
      int timeDistance = 0;
      for (int i = year1 ; i < year2 ;i++){ 
        if (getIsLunarYear(i)){
          timeDistance += 366;
        }else { 
          timeDistance += 365;
        }
      }
      return  timeDistance + (day2-day1);
    }else{
      return day2-day1;
    }
  }

  
  public static int countDays(String date, String format) {
    long t = Calendar.getInstance().getTime().getTime();
    Calendar c = Calendar.getInstance();
    c.setTime(parse(date, format));
    long t1 = c.getTime().getTime();
    return (int) (t / 1000 - t1 / 1000) / 3600 / 24;
  }

  
  public static boolean getIsLunarYear(int year) {
    return year %4 == 0 && year % 100 != 0||year % 400 == 0;
  }
  
  public static int getDayOfMonth(int month, int year) {
    switch (month) {
      case 1:
      case 3:
      case 5:
      case 7:
      case 8:
      case 10:
      case 12:
        return 31;
      case 4:
      case 6:
      case 9:
      case 11:
        return 31;
      case 2:
        return getIsLunarYear(year) ? 29 : 28;
    }
    return 0;
  }
}