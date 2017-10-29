package com.runstart.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by zhouj on 2017-10-09.
 */

public class AboutSport extends BmobObject{
    /**
     * AboutSport字段属性
     */
    private String newsImage;       //源图片
    private String newsBgImage;     //背景图片
    private String newsTitle;
    private String newsContent;
    /**
     * AboutSport的getter/setter方法
     */
    public String getNewsImage() {
        return newsImage;
    }

    public void setNewsImage(String newsImage) {
        this.newsImage = newsImage;
    }

    public String getNewsBgImage() {
        return newsBgImage;
    }

    public void setNewsBgImage(String newsBgImage) {
        this.newsBgImage = newsBgImage;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewsContent() {
        return newsContent;
    }

    public void setNewsContent(String newsContent) {
        this.newsContent = newsContent;
    }
}
