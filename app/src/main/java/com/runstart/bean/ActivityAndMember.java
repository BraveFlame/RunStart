package com.runstart.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by 10605 on 2017/9/26.
 */

public class ActivityAndMember extends BmobObject{

    private String memberId;
    private String activityId;
    private int signInTimes;
    private int signInFlag;

    public String getMemberId(){
        return memberId;
    }
    public String getActivityId(){
        return activityId;
    }
    public int getSignInTimes(){
        return signInTimes;
    }
    public int getSignInFlag(){
        return signInFlag;
    }

    public void setMemberId(String memberId){
        this.memberId=memberId;
    }
    public void setActivityId(String activityId){
        this.activityId=activityId;
    }
    public void setSignInTimes(int signInTimes){
        this.signInTimes=signInTimes;
    }
    public void setSignInFlag(int signInFlag){
        this.signInFlag=signInFlag;
    }
}
