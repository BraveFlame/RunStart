package com.runstart.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by zhouj on 2017-09-28.
 */

public class ExerciseDiary extends BmobObject{
    /*
    定义字段
     */
    private String userObjectId;
    private String exDairyTitle;
    private String exDairyContent;
    private String exDairyDate;
    private boolean isCheck;

    public String getUserObjectId() {
        return userObjectId;
    }

    public void setUserObjectId(String userObjectId) {
        this.userObjectId = userObjectId;
    }
    /*
    定义字段属性的setter/getter的方法
     */

    public void setExDairyTitle(String exDairyTitle) {
        this.exDairyTitle = exDairyTitle;
    }

    public String getExDairyTitle() {
        return exDairyTitle;
    }

    public void setExDairyContent(String exDairyContent) {
        this.exDairyContent = exDairyContent;
    }

    public String getExDairyContent() {
        return exDairyContent;
    }

    public void setExDairyDate(String exDairyDate) {
        this.exDairyDate = exDairyDate;
    }

    public String getExDairyDate() {
        return exDairyDate;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public boolean isCheck() {
        return isCheck;
    }
}
