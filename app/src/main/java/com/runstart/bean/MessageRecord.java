package com.runstart.bean;

/**
 * Created by zhouj on 2017-10-07.
 */

public class MessageRecord {
    /**
     * 定义字段
     */
    private String userImage;
    private String userName;
    private String userMessage;
    private String userTime;
    /**
     * 初始化字段的getter/setter属性
     */
    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
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
}
