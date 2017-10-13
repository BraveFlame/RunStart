package com.runstart.mine;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.runstart.BmobBean.User;
import com.runstart.R;
import com.runstart.bean.MessageRecord;
import com.runstart.friend.ChatActivity;
import com.runstart.friend.ListenMsgService;
import com.runstart.friend.LocalChatLog;
import com.runstart.friend.MsgChat;
import com.runstart.help.ActivityCollector;
import com.runstart.history.MyApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SQLQueryListener;

/**
 * Created by zhouj on 2017-09-21.
 */

public class MineMessageRecordActivity extends Activity implements View.OnClickListener{
    //定义fragment_mine_messagerecord_title.xml的布局组件
    private ImageView iv_zuojiantou;
    //定义fragment_mine_messagerecord_content.xml的布局组件
    private ListView meRecordListView;
    //定义ListView的适配器meRecordAdapter
    private MeRecordAdapter meRecordAdapter;
    //定义List列表来保存MessageRecord的相应数据信息
    private List<MessageRecord> messageRecordList=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_mine_messagerecord);
        ActivityCollector.addActivity(this);
        getMessageRecordData();
        initMeRecordView();
        initListView();

        localChatLog = LocalChatLog.getLocalChatLog(MineMessageRecordActivity.this);
    }

    @Override
    public void onResume() {
        super.onResume();
        queryToMeMsgChat();
        IntentFilter filter = new IntentFilter(ListenMsgService.FILTER_STR);
        registerReceiver(new MsgCountReceiver(), filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    /**
     * 初始化组件
     */
    public void initMeRecordView(){
        iv_zuojiantou = (ImageView) findViewById(R.id.mine_messagerecord_iv_zuojiantou);
        iv_zuojiantou.setOnClickListener(this);
        meRecordListView = (ListView) findViewById(R.id.mine_messagerecord_listview);
    }

    public void initListView(){
        meRecordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (((TextView)view.findViewById(R.id.friendObjectId)).getText() != null){
                    String friendObjectId = ((TextView)view.findViewById(R.id.friendObjectId)).getText().toString();
                    if (friendObjectId.length() != 0){
                        goChat(friendObjectId);
                        view.findViewById(R.id.mine_messagerecord_listitem_tv_msgCount).setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(MineMessageRecordActivity.this, "data loading ...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 初始化MessageRecord的相应数据
     */
    public void getMessageRecordData() {
        queryToMeMsgChat();
    }
    /**
     * 提供给listView适配器
     */
    public void useListViewAdapter(){
        meRecordAdapter = new MeRecordAdapter(MineMessageRecordActivity.this);
        meRecordAdapter.setMeRecordList(messageRecordList);
        meRecordListView.setAdapter(meRecordAdapter);
    }
    /**
     * onClickListener事件监听的总方法
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mine_messagerecord_iv_zuojiantou:
                MineMessageRecordActivity.this.finish();
                break;
            default:
                break;
        }
    }

    //消息相关
    public class MsgCountReceiver extends BroadcastReceiver {
        public MsgCountReceiver(){
            super();
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            queryToMeMsgChat();
        }
    }

    private Map<String, MsgChat> toMeMsgChatMap = new HashMap<>();
    private Map<String, MsgChat> toFriendMsgChatMap = new HashMap<>();
    private void queryToMeMsgChat() {
        new BmobQuery<MsgChat>().setSQL("select * from MsgChat where friendObjectId=?")
                .setPreparedParams(new String[]{MyApplication.applicationMap.get(MyApplication.userObjectIdKey)})
                .doSQLQuery(new SQLQueryListener<MsgChat>() {
                    @Override
                    public void done(BmobQueryResult<MsgChat> bmobQueryResult, BmobException e) {
                        if (e == null) {
                            for (MsgChat msgChat: bmobQueryResult.getResults()){
                                toMeMsgChatMap.put(msgChat.getUserObjectId(), msgChat);

                                String data = msgChat.getLeaveMsg();
                                if (data == null || data.equals("0")) {
                                    msgCountMap.put(msgChat.getUserObjectId(), 0);
                                } else {
                                    int count = data.split("\\.\\*\\.\\|\\*\\|").length - 1;
                                    msgCountMap.put(msgChat.getUserObjectId(), count);
                                }
                                queryToFriendMsgChat(msgChat.getUserObjectId());
                                queryUser(msgChat.getUserObjectId());

                            }
                        } else {
                            //Toast.makeText(MineMessageRecordActivity.this, "initialing messages failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void queryToFriendMsgChat(String friendObjectId){
        if (Thread.activeCount() >= 10){
            try {
                Thread.sleep(200);
            } catch (Exception e1){
                e1.printStackTrace();
            }
        }
        new BmobQuery<MsgChat>().setSQL("select * from MsgChat where userObjectId=? and friendObjectId=?")
                .setPreparedParams(new String[]{MyApplication.applicationMap.get(MyApplication.userObjectIdKey), friendObjectId})
                .doSQLQuery(new SQLQueryListener<MsgChat>() {
                    @Override
                    public void done(BmobQueryResult<MsgChat> bmobQueryResult, BmobException e) {
                        if (e == null) {
                            synchronized (MsgChat.class){
                                MsgChat msgChat = bmobQueryResult.getResults().get(0);
                                toFriendMsgChatMap.put(msgChat.getFriendObjectId(), bmobQueryResult.getResults().get(0));
                            }
                        } else {
                            //Toast.makeText(MineMessageRecordActivity.this, "initialing messages failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private Map<String, Integer> msgCountMap = new HashMap<>();
    private Map<String, User> userMap = new HashMap<>();
    private Map<String, Bitmap> imageMap = new HashMap<>();
    private LocalChatLog localChatLog;
    private void queryUser(final String objectId){
        final BmobQuery<User> query = new BmobQuery();
        query.setLimit(2);
        query.getObject(objectId, new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if(e==null){
                    synchronized (User.class){
                        userMap.put(objectId, user);
                        if ((toFriendMsgChatMap.size() == toMeMsgChatMap.size()) && (userMap.size() == toMeMsgChatMap.size())){
                            showResult();
                        }
                    }
                    queryBitMap(user);
                }else{
                    e.printStackTrace();
                    //Toast.makeText(MineMessageRecordActivity.this, "loading friends failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void queryBitMap(User user){
        final int objectIdLength = user.getObjectId().length();
        String saveName = user.getObjectId() + ".png";
        String imageUri = user.getHeaderImageUri();
        File saveFile = new File(Environment.getExternalStorageDirectory() + File.separator + "lovesportimage", saveName);
        if (imageUri == null || imageUri.length() == 0){
            synchronized (Bitmap.class){
                imageMap.put(user.getObjectId(), null);
                if ((toFriendMsgChatMap.size() == toMeMsgChatMap.size()) && (userMap.size() == toMeMsgChatMap.size())){
                    showResult();
                }
                return;
            }
        }
        new BmobFile(saveName, "", imageUri).download(saveFile, new DownloadFileListener() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null){
                    synchronized (Bitmap.class){
                        imageMap.put(s.substring(s.length() - objectIdLength - 4, s.length() - 4), BitmapFactory.decodeFile(s));
                        if ((toFriendMsgChatMap.size() == toMeMsgChatMap.size()) && (userMap.size() == toMeMsgChatMap.size())){
                            showResult();
                        }
                    }
                }else {
                    Toast.makeText(MineMessageRecordActivity.this, "load friends' images failed", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onProgress(Integer integer, long l) {}
        });
    }

    private void showResult(){
        messageRecordList = new ArrayList<>();
        for (Map.Entry<String, User> entry: userMap.entrySet()){
            MessageRecord messageRecord=new MessageRecord();
            String key = entry.getKey();

            messageRecord.setUserImage(imageMap.get(key));

            messageRecord.setFriendObjectId(key);

            if (userMap.get(key) == null){
                messageRecord.setUserName("");
            }else {
                messageRecord.setUserName(userMap.get(key).getNickName());
            }

            messageRecord.setMsgcount(msgCountMap.get(key) + "");
            if (msgCountMap.get(key) > 99){
                messageRecord.setMsgcount("99+");
            }

            MsgChat toMeMsgChat = toMeMsgChatMap.get(key);
            MsgChat toFriendMsgChat = toFriendMsgChatMap.get(key);
            String toMeData = toMeMsgChat.getLeaveMsg();
            String toFriendData = toFriendMsgChat.getLeaveMsg();
            if (toMeData != null && ! toMeData.equals("0")){
                String[] leaveMsgs = toMeData.split("\\.\\*\\.\\|\\*\\|");
                String lastLeaveMsg = leaveMsgs[leaveMsgs.length - 1];
                if (lastLeaveMsg.contains("http://bmob-cdn-14232.b0.upaiyun.com")){
                    messageRecord.setUserMessage("[image]");
                } else {
                    messageRecord.setUserMessage(lastLeaveMsg.substring(0, lastLeaveMsg.length() - 19));
                }
                messageRecord.setUserTime(lastLeaveMsg.substring(lastLeaveMsg.length() - 19));
            } else if(toFriendData != null && ! toFriendData.equals("0")){
                String[] leaveMsgs = toFriendData.split("\\.\\*\\.\\|\\*\\|");
                String lastLeaveMsg = leaveMsgs[leaveMsgs.length - 1];
                if (lastLeaveMsg.contains("http://bmob-cdn-14232.b0.upaiyun.com")){
                    messageRecord.setUserMessage("[image]");
                } else {
                    messageRecord.setUserMessage(lastLeaveMsg.substring(0, lastLeaveMsg.length() - 19));
                }
                messageRecord.setUserTime(lastLeaveMsg.substring(lastLeaveMsg.length() - 19));
            } else {
                String localMsgChat = localChatLog.getLastMsgChat(toFriendMsgChat.getObjectId(),toMeMsgChat.getObjectId() );
                Log.e("chatDbb",toFriendMsgChat.getObjectId()+"  "+toMeMsgChat.getObjectId() );
                if ("".equals(localMsgChat)){
                    messageRecord.setUserMessage("no messages to show");
                    messageRecord.setUserTime("");
                } else {
                    if (localMsgChat.contains("http://bmob-cdn-14232.b0.upaiyun.com")){
                        messageRecord.setUserMessage("[image]");
                    } else {
                        messageRecord.setUserMessage(localMsgChat.substring(0, localMsgChat.length() - 19));
                    }
                    messageRecord.setUserTime(localMsgChat.substring(localMsgChat.length() - 19));
                }
            }
            messageRecordList.add(messageRecord);
        }
        useListViewAdapter();
    }

    private void goChat(String key) {
        if (userMap.get(key) != null && toFriendMsgChatMap.get(key) != null){
            MsgChat toMeMsgChat = toMeMsgChatMap.get(key);
            MsgChat toFriendMsgChat = toFriendMsgChatMap.get(key);
            Bundle data = new Bundle();
            ArrayList<Map<String, String>> msgChatArrayList = new ArrayList<>();

            Map<String, String> msgObjectIdMap = new HashMap<>();
            data.putString("fName", userMap.get(key).getNickName());
            msgObjectIdMap.put("userMsgObjectId", toFriendMsgChat.getObjectId());
            msgObjectIdMap.put("friendMsgObjectId", toMeMsgChat.getObjectId());
            msgChatArrayList.add(msgObjectIdMap);
            data.putSerializable("msgChatArrayList", msgChatArrayList);
            startActivity(new Intent(MineMessageRecordActivity.this, ChatActivity.class).putExtras(data));
        } else {
            Toast.makeText(MineMessageRecordActivity.this, "data loading ...", Toast.LENGTH_SHORT).show();
        }

    }
}
