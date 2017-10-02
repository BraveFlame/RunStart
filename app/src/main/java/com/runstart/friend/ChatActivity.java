package com.runstart.friend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.runstart.R;
import com.runstart.help.ToastShow;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.ValueEventListener;

/**
 * Created by user on 17-9-28.
 */
/*
未解决：1.没有实现保存到本地数据库。
        2.当发送点击过快时会使得isFirstRec = true;刚好碰上监听数据，导致接受对方上次最后一条信息！
        3.离线信息的判断不适和 A发送给B，B不回复时直接退出，下次B照样会再次收到那个信息！若B一开始、一直不在线则不会重复，
          A多次登录发送给离线的B，B也不会重复。
 */
public class ChatActivity extends Activity implements View.OnClickListener {
    private TextView chatNameText;
    private ListView msgListView;
    private EditText inputText;
    private Button send;
    private ImageView cameraImg, pictureImg;
    private MsgAdapter adapter;
    private String chatObjectId;
    private List<MsgChat> msgList = new ArrayList<>();


    private String userOId, friendOId, friendName;
    private String getUserID, getFriendID, userLeaveMsg, friendLeaveMsg;
    private StringBuilder myToHimLeave = new StringBuilder("0");
    private StringBuilder himToMyLeave = new StringBuilder("0");

