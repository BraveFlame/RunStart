package com.runstart.friend;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by g on 2017/10/2.
 */

public class BmobJdonChat {

    /*
    {"appKey":"6bbf7fb6372e29f54eead6a98a204621","tableName":"MsgChat","objectId":"","action":"updateTable",
    "data":{"content":"大家好","createdAt":"2017-10-02 11:55:32","friendObjectId":"54c48f1bfc","objectId":"2cac02631b","type":1,
    "updatedAt":"2017-10-02 11:55:32","userObjectId":"033d152e41"}}
     */
    public  static String jsonToString(JSONObject data,String point){
        String content="";
        try {
            JSONObject jsonObject=data.getJSONObject("data");
           content=jsonObject.getString(point);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    public static List<MsgChat> getLeaveMsg(List<MsgChat>list,String leaveMsg){
        String[]msg;
        msg=leaveMsg.split("\\.\\*\\.\\|\\*\\|");
        for(int i=0;i<msg.length-1;i++){
            MsgChat msgChat=new MsgChat();
            msgChat.setRecContent(msg[i+1]);
            msgChat.setType(MsgChat.TYPE_RECEIVED);
            list.add(msgChat);
        }
        return list;
    }
}
