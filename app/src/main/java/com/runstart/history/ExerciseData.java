package com.runstart.history;

/**
 * Created by 10605 on 2017/9/18.
 */

public class ExerciseData {
    int month0;
    int day0;
    int dayOfYear;
    int week;
    int type;
    double day_time;
    double month_time;
    double week_time;
    double day_distance[]=new double[4];
    double week_distance[]=new double[4];
    double month_distance[]=new double[4];
    double day_cal;
    double week_cal;
    double month_cal;

    String objectUserId;

    public int getMonth0() {
        return month0;
    }

    public void setMonth0(int month0) {
        this.month0 = month0;
    }

    public int getDay0() {
        return day0;
    }

    public void setDay0(int day0) {
        this.day0 = day0;
    }

    public int getDayOfYear() {
        return dayOfYear;
    }

    public void setDayOfYear(int dayOfYear) {
        this.dayOfYear = dayOfYear;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getDay_time() {
        return day_time;
    }

    public void setDay_time(double day_time) {
        this.day_time = day_time;
    }

    public double getMonth_time() {
        return month_time;
    }

    public void setMonth_time(double month_time) {
        this.month_time = month_time;
    }

    public double getWeek_time() {
        return week_time;
    }

    public void setWeek_time(double week_time) {
        this.week_time = week_time;
    }

    public double[] getDay_distance() {
        return day_distance;
    }

    public void setDay_distance(double[] day_distance) {
        this.day_distance = day_distance;
    }

    public double[] getWeek_distance() {
        return week_distance;
    }

    public void setWeek_distance(double[] week_distance) {
        this.week_distance = week_distance;
    }

    public double[] getMonth_distance() {
        return month_distance;
    }

    public void setMonth_distance(double[] month_distance) {
        this.month_distance = month_distance;
    }

    public double getDay_cal() {
        return day_cal;
    }

    public void setDay_cal(double day_cal) {
        this.day_cal = day_cal;
    }

    public double getWeek_cal() {
        return week_cal;
    }

    public void setWeek_cal(double week_cal) {
        this.week_cal = week_cal;
    }

    public double getMonth_cal() {
        return month_cal;
    }

    public void setMonth_cal(double month_cal) {
        this.month_cal = month_cal;
    }


}
