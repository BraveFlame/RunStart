package com.runstart.friend;

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
    private int type;

    public MsgChat(String userObjectId, String friendObjectId, String content, String leaveMsg, int type) {
        this.userObjectId = userObjectId;
        this.friendObjectId = friendObjectId;
        this.content = content;
        this.leaveMsg = leaveMsg;
        this.type = type;
    }

    public MsgChat() {
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
}


