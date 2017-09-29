package com.runstart.friend;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.runstart.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 17-9-28.
 */

public class ChatActivity extends Activity {
    private TextView chatNameText;
    private ListView msgListView;
    private EditText inputText;
    private Button send;
    private MsgAdapter adapter;
    private List<MsgChat> msgList = new ArrayList<MsgChat>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.freind_chat_layout);
        initMsgs(); // 初始化消息数据
        adapter = new MsgAdapter(this, R.layout.freind_chat_item, msgList);
        chatNameText=(TextView)findViewById(R.id.friend_chat_name);
        chatNameText.setText(getIntent().getStringExtra("name"));
        inputText = (EditText) findViewById(R.id.friend_chat_input);
        send = (Button) findViewById(R.id.friend_chat_send);
        msgListView = (ListView) findViewById(R.id.msg_list_view);
        msgListView.setAdapter(adapter);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputText.getText().toString();
                if (!"".equals(content)) {
                    MsgChat msg = new MsgChat(content, MsgChat.TYPE_SENT);
                    msgList.add(msg);
                    adapter.notifyDataSetChanged(); // 当有新消息时,刷新
                    msgListView.setSelection(msgList.size()); // 将ListView
                    inputText.setText(""); // 清空输入框中的内容
                }
            }
        });
    }

    private void initMsgs() {
        MsgChat msg1 = new MsgChat("Hello guy.", MsgChat.TYPE_RECEIVED);
        msgList.add(msg1);
        MsgChat msg2 = new MsgChat("Hello. Who is that?", MsgChat.TYPE_SENT);
        msgList.add(msg2);
        MsgChat msg3 = new MsgChat("This is Tom. Nice talking to you.This is Tom. Nice talking to you. This is Tom. Nice talking to you.  ", MsgChat.TYPE_RECEIVED);
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
