package com.runstart.BmobBean;

import cn.bmob.v3.BmobObject;

/**
 * Created by user on 17-9-26.
 */
public class Friend extends BmobObject {
    private String userObjectId;
    private String friendObjectId;
    private int isFriend;//0表示是非好友,1表示好友
    private String likeDate;
    //private int likeState;


    public Friend(){}
    public Friend(String userObjectId, String friendObjectId, int isFriend, String likeDate) {
        this.userObjectId = userObjectId;
        this.friendObjectId = friendObjectId;
        this.isFriend = isFriend;
        this.likeDate = likeDate;
    }

    public String getUserObjectId() {
        return userObjectId;
    }

    public void setUserObjectId(String userObjectId) {
        this.userObjectId = userObjectId;
    }

    public String getFriendObjectId() {
        return friendObjectId;
    }

    public void setFriendObjectId(String friendObjectId) {
        this.friendObjectId = friendObjectId;
    }

    public int isFriend() {
        return isFriend;
    }

    public void setFriend(int isFriend) {
        isFriend = isFriend;
    }

    public String getLikeDate() {
        return likeDate;
    }

    public void setLikeDate(String likeDate) {
        this.likeDate = likeDate;
    }

    /*public int getLikeState() {
        return likeState;
    }

    public void setLikeState(int likeState) {
        this.likeState = likeState;
    }*/

    @Override
    public String toString() {
        return "Friend{" +
                "userObjectId='" + userObjectId + '\'' +
                ", friendObjectId='" + friendObjectId + '\'' +
                ", isFriend=" + isFriend +
                ", likeDate=" + likeDate +
                //", likeState=" + likeState +
                '}';
    }
}
