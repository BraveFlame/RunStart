package com.runstart.friend;

import cn.bmob.v3.BmobObject;

/**
 * Created by user on 17-9-28.
 */

public class MsgChat extends BmobObject{
    public static final int TYPE_RECEIVED = 0;
    public  static final int TYPE_SENT = 1;
    public String userObjectId,friendObjectId;
    private String recContent,sendContent;
    private String userLeaveMsg,friendLeaveMsg;

    public String getUserLeaveMsg() {
        return userLeaveMsg;
    }

    public void setUserLeaveMsg(String userLeaveMsg) {
        this.userLeaveMsg = userLeaveMsg;
    }

    public String getFriendLeaveMsg() {
        return friendLeaveMsg;
    }

    public void setFriendLeaveMsg(String friendLeaveMsg) {
        this.friendLeaveMsg = friendLeaveMsg;
    }

    public String getRecContent() {
        return recContent;
    }

    public void setRecContent(String recContent) {
        this.recContent = recContent;
    }

    public String getSendContent() {
        return sendContent;
    }

    public void setSendContent(String sendContent) {
        this.sendContent = sendContent;
    }

    private int type;
//    public MsgChat(String content, int type) {
//        this.content = content;
//        this.type = type;
//    }
   // public MsgChat(){};

    public int getType() {
        return type;
    }
//
//    public int getTYPE_RECEIVED() {
//        return TYPE_RECEIVED;
//    }
//
//    public int getTYPE_SENT() {
//        return TYPE_SENT;
//    }

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

 //   public void setContent(String content) {
 //       this.content = content;
  //  }

    public void setType(int type) {
        this.type = type;
    }
}


