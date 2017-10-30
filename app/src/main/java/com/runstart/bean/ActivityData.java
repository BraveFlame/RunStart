package com.runstart.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by 10605 on 2017/9/26.
 */

public class ActivityData extends BmobObject {

    private int frequency;
    private int exerciseType;
    private int exerciseAmount;
    private String creatorId;
    private String backgroundURL;
    private String activityName;
    private String activityIntroduction;

    public String getCreatorId(){
        return creatorId;
    }
    public String getBackgroundURL(){
        return backgroundURL;
    }
    public String getActivityName(){
        return activityName;
    }
    public String getActivityIntroduction(){
        return activityIntroduction;
    }
    public int getFrequency(){
        return frequency;
    }
    public int getExerciseType(){
        return exerciseType;
    }
    public int getExerciseAmount(){
        return exerciseAmount;
    }

    public void setFrequency(int frequency){
        this.frequency=frequency;
    }
    public void setExerciseType(int exerciseType){
        this.exerciseType=exerciseType;
    }
    public void setExerciseAmount(int exerciseAmount){
        this.exerciseAmount=exerciseAmount;
    }
    public void setCreatorId(String creatorId){
        this.creatorId=creatorId;
    }
    public void setBackgroundURL(String backgroundURL){
        this.backgroundURL=backgroundURL;
    }
    public void setActivityName(String activityName){
        this.activityName=activityName;
    }
    public void setActivityIntroduction(String activityIntroduction){
        this.activityIntroduction=activityIntroduction;
    }
}