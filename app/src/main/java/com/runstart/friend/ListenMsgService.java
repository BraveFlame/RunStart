package com.runstart.friend;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;


import com.runstart.history.MyApplication;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.ValueEventListener;

/**
 * Created by user on 17-10-11.
 */
public class ListenMsgService extends Service {
    public static final String FILTER_STR = "com.tcl.lovesport.broadcastreceiver";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        listenMsgChanged();
    }

    //监听消息列表改变
    private Map<String, MsgChat> msgChatMap;
    private Map<String, Integer> msgCountMap;
    private BmobRealTimeData bmobRealTimeData = new BmobRealTimeData();

    private void getMsgChatMap(){
        msgChatMap = new HashMap<>();
        msgCountMap = new HashMap<>();
        new BmobQuery<MsgChat>().setSQL("select * from MsgChat where friendObjectId=?")
                .setPreparedParams(new String[]{MyApplication.applicationMap.get(MyApplication.userObjectIdKey)})
                .doSQLQuery(new SQLQueryListener<MsgChat>() {
                    @Override
                    public void done(BmobQueryResult<MsgChat> bmobQueryResult, BmobException e) {
                        List<MsgChat> msgChatList = bmobQueryResult.getResults();
                        for (MsgChat msgChat: msgChatList){
                            msgChatMap.put(msgChat.getUserObjectId(), msgChat);
                        }
                        for (Map.Entry<String, MsgChat> entry:msgChatMap.entrySet()){
                            String key = entry.getKey();
                            String data = entry.getValue().toString();
                            if (data.contains(", leaveMsg='0.*.|*|")){
                                String leaveMsg = data.split(", leaveMsg='0\\.\\*\\.\\|\\*\\|")[1];
                                msgCountMap.put(key, 1);
                                if (leaveMsg.contains(".*.|*|")){
                                    msgCountMap.put(key, leaveMsg.split("\\.\\*\\.\\|\\*\\|").length);
                                }
                            }else {
                                msgCountMap.put(key, 0);
                            }
                        }
                        //发送广播
                        Intent intent = new Intent(FILTER_STR);
                        Bundle data = new Bundle();
                        ArrayList<Map<String, Integer>> msgCountMapLoader = new ArrayList<>();
                        msgCountMapLoader.add(msgCountMap);
                        data.putSerializable("msgCountMapLoader", msgCountMapLoader);
                        intent.putExtras(data);
                        sendBroadcast(intent);
                    }
                });
    }
    private void listenMsgChanged(){
        bmobRealTimeData.start(new ValueEventListener() {
            @Override
            public void onDataChange(JSONObject data) {
                getMsgChatMap();

            }
            @Override
            public void onConnectCompleted(Exception ex) {
                if (bmobRealTimeData.isConnected()) {
                    // 监听表更新
                    bmobRealTimeData.subTableUpdate("MsgChat");
                }
            }
        });
    }
}
