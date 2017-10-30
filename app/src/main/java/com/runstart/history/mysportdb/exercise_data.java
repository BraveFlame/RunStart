package com.runstart.history.mysportdb;

import cn.bmob.v3.BmobObject;

/**
 * Created by 10605 on 2017/9/8.
 */

public class exercise_data extends BmobObject {

    private int userID;
    private int time;
    private int month;
    private int distance;
    private int day;
    private int cal;
    private int week;

    public int getID() {
        return userID;
    }
    public void setID(int id) {
        this.userID=id;
    }
    public int getTime(){
        return time;
    }
    public void setTime(int time){
        this.time=time;
    }
    public int getMonth(){
        return month;
    }
    public void setMonth(int month){
        this.month=month;
    }
    public int getDistance(){
        return distance;
    }
    public void setDistance(int distance){
        this.distance=distance;
    }
    public int getDay(){
        return day;
    }
    public void setDay(int day){
        this.day=day;
    }
    public int getWeek(){
        return week;
    }
    public void setWeek(int week){
        this.week=week;
    }
    public int getCal(){
        return cal;
    }
    public void setCal(int cal){
        this.cal=cal;
    }
}