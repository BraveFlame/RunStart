package com.runstart.bean;

import android.graphics.Bitmap;

/**
 * Created by zhouj on 2017-10-07.
 */

public class MessageRecord {
    /**
     * 定义字段
     */
    private Bitmap userImage;
    private String userName;
    private String userMessage;
    private String userTime;
    private String msgcount;
    private String friendObjectId;
    /**
     * 初始化字段的getter/setter属性
     */
    public Bitmap getUserImage() {
        return userImage;
    }

    public void setUserImage(Bitmap userImage) {
        this.userImage = userImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getUserTime() {
        return userTime;
    }

    public void setUserTime(String userTime) {
        this.userTime = userTime;
    }

    public String getMsgcount() {
        return msgcount;
    }

    public void setMsgcount(String msgcount) {
        this.msgcount = msgcount;
    }

    public String getFriendObjectId() {
        return friendObjectId;
    }

    public void setFriendObjectId(String friendObjectId) {
        this.friendObjectId = friendObjectId;
    }
}