    private BmobRealTimeData rtd;
    private String content, recMsgPoint;
    private String lastRecContent;
    private boolean isFirstRec = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.freind_chat_layout);
        initView();
        userOId = getIntent().getStringExtra("user");
        friendOId = getIntent().getStringExtra("friend");

        friendName = getIntent().getStringExtra("fName");
        chatObjectId = getIntent().getStringExtra("chatObjectId");
        chatNameText.setText(friendName);
        msgListView.setAdapter(adapter);
        getMsgData();

        rtd = new BmobRealTimeData();
        //在线时监听对方发信息
        rtd.start(new ValueEventListener() {
            @Override
            public void onDataChange(JSONObject data) {
                if (isFirstRec) {
                    lastRecContent = BmobJdonChat.jsonToString(data, recMsgPoint);
                    MsgChat msg = new MsgChat();
                    msg.setType(MsgChat.TYPE_RECEIVED);
                    msg.setRecContent(lastRecContent);
                    msgList.add(msg);
                    adapter.notifyDataSetChanged(); // 当有新消息时,刷新
                    msgListView.setSelection(msgList.size()); // 将ListView
                    if (userOId.equals(getUserID)) {
                        myToHimLeave.delete(1, myToHimLeave.length());
                    } else {
                        himToMyLeave.delete(1, himToMyLeave.length());
                    }
                }

                Log.d("bmob", "(" + data.optString("action") + ")" + "数据：" + data);
            }

            @Override
            public void onConnectCompleted(Exception ex) {
                Log.d("bmob", "连接成功:" + rtd.isConnected());
                if (rtd.isConnected()) {
                    // 监听表更新
                    rtd.subRowUpdate("MsgChat", chatObjectId);
                    Log.d("bmob", "监听DaySport表成功:");
                }
            }
        });


    }

    //连接时，查看是否有离线信息，有则显示，并清0；另外判断对方有没有看到自己的离线信息，没看到则继续添加离线信息
    public void getMsgData() {
        BmobQuery<MsgChat> msgChatBmobQuery = new BmobQuery<>();
        msgChatBmobQuery.getObject(chatObjectId, new QueryListener<MsgChat>() {
            @Override
            public void done(MsgChat msgChat, BmobException e) {
                if (e == null) {
                    getUserID = msgChat.getUserObjectId();
                    getFriendID = msgChat.getFriendObjectId();
                    userLeaveMsg = msgChat.getUserLeaveMsg();
                    friendLeaveMsg = msgChat.getFriendLeaveMsg();
                    ToastShow.showToast(ChatActivity.this, "huoqu-chgong");
                    //判断是哪一方，只不过是名字有差异，事实上双方都是一样的朋友，userId和FriendId无差异
                    if (getUserID.equals(userOId)) {
                        recMsgPoint = "recContent";
                    } else recMsgPoint = "sendContent";

                    //查看对方给我的离线信息，并清0
                    if (getUserID.equals(userOId) && !friendLeaveMsg.equals("0")) {
                        BmobJdonChat.getLeaveMsg(msgList, friendLeaveMsg);
                        adapter.notifyDataSetChanged(); // 当有新消息时,刷新
                        msgListView.setSelection(msgList.size()); // 将ListView
                        msgChat.setFriendLeaveMsg("0");
                    }
                    //看看对方有没有我给的离线信息,没有的话如果发信息，继续添加。已看则重新开始
                    if (getUserID.equals(userOId) && !userLeaveMsg.equals("0")) {
                        myToHimLeave.replace(0, myToHimLeave.length(), userLeaveMsg);
                    }

                    //查看对方给我的离线信息，并清0
                    if (!getUserID.equals(userOId) && !userLeaveMsg.equals("0")) {
                        BmobJdonChat.getLeaveMsg(msgList, userLeaveMsg);
                        adapter.notifyDataSetChanged(); // 当有新消息时,刷新
                        msgListView.setSelection(msgList.size()); // 将ListView
                        msgChat.setUserLeaveMsg("0");
                    }

                    //看看对方有没有我给的离线信息,没有的话如果发信息，继续添加。已看则重新开始
                    if (!getUserID.equals(userOId) && !friendLeaveMsg.equals("0")) {
                        himToMyLeave.replace(0, himToMyLeave.length(), friendLeaveMsg);
                    }
                    //对看到的留言进行清0
                    isFirstRec = false;
                    msgChat.update(chatObjectId, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            {
                                if (e == null) {
                                    isFirstRec = true;
                                } else {
                                    ToastShow.showToast(ChatActivity.this, "发送失败！");
                                }
                            }
                        }
                    });

                } else {
                    ToastShow.showToast(ChatActivity.this, "huoqu失败！");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.friend_chat_send:
                content = inputText.getText().toString();
                isFirstRec = false;
                if (!"".equals(content)) {
                    MsgChat msgChat = new MsgChat();
                    //发送信息时，顺便保留到离线信息。
                    if (getUserID.equals(userOId)) {
                        msgChat.setSendContent(content);
                        msgChat.setUserLeaveMsg(myToHimLeave.append(".*.|*|" + content).toString());
                        //如果我在线，则把对方的给我的离线信息删掉
                        himToMyLeave.delete(1, himToMyLeave.length());
                        msgChat.setFriendLeaveMsg("0");
                    }
                    //发送信息时，顺便保留到离线信息。
                    else {
                        msgChat.setRecContent(content);
                        msgChat.setFriendLeaveMsg(himToMyLeave.append(".*.|*|" + content).toString());
                        //如果我在线，则把对方的给我的离线信息删掉
                        myToHimLeave.delete(1, myToHimLeave.length());
                        msgChat.setUserLeaveMsg("0");
                    }
                    msgChat.update(chatObjectId, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            {
                                if (e == null) {
                                    inputText.setText(""); // 清空输入框中的内容
                                    MsgChat msg = new MsgChat();
                                    msg.setSendContent(content);
                                    msg.setType(MsgChat.TYPE_SENT);
                                    msgList.add(msg);
                                    adapter.notifyDataSetChanged(); // 当有新消息时,刷新
                                    msgListView.setSelection(msgList.size()); // 将ListView
                                    isFirstRec = true;
                                } else {
                                    ToastShow.showToast(ChatActivity.this, "发送失败！");
                                }
                            }
                        }
                    });
                }
                break;
            case R.id.friend_chat_camera:
                PhotoUtilsCircle.photograph(ChatActivity.this);
                break;

            case R.id.friend_chat_picture:
                PhotoUtilsCircle.selectPictureFromAlbum(ChatActivity.this);
                break;

            default:
                break;
        }
    }

    public void initView() {
        //initMsgs(); // 初始化消息数据
        adapter = new MsgAdapter(this, R.layout.freind_chat_item, msgList);
        chatNameText = (TextView) findViewById(R.id.friend_chat_name);
        chatNameText.setText(getIntent().getStringExtra("name"));
        inputText = (EditText) findViewById(R.id.friend_chat_input);
        send = (Button) findViewById(R.id.friend_chat_send);
        send.setOnClickListener(this);
        cameraImg = (ImageView) findViewById(R.id.friend_chat_camera);
        cameraImg.setOnClickListener(this);
        msgListView = (ListView) findViewById(R.id.msg_list_view);
        pictureImg = (ImageView) findViewById(R.id.friend_chat_picture);
        pictureImg.setOnClickListener(this);

    }


    private String bxfPath = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String str = PhotoUtilsCircle.myPictureOnResultOperate(requestCode, resultCode, data, this);
        if (str.length() > 3) {
            if (str.substring(0, 3).equals("bxf"))
                bxfPath = str.substring(3);
        } else if (str.equals("3")) PhotoUtilsCircle.showImage(cameraImg, bxfPath);
        Log.e("database", "str:" + str);
    }

    //调用option就一路通了
    @Override
    protected void onDestroy() {
        super.onDestroy();

        //BmobIM.getInstance().disConnect();
    }


}
