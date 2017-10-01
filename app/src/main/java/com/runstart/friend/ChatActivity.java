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

import com.runstart.BmobBean.Friend;
import com.runstart.BmobBean.User;
import com.runstart.R;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

/**
 * Created by user on 17-9-28.
 */

public class ChatActivity extends Activity implements View.OnClickListener {
    private TextView chatNameText;
    private ListView msgListView;
    private EditText inputText;
    private Button send;
    private ImageView cameraImg, pictureImg;
    private MsgAdapter adapter;
    private List<MsgChat> msgList = new ArrayList<>();


    private Friend friend;
    private String userOId,friendOId,friendName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.freind_chat_layout);
        initView();
        userOId = getIntent().getStringExtra("user");
        friendOId = getIntent().getStringExtra("friend");
        friendName=getIntent().getStringExtra("fName");
        chatNameText.setText(friendName);
        msgListView.setAdapter(adapter);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.friend_chat_send:
                String content = inputText.getText().toString();
                if (!"".equals(content)) {
                    MsgChat msg = new MsgChat(content, MsgChat.TYPE_SENT);
                    msgList.add(msg);
                    adapter.notifyDataSetChanged(); // 当有新消息时,刷新
                    msgListView.setSelection(msgList.size()); // 将ListView
                    inputText.setText(""); // 清空输入框中的内容
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
        initMsgs(); // 初始化消息数据
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
//        BmobIM.getInstance().disConnect();
    }

    private void initMsgs() {
        MsgChat msg1 = new MsgChat("Hello guy.", MsgChat.TYPE_RECEIVED);
        msgList.add(msg1);
        MsgChat msg2 = new MsgChat("Hello. Who is that?", MsgChat.TYPE_SENT);
        msgList.add(msg2);
        MsgChat msg3 = new MsgChat(" Nice talking to you.  ", MsgChat.TYPE_RECEIVED);
        msgList.add(msg3);
        MsgChat msg4 = new MsgChat("Hello guy.Hello guy.Hello guy.Hello guy.Hello guy.Hello guy.Hello guy.Hello guy.Hello guy.", MsgChat.TYPE_RECEIVED);
        msgList.add(msg4);
        MsgChat msg5 = new MsgChat("Hello. Who is that?", MsgChat.TYPE_SENT);
        msgList.add(msg5);
        MsgChat msg6 = new MsgChat("This is Tom. Nice talking to you.This is Tom. Nice talking to you. This is Tom. Nice talking to you.  ", MsgChat.TYPE_RECEIVED);
        msgList.add(msg6);
        MsgChat msg7 = new MsgChat("Hello. Who is that?", MsgChat.TYPE_SENT);
        msgList.add(msg7);
        MsgChat msg8 = new MsgChat("This is Tom. Nice talking to you.This is Tom. Nice talking to you. This is Tom. Nice talking to you.  ", MsgChat.TYPE_RECEIVED);
        msgList.add(msg8);
        MsgChat msg9 = new MsgChat("Hello. Who is that?Hello. Who is that?Hello. Who is that?", MsgChat.TYPE_SENT);
        msgList.add(msg9);
    }


}
