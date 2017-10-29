package com.runstart.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by zhonghao.song on 17-9-30.
 */

public class DaySport extends BmobObject {
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public double getMonth() {
        return month;
    }

    public void setMonth(double month) {
        this.month = month;
    }

    public double getWeek() {
        return week;
    }

    public void setWeek(double week) {
        this.week = week;
    }

    public double getDay() {
        return day;
    }

    public void setDay(double day) {
        this.day = day;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getCal() {
        return cal;
    }

    public void setCal(double cal) {
        this.cal = cal;
    }

    public double getType() {
        return type;
    }

    public void setType(double type) {
        this.type = type;
    }

    private double month,week,day,distance,time,cal,type;
    private String userID;

}
