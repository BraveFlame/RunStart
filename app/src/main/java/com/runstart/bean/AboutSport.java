package com.runstart.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by zhouj on 2017-10-09.
 */

public class AboutSport extends BmobObject{
    /**
     * AboutSport字段属性
     */
    private String newsImage;
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
