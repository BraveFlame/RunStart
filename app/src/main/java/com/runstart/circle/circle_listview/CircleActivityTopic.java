package com.runstart.circle.circle_listview;

import com.runstart.bean.Friend;

/**
 * Created by 10605 on 2017/10/9.
 */
public class CircleActivityTopic implements Comparable<CircleActivityTopic>{
    /**
     * 定义字段
     */
    private int number;
    private String userHeadImage;
    private String userName;
    private String userDistance;
    private boolean isLiked;
    private int likeTimes;
    private Friend friend;
    private String memberId;

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public Friend getFriend() {
        return friend;
    }

    public void setFriend(Friend friend) {
        this.friend = friend;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getUserHeadImage() {
        return userHeadImage;
    }

    public void setUserHeadImage(String userHeadImage) {
        this.userHeadImage = userHeadImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserDistance() {
        return userDistance;
    }

    public void setUserDistance(String userDistance) {
        this.userDistance = userDistance;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public int getLikeTimes() {
        return likeTimes;
    }

    public void setLikeTimes(int likeTimes) {
        this.likeTimes = likeTimes;
    }

    private Integer getInteger(String str){
        return Integer.parseInt(str.substring(0,str.length()-1));
    }
    public int compareTo(CircleActivityTopic arg0) {
        return getInteger(arg0.getUserDistance()).compareTo(getInteger(this.getUserDistance()));
    }
}

