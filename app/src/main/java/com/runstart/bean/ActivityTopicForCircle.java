package com.runstart.bean;

/**
 * Created by zhouj on 2017-09-19.
 */

public class ActivityTopicForCircle implements Comparable<ActivityTopicForCircle>{
    /**
     * 定义字段
     */
    private String ADid;
    private String topicImage;
    private String topicTitle;
    private String userHeadImage;
    private String userName;
    private String topicProgressbar;

    /**
     * 定义getter/settter属性
     *
     */
    public String getADid() {
        return ADid;
    }

    public void setADid(String ADid) {
        this.ADid = ADid;
    }

    public void setTopicImage(String topicImage) {
        this.topicImage = topicImage;
    }
    public String getTopicImage() {
        return topicImage;
    }

    public void setTopicTitle(String topicTitle) {
        this.topicTitle = topicTitle;
    }

    public String getTopicTitle() {
        return topicTitle;
    }

    public void setUserHeadImage(String userHeadImage) {
        this.userHeadImage = userHeadImage;
    }

    public String getUserHeadImage() {
        return userHeadImage;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setTopicProgressbar(String topicProgressbar) {
        this.topicProgressbar = topicProgressbar;
    }

    public String getTopicProgressbar() {
        return topicProgressbar;
    }

    @Override
    public int compareTo(ActivityTopicForCircle o) {
        int result=(o.getTopicImage()).compareTo(this.getTopicImage());
        if (result==0)result=o.getUserHeadImage().compareTo(this.getUserHeadImage());
        if (result==0)result=o.getTopicTitle().compareTo(this.getTopicTitle());
        if (result==0)result=o.getUserName().compareTo(this.getUserName());
        return result;
    }
}
