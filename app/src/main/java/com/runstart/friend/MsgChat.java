package com.runstart.friend;

import android.graphics.Bitmap;

import cn.bmob.v3.BmobObject;

/**
 * Created by user on 17-9-28.
 */

public class MsgChat extends BmobObject {
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SENT = 1;
    public String userObjectId, friendObjectId;
    private String content;
    private String leaveMsg;
    private String time;
    private int type;
    private Bitmap bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public MsgChat(String userObjectId, String friendObjectId, String content, String leaveMsg, int type) {
        this.userObjectId = userObjectId;
        this.friendObjectId = friendObjectId;
        this.content = content;
        this.leaveMsg = leaveMsg;
        this.type = type;
    }

    public MsgChat() {
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLeaveMsg() {
        return leaveMsg;
    }

    public void setLeaveMsg(String leaveMsg) {
        this.leaveMsg = leaveMsg;
    }

    public int getType() {
        return type;
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


    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "MsgChat{" +
                "userObjectId='" + userObjectId + '\'' +
                ", friendObjectId='" + friendObjectId + '\'' +
                ", content='" + content + '\'' +
                ", leaveMsg='" + leaveMsg + '\'' +
                ", time='" + time + '\'' +
                ", type=" + type +
                '}';
    }
}


