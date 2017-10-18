package com.runstart.BmobBean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

/**
 * Created by User on 17-9-15.
 */
public class User extends BmobObject implements Serializable {
    private String phoneNumber;
    private String password;
    private String nickName;
    private String headerImageUri;
    private int likeNumberForHistory;
    private int walkDistance;
    private int runDistance;
    private int rideDistance;
    private int walkKcal;
    private int runKcal;
    private int rideKcal;
    private int walkTime;
    private int runTime;
    private int rideTime;

    private String location,mailBox;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMailBox() {
        return mailBox;
    }

    public void setMailBox(String mailBox) {
        this.mailBox = mailBox;
    }

    public User(){}

    public User(String phoneNumber, String password, String nickName, String headerImageUri, int likeNumberForHistory,
                int walkDistance, int runDistance, int rideDistance, int walkKcal, int runKcal, int rideKcal, int walkTime,
                int runTime, int rideTime) {
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.nickName = nickName;
        this.headerImageUri = headerImageUri;
        this.likeNumberForHistory = likeNumberForHistory;
        this.walkDistance = walkDistance;
        this.runDistance = runDistance;
        this.rideDistance = rideDistance;
        this.walkKcal = walkKcal;
        this.runKcal = runKcal;
        this.rideKcal = rideKcal;
        this.walkTime = walkTime;
        this.runTime = runTime;
        this.rideTime = rideTime;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeaderImageUri() {
        return headerImageUri;
    }

    public void setHeaderImageUri(String headerImageUri) {
        this.headerImageUri = headerImageUri;
    }

    public int getLikeNumberForHistory() {
        return likeNumberForHistory;
    }

    public void setLikeNumberForHistory(int likeNumberForHistory) {
        this.likeNumberForHistory = likeNumberForHistory;
    }

    public int getWalkDistance() {
        return walkDistance;
    }

    public void setWalkDistance(int walkDistance) {
        this.walkDistance = walkDistance;
    }

    public int getRunDistance() {
        return runDistance;
    }

    public void setRunDistance(int runDistance) {
        this.runDistance = runDistance;
    }

    public int getRideDistance() {
        return rideDistance;
    }

    public void setRideDistance(int rideDistance) {
        this.rideDistance = rideDistance;
    }

    public int getWalkKcal() {
        return walkKcal;
    }

    public void setWalkKcal(int walkKcal) {
        this.walkKcal = walkKcal;
    }

    public int getRunKcal() {
        return runKcal;
    }

    public void setRunKcal(int runKcal) {
        this.runKcal = runKcal;
    }

    public int getRideKcal() {
        return rideKcal;
    }

    public void setRideKcal(int rideKcal) {
        this.rideKcal = rideKcal;
    }

    public int getWalkTime() {
        return walkTime;
    }

    public void setWalkTime(int walkTime) {
        this.walkTime = walkTime;
    }

    public int getRunTime() {
        return runTime;
    }

    public void setRunTime(int runTime) {
        this.runTime = runTime;
    }

    public int getRideTime() {
        return rideTime;
    }

    public void setRideTime(int rideTime) {
        this.rideTime = rideTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", password='" + password + '\'' +
                ", nickName='" + nickName + '\'' +
                ", headerImageUri='" + headerImageUri + '\'' +
                ", likeNumberForHistory=" + likeNumberForHistory +
                ", walkDistance=" + walkDistance +
                ", runDistance=" + runDistance +
                ", rideDistance=" + rideDistance +
                ", walkKcal=" + walkKcal +
                ", runKcal=" + runKcal +
                ", rideKcal=" + rideKcal +
                ", walkTime=" + walkTime +
                ", runTime=" + runTime +
                ", rideTime=" + rideTime +
                '}';
    }
}
