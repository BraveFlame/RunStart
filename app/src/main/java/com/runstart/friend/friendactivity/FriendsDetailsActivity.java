package com.runstart.friend.friendactivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.ArrayMap;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.runstart.BmobBean.Friend;
import com.runstart.BmobBean.User;
import com.runstart.R;
import com.runstart.friend.ChatActivity;
import com.runstart.friend.MsgChat;
import com.runstart.friend.adapter.MyUtils;
import com.runstart.history.MyApplication;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;

public class FriendsDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView headerImageRect, likeImage;
    private MyHeaderImageView headerImageCircle;

    private TextView friendName, likeNumber, sportDistance, msgCount, averageSpeed, timeCost, kCal;

    private RadioGroup mRadioGroup;
    private RadioButton[] mRadioButtons;

    private Button goChatting;
    private User user;
    private Friend friend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_details);

        initView();
        setonClickListener();

        user = (User) getIntent().getSerializableExtra("user");
        friend = (Friend) getIntent().getSerializableExtra("friend");
        List<Bitmap> bitmapList = (List<Bitmap>) getIntent().getSerializableExtra("headerImage");

        headerImageRect.setImageBitmap(bitmapList.get(0));
        headerImageCircle.setImageBitmap(bitmapList.get(0));
        if (friend.getLikeDate() == null || friend.getLikeDate().length() == 0) {
            likeImage.setImageResource(R.mipmap.ic_zan2);
        } else {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            if (!((year == Integer.parseInt(friend.getLikeDate().substring(0, 4))) &&
                    (month == Integer.parseInt(friend.getLikeDate().substring(4, 6))) &&
                    (day == Integer.parseInt(friend.getLikeDate().substring(6, 8))))) {
                likeImage.setImageResource(R.mipmap.ic_zan2);
            } else {
                likeImage.setImageResource(R.mipmap.ic_zan);
            }
        }
        friendName.setText(user.getNickName());
        likeNumber.setText(user.getLikeNumberForHistory() + "");

        likeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyUtils.like(likeImage, friend, user, FriendsDetailsActivity.this, likeNumber);
            }
        });

        mRadioButtons[0].setChecked(true);
        final String sportDistanceStr = (user.getWalkDistance() + user.getRunDistance() + user.getRideDistance()) + " m";
        sportDistance.setText(sportDistanceStr);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.all:
                        sportDistance.setText(sportDistanceStr);
                        for (RadioButton radioButton : mRadioButtons) {
                            radioButton.setTextColor(0xff878787);
                        }
                        mRadioButtons[0].setTextColor(0xff21187f);
                        break;
                    case R.id.walk:
                        sportDistance.setText(user.getWalkDistance() + " m");
                        for (RadioButton radioButton : mRadioButtons) {
                            radioButton.setTextColor(0xff878787);
                        }
                        mRadioButtons[1].setTextColor(0xff21187f);
                        break;
                    case R.id.run:
                        sportDistance.setText(user.getRunDistance() + " m");
                        for (RadioButton radioButton : mRadioButtons) {
                            radioButton.setTextColor(0xff878787);
                        }
                        mRadioButtons[2].setTextColor(0xff21187f);
                        break;
                    case R.id.ride:
                        sportDistance.setText(user.getRideDistance() + " m");
                        for (RadioButton radioButton : mRadioButtons) {
                            radioButton.setTextColor(0xff878787);
                        }
                        mRadioButtons[3].setTextColor(0xff21187f);
                        break;
                }
            }
        });


    }


    public void initView() {

        headerImageRect = (ImageView) findViewById(R.id.headerImageRect);
        likeImage = (ImageView) findViewById(R.id.likeImage);
        headerImageCircle = (MyHeaderImageView) findViewById(R.id.headerImageCircle);
        friendName = (TextView) findViewById(R.id.NickName);
        likeNumber = (TextView) findViewById(R.id.likeNumber);
        sportDistance = (TextView) findViewById(R.id.sportLength);
        msgCount = (TextView) findViewById(R.id.msgCount);
        averageSpeed = (TextView) findViewById(R.id.averageSpeed);
        timeCost = (TextView) findViewById(R.id.timeCost);
        kCal = (TextView) findViewById(R.id.kCal);
        goChatting=(Button) findViewById(R.id.goChatting);

        mRadioGroup = (RadioGroup) findViewById(R.id.buttonRadioGroup);
        mRadioButtons = new RadioButton[]{(RadioButton) findViewById(R.id.all), (RadioButton) findViewById(R.id.walk),
                (RadioButton) findViewById(R.id.run), (RadioButton) findViewById(R.id.ride)};
    }

    public void setonClickListener() {
        msgCount.setOnClickListener(this);
        goChatting.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.msgCount:

                Intent chatIntent = new Intent(FriendsDetailsActivity.this, ChatActivity.class);
                Bundle data = new Bundle();
                data.putString("friend", user.getObjectId());
                data.putString("user", MyApplication.applicationMap.get("userObjectId"));
                data.putString("fName", user.getNickName());
                if (user.getObjectId().equals("033d152e41")) {
                    data.putString("chatUserObjectId", "1096bbddb0");
                    data.putString("chatFriendObjectId", "af6576715e");
                } else {
                    data.putString("chatUserObjectId", "af6576715e");
                    data.putString("chatFriendObjectId", "1096bbddb0");
                }


                chatIntent.putExtras(data);
                startActivity(chatIntent);
                break;

            case R.id.goChatting:
                final ArrayList<Map<String, String>> msgChatArrayList = new ArrayList<>();
                final Map<String, String> msgObjectIdMap = new ArrayMap<>();

                goChat(new String[]{MyApplication.applicationMap.get("userObjectId"), user.getObjectId()},
                        "userMsgObjectId", user, msgChatArrayList, msgObjectIdMap);
                goChat(new String[]{user.getObjectId(), MyApplication.applicationMap.get("userObjectId")},
                        "friendMsgObjectId", user, msgChatArrayList, msgObjectIdMap);
                break;
            default:
                break;


        }

    }

    private void goChat(String[] params, final String key, final User user, final ArrayList<Map<String, String>> msgChatArrayList,
                        final Map<String, String> msgObjectIdMap){
        new BmobQuery<MsgChat>().setSQL("select * from MsgChat where userObjectId=? and friendObjectId=?")
                .setPreparedParams(params).doSQLQuery(new SQLQueryListener<MsgChat>() {
            @Override
            public void done(BmobQueryResult<MsgChat> bmobQueryResult, BmobException e) {
                if (e == null){
                    final Bundle data = new Bundle();
                    final MsgChat msgChat_1, msgChat_2;
                    if (bmobQueryResult.getResults().size() == 0){
                        msgChat_1 = new MsgChat(MyApplication.applicationMap.get("userObjectId"), user.getObjectId(), null, null, 0);
                        msgChat_2 = new MsgChat(user.getObjectId(), MyApplication.applicationMap.get("userObjectId"), null, null, 0);
                        msgChat_1.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null){
                                    synchronized (MsgChat.class){
                                        msgObjectIdMap.put("userMsgObjectId", msgChat_1.getObjectId());
                                        if (msgObjectIdMap.size() == 2){
                                            msgChatArrayList.add(msgObjectIdMap);
                                            data.putSerializable("msgChatArrayList", msgChatArrayList);
                                            data.putString("fName", user.getNickName());
                                            startActivity(new Intent(FriendsDetailsActivity.this, ChatActivity.class).putExtras(data));
                                        }
                                    }
                                }else {
                                    Toast.makeText(FriendsDetailsActivity.this, "Going chatting failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        msgChat_2.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null){
                                    synchronized (MsgChat.class){
                                        msgObjectIdMap.put("friendMsgObjectId", msgChat_2.getObjectId());
                                        if (msgObjectIdMap.size() == 2){
                                            msgChatArrayList.add(msgObjectIdMap);
                                            data.putString("fName", user.getNickName());
                                            data.putSerializable("msgChatArrayList", msgChatArrayList);
                                            startActivity(new Intent(FriendsDetailsActivity.this, ChatActivity.class).putExtras(data));
                                        }
                                    }
                                }else {
                                    Toast.makeText(FriendsDetailsActivity.this, "Going chatting failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        synchronized (MsgChat.class){
                            msgObjectIdMap.put(key, bmobQueryResult.getResults().get(0).getObjectId());
                            if (msgObjectIdMap.size() == 2){
                                msgChatArrayList.add(msgObjectIdMap);
                                data.putSerializable("msgChatArrayList", msgChatArrayList);
                                data.putString("fName", user.getNickName());
                                startActivity(new Intent(FriendsDetailsActivity.this, ChatActivity.class).putExtras(data));
                            }
                        }
                    }
                } else {
                    Toast.makeText(FriendsDetailsActivity.this, "Going chatting failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
