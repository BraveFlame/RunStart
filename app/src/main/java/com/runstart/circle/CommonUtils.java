package com.runstart.circle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by 10605 on 2017/9/26.
 */

public class CommonUtils {

    public static String frequencyString[]={"once a day","twice a day","once a week","twice a week","three times a week","once a month"};

    //输入某个以前的时间，获取与今天的间隔的天数，若是以后的则为负数，同一天为0
    public static long getTwoDay(String sj) {
        Date date = new Date();
        long times = date.getTime();//时间戳
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(date);
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        long day = 0;
        try {
            date = myFormatter.parse(dateString);
            Date mydate = myFormatter.parse(sj);
            day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
        } catch (Exception e) {
            return 0;
        }
        return day;
    }

    public static long getYimanTwoDay(String sj) {
        int year=Integer.parseInt(sj.substring(0,4));
        int month=Integer.parseInt(sj.substring(4,6));
        int Day=Integer.parseInt(sj.substring(6,8));
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month-1);
        calendar.set(Calendar.DAY_OF_MONTH, Day);
        Calendar calendar_now=Calendar.getInstance();
        long interval=calendar_now.getTime().getTime()-calendar.getTime().getTime();
        return interval/(24 * 60 * 60 * 1000);
    }

    public static int getTwoWeek(String sj){
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = sdf.parse(sj);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            Calendar c= Calendar.getInstance();
            return (c.get(Calendar.WEEK_OF_YEAR)-calendar.get(Calendar.WEEK_OF_YEAR));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return -1;
        }
    }

    public static int getTwoMonth(String sj){
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = sdf.parse(sj);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            Calendar c= Calendar.getInstance();
            return (c.get(Calendar.MONTH)-calendar.get(Calendar.MONTH));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return -1;
        }
    }

    public static String secondsToHHmmss(int itime){
        int hour=itime/3600;
        int minute=(itime-hour*3600)/60;
        int second=itime%60;
        String str;
        if(hour<10)str="0"+hour+":";
        else str=""+hour+":";
        if(minute<10)str+=("0"+minute+":");
        else str+=minute+":";
        if(second<10)str+=("0"+second);
        else str+=""+second;
        return str;
    }

    //获取日期和时间的格式化形式
    public static String getStringToday() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }
}
