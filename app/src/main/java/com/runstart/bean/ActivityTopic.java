package com.runstart.bean;

/**
 * Created by zhouj on 2017-09-19.
 */

public class ActivityTopic {
    /**
     * 定义字段
     */
    private String topicImage;
    private String topicTitle;
    private String userHeadImage;
    private String userName;
    private String topicProgressbar;

    /**
     * 定义getter/settter属性
     *
     */
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
}
