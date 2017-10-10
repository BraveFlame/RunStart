package com.runstart.friend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import cn.bmob.v3.listener.ValueEventListener;

/**
 * Created by user on 17-9-28.
 */
/*
已解决：Y1.没有实现保存到本地数据库。
       Y 2.当发送点击过快时会使得isFirstRec = true;刚好碰上监听数据，导致接受对方上次最后一条信息！
        Y3.离线信息的判断不适和 A发送给B，B不回复时直接退出，下次B照样会再次收到那个信息！若B一开始、一直不在线则不会重复，
          A多次登录发送给离线的B，B也不会重复。
         Y 4.图片发送
 */
public class ChatActivity extends Activity implements View.OnClickListener {

    private TextView chatNameText;
    private ListView msgListView;
    private EditText inputText;
    private Button send;
    private ImageView cameraImg, pictureImg;

    private MsgAdapter adapter;
    private List<MsgChat> msgList = new ArrayList<>();


    private String chatUserObjectId, chatFriendObjectId;
    private String friendName;

    private String userLeaveMsg, friendLeaveMsg;
    private StringBuilder myToHimLeave = new StringBuilder("0");
    private BmobRealTimeData rtd;
    private String content;
    private String lastRecContent;

    private LocalChatLog chatDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.freind_chat_layout);
        chatDb = LocalChatLog.getLocalChatLog(this);
        initView();
        //昵称
        friendName = getIntent().getStringExtra("fName");

        //需要更改和监听的两行数据objectId
        chatUserObjectId = getMsgChatObjectIdMap().get("userMsgObjectId");
        chatFriendObjectId = getMsgChatObjectIdMap().get("friendMsgObjectId");
        //获取本地记录
        getLocalChat();
        chatNameText.setText(friendName);
        msgListView.setAdapter(adapter);
        msgListView.setSelection(msgList.size());
        //获取离线信息--留言
        getMsgData();

        //开始监听
        rtd = new BmobRealTimeData();
        //在线时监听对方发信息
        rtd.start(new ValueEventListener() {
            @Override
            public void onDataChange(JSONObject data) {
                //对方发送的信息
                recContent(data);
            }

            @Override
            public void onConnectCompleted(Exception ex) {
                Log.d("bmob", "连接成功:" + rtd.isConnected());
                if (rtd.isConnected()) {
                    // 监听表更新
                    rtd.subRowUpdate("MsgChat", chatFriendObjectId);
                    Log.d("bmob", "监听DaySport表成功:");
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.friend_chat_send:
                content = inputText.getText().toString();
                //isFirstRec = false;
                if (!"".equals(content)) {
                    sendMsg(content, "110");
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

    /**
     * 发送信息，为文字或者已上传的图片地址，没有图片时，地址为110
     * @param content
     * @param imgPath
     */
    public void sendMsg(final String content, final String imgPath) {
        final MsgChat msgChat = new MsgChat();
        //发送信息时，顺便保留到离线信息。
        msgChat.setContent(content);
        msgChat.setLeaveMsg(myToHimLeave.append(".*.|*|" + content).toString());
        msgChat.update(chatUserObjectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                {
                    if (e == null) {
                        inputText.setText(""); // 清空输入框中的内容
                        MsgChat msg = new MsgChat();
                        if (imgPath.equals("110"))
                            msg.setContent(content);
                        else msg.setContent(imgPath);
                        msg.setType(MsgChat.TYPE_SENT);
                        msgList.add(msg);
                        adapter.notifyDataSetChanged(); // 当有新消息时,刷新
                        msgListView.setSelection(msgList.size()); // 将ListView
                        chatDb.saveLocalChat(msg, chatUserObjectId, chatFriendObjectId);
                    } else {
                        ToastShow.showToast(ChatActivity.this, "发送失败！");
                    }
                }
            }
        });

    }


    /**
     * 根据裁剪完图片后发过来的本地地址，上传bmob，并把bmob上图片的地址发给对方
     *
     * @param path
     */
    public void sendImg(final String path) {
        final BmobFile bmobFile = new BmobFile(new File(path));
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    //上传成功，发网上地址给对方
                    sendMsg(bmobFile.getFileUrl(), path);
                    ToastShow.showToast(ChatActivity.this, "发送图片成功！！");
                } else {
                    ToastShow.showToast(ChatActivity.this, "发送图片失败！");
                }
            }

            @Override
            public void onProgress(Integer value) {

            }
        });

    }

    /**
     * 连接时，查看是否有离线信息，有则显示，并清0；
     * 另外判断对方有没有看到自己的离线信息，没看到则继续添加离线信息
     */

    public void getMsgData() {
        BmobQuery<MsgChat> msgChatBmobQuery = new BmobQuery<>();
        msgChatBmobQuery.getObject(chatFriendObjectId, new QueryListener<MsgChat>() {
            @Override
            public void done(MsgChat msgChat, BmobException e) {
                if (e == null) {
                    friendLeaveMsg = msgChat.getLeaveMsg();
                    ToastShow.showToast(ChatActivity.this, "huoqu-chgong");
                    //查看对方给我的离线信息，并清0
                    if (!friendLeaveMsg.equals("0") && !friendLeaveMsg.equals("")) {
                      //  leaveContent(friendLeaveMsg);
                        BmobJdonChat.getLeaveMsg(adapter,msgListView,msgList, friendLeaveMsg, ChatActivity.this, chatUserObjectId, chatFriendObjectId);
//                        adapter.notifyDataSetChanged(); // 当有新消息时,刷新
//                        msgListView.setSelection(msgList.size()); // 将ListView
                        msgChat.setLeaveMsg("0");
                        msgChat.setContent(".*.|*|");

                        msgChat.update(chatFriendObjectId, new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                {
                                    if (e == null) {

                                    } else {
                                        ToastShow.showToast(ChatActivity.this, "发送失败！");
                                    }
                                }
                            }
                        });
                    }

                } else {
                    ToastShow.showToast(ChatActivity.this, "huoqu失败！");
                }
            }
        });

        BmobQuery<MsgChat> msgILeave = new BmobQuery<>();
        msgILeave.getObject(chatUserObjectId, new QueryListener<MsgChat>() {
            @Override
            public void done(MsgChat msgChat, BmobException e) {
                if (e == null) {
                    userLeaveMsg = msgChat.getLeaveMsg();
                    //看看对方有没有看我给的离线信息,没有的话如果发信息，继续添加。已看则重新开始
                    if (!userLeaveMsg.equals("0")) {
                        myToHimLeave.replace(0, myToHimLeave.length(), userLeaveMsg);
                    }
                } else {
                    ToastShow.showToast(ChatActivity.this, "huoqu失败！");
                }
            }
        });

    }

    /**
     * 对方发送信息，判断是文字还是图片
     *
     * @param data
     */
    public void recContent(JSONObject data) {

        lastRecContent = BmobJdonChat.jsonToString(data, "content");
        if (lastRecContent.equals(".*.|*|"))
            return;
        if (lastRecContent.equals("")) {
            myToHimLeave.delete(1, myToHimLeave.length());
            return;
        }
        myToHimLeave.delete(1, myToHimLeave.length());
        //接收到图片
        if (lastRecContent.contains("http://bmob-cdn-14232.b0.upaiyun.com")) {
            String picName = System.currentTimeMillis() + ".png";
            BmobFile bmobFile = new BmobFile(picName, "", lastRecContent);
            recImg(bmobFile, picName);
        }
        //接收到文字或表情
        else {
            recMsg(lastRecContent);
        }

        Log.d("bmob", "(" + data.optString("action") + ")" + "数据：" + data);

    }

    public void leaveContent(String friendLeaveMsg){
        //接收到图片
        if (friendLeaveMsg.contains("http://bmob-cdn-14232.b0.upaiyun.com")) {
            String picName = System.currentTimeMillis() + ".png";
            BmobFile bmobFile = new BmobFile(picName, "", friendLeaveMsg);
            recImg(bmobFile, picName);
        }
        //接收到文字或表情
        else {
            recMsg(friendLeaveMsg);
        }
    }

    /**
     * 将文字或图片的本地地址传入
     *
     * @param lastRecContent
     */
    public void recMsg(String lastRecContent) {
        MsgChat msg = new MsgChat();
        msg.setType(MsgChat.TYPE_RECEIVED);
        msg.setContent(lastRecContent);
        chatDb.saveLocalChat(msg, chatUserObjectId, chatFriendObjectId);
        msgList.add(msg);
        adapter.notifyDataSetChanged(); // 当有新消息时,刷新
        msgListView.setSelection(msgList.size()); // 将ListView
        Log.e("chat", "save" + msgList.size());

    }

    /**
     * 接收的是图片链接时，先下载到本地，再发送本地地址到MsgChat的content字段
     *
     * @param bmobFile
     * @param name
     */
    public void recImg(BmobFile bmobFile, String name) {
        // 设置文件保存路径这里放在del目录下
        final File file;
        String path;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            //SDCard是否可用
            path = Environment.getExternalStorageDirectory() + File.separator + getPackageName() + File.separator + "myimages/" + name;
            file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
        } else {
            path = getFilesDir() + File.separator + getPackageName() + File.separator + "myimages/" + name;
            file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
        }

        bmobFile.download(file, new DownloadFileListener() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    //获取成功
                    if (s != null)
                        recMsg(s);

                }
            }

            @Override
            public void onProgress(Integer integer, long l) {

            }
        });
    }


    /**
     * 初始化控件和布局
     */
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

    /**
     * 获取本地聊天记录
     */
    public void getLocalChat() {
        chatDb.getLocalChat(chatUserObjectId, chatFriendObjectId, msgList);
        Log.e("chatDb", "" + msgList.size());
    }

    private String bxfPath = "";

    /**
     * 调用拍照或者照片，裁剪后直接发送
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String str = PhotoUtilsCircle.myPictureOnResultOperate(requestCode, resultCode, data, this);
        if (str.length() > 3) {
            if (str.substring(0, 3).equals("bxf"))
                bxfPath = str.substring(3);
        } else if (str.equals("3")) {
            //发送图片
            sendImg(bxfPath);
        }
        Log.e("database", "str:" + str);
    }

    /**
     * 返回时，把对方给我的留言清空，防止下次登陆再次收到
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("chat", "destroy");
        MsgChat msgChat = new MsgChat();
        msgChat.setObjectId(chatFriendObjectId);
        msgChat.setLeaveMsg("0");
        msgChat.setContent("");
        msgChat.update();
        MsgChat msgChat1 = new MsgChat();
        msgChat1.setContent("");
        msgChat1.setObjectId(chatUserObjectId);
        msgChat1.update();
        rtd.unsubRowUpdate("MsgChat", chatFriendObjectId);

    }

    /**
     * 获取两行数据的objectId
     *
     * @return
     */
    private Map<String, String> getMsgChatObjectIdMap() {
        ArrayList<Map<String, String>> msgChatArrayList = (ArrayList<Map<String, String>>) getIntent().getSerializableExtra("msgChatArrayList");
        Map<String, String> msgObjectIdMap = msgChatArrayList.get(0);
        return msgObjectIdMap;
    }


}
