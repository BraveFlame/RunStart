package com.runstart.friend;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;


import com.runstart.R;
import com.runstart.history.MyApplication;
import com.runstart.mine.MineMessageRecordActivity;

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


        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        listenMsgChanged();
    }

    //监听消息列表改变
    private Map<String, MsgChat> msgChatMap;
    private Map<String, Integer> msgCountMap;
    private BmobRealTimeData bmobRealTimeData = new BmobRealTimeData();
    private int lastMsgCount = 0;

    private void getMsgChatMap(){
        msgChatMap = new HashMap<>();
        msgCountMap = new HashMap<>();
        new BmobQuery<MsgChat>().setSQL("select * from MsgChat where friendObjectId=?")
                .setPreparedParams(new String[]{MyApplication.applicationMap.get(MyApplication.userObjectIdKey)})
                .doSQLQuery(new SQLQueryListener<MsgChat>() {
                    @Override
                    public void done(BmobQueryResult<MsgChat> bmobQueryResult, BmobException e) {
                        if (e == null){
                            List<MsgChat> msgChatList = bmobQueryResult.getResults();
                            for (MsgChat msgChat: msgChatList){
                                msgChatMap.put(msgChat.getUserObjectId(), msgChat);
                            }
                            for (Map.Entry<String, MsgChat> entry:msgChatMap.entrySet()){
                                String key = entry.getKey();
                                String data = entry.getValue().getLeaveMsg();
                                if (data == null || data.equals("0")){
                                    msgCountMap.put(key, 0);
                                } else {
                                    msgCountMap.put(key, data.split("\\.\\*\\.\\|\\*\\|").length - 1);
                                }

                                int count = 0;
                                for (Map.Entry<String, Integer> entry1: msgCountMap.entrySet()){
                                    count += entry1.getValue();
                                }
                                if (count > lastMsgCount){
                                    notifyNewMsg(MineMessageRecordActivity.class,
                                            R.mipmap.ic_xiaoxi, "you have " + count + " new messages to read");
                                }
                                lastMsgCount = count;
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

    private NotificationManager notificationManager;
    private void notifyNewMsg(Class clazz, int iconId, String content){
        Intent intent = new Intent(ListenMsgService.this, clazz);
        PendingIntent pi = PendingIntent.getActivity(ListenMsgService.this, 0, intent, 0);

        Notification notification = new Notification.Builder(ListenMsgService.this)
                .setAutoCancel(true).setTicker("New messages").setContentTitle("New messages").setSmallIcon(iconId)
                .setContentText(content).setDefaults(Notification.DEFAULT_VIBRATE)
                .setWhen(System.currentTimeMillis()).setContentIntent(pi)
                .build();
        notificationManager.notify(NotificationManager.IMPORTANCE_NONE, notification);
    }
}
