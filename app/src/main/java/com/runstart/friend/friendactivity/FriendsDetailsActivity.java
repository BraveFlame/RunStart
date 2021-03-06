package com.runstart.friend.friendactivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.runstart.BmobBean.ActivityAndMember;
import com.runstart.BmobBean.ActivityData;
import com.runstart.BmobBean.Friend;
import com.runstart.BmobBean.User;
import com.runstart.R;
import com.runstart.bean.ActivityTopicForCircle;
import com.runstart.circle.CirclePushCardActivity;
import com.runstart.circle.GetFromBmob;
import com.runstart.friend.ChatActivity;
import com.runstart.friend.ListenMsgService;
import com.runstart.friend.MsgChat;
import com.runstart.friend.adapter.MyUtils;
import com.runstart.MyApplication;
import com.runstart.middle.ListViewAdapterForCircle;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class FriendsDetailsActivity extends AppCompatActivity {

    private ImageView headerImageRect, likeImage;
    private MyHeaderImageView headerImageCircle;

    private TextView friendName, likeNumber, sportDistance, msgCountTextView, averageSpeed, timeCost, kCal;

    private RadioGroup mRadioGroup;
    private RadioButton[] mRadioButtons;

    private LinearLayout goChatting;

    private FloatingActionButton addFriend;

    private Friend friend;
    private User user;
    private MsgCountReceiver msgCountReceiver;

    public static void jump(Activity activity, User friendUser, Friend friend, Bitmap headerImage) {
        Intent intent = new Intent(activity, FriendsDetailsActivity.class);
        Bundle data = new Bundle();
        data.putSerializable("friend", friend);
        data.putSerializable("user", friendUser);
        ArrayList<Bitmap> bitmapList = new ArrayList<>();
        bitmapList.add(headerImage);
        data.putSerializable("headerImage", bitmapList);
        intent.putExtras(data);
        activity.startActivity(intent);
    }

    public static void jump(Fragment fragment, User user, Friend friend, Bitmap headerImage) {
        Intent intent = new Intent(fragment.getActivity(), FriendsDetailsActivity.class);
        Bundle data = new Bundle();
        data.putSerializable("friend", friend);
        data.putSerializable("user", user);
        ArrayList<Bitmap> bitmapList = new ArrayList<>();
        bitmapList.add(headerImage);
        data.putSerializable("headerImage", bitmapList);
        intent.putExtras(data);
        fragment.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_details);

        goChatting = (LinearLayout) findViewById(R.id.goChatting);
        addFriend = (FloatingActionButton) findViewById(R.id.addFriend);
        msgCountReceiver=new MsgCountReceiver();
        headerImageRect = (ImageView) findViewById(R.id.headerImageRect);
        likeImage = (ImageView) findViewById(R.id.likeImage);
        headerImageCircle = (MyHeaderImageView) findViewById(R.id.headerImageCircle);
        friendName = (TextView) findViewById(R.id.NickName);
        likeNumber = (TextView) findViewById(R.id.likeNumber);
        sportDistance = (TextView) findViewById(R.id.sportLength);
        msgCountTextView = (TextView) findViewById(R.id.msgCount);
        averageSpeed = (TextView) findViewById(R.id.averageSpeed);
        timeCost = (TextView) findViewById(R.id.timeCost);
        kCal = (TextView) findViewById(R.id.kCal);
        IntentFilter filter = new IntentFilter(ListenMsgService.FILTER_STR);
        registerReceiver(msgCountReceiver, filter);
        //初始化
        mRadioGroup = (RadioGroup) findViewById(R.id.buttonRadioGroup);
        mRadioButtons = new RadioButton[]{(RadioButton) findViewById(R.id.all), (RadioButton) findViewById(R.id.walk),
                (RadioButton) findViewById(R.id.run), (RadioButton) findViewById(R.id.ride)};
        mRadioButtons[0].setChecked(true);
        //获取数据
        user = (User) getIntent().getSerializableExtra("user");
        friend = (Friend) getIntent().getSerializableExtra("friend");
        if ((friend == null || friend.isFriend() == 0) && (!MyApplication.applicationMap.get("userObjectId").equals(user.getObjectId()))) {
            addFriend.setVisibility(View.VISIBLE);
        }
        headerImageRect.setImageResource(R.mipmap.ic_shangchuangtupiang);
        headerImageCircle.setImageResource(R.mipmap.ic_shangchuangtupiang);

        queryImage(user.getHeaderImageUri());

        onClickRadioButton(user, sportDistance, averageSpeed, timeCost, kCal, "all", 0);
        if (friend == null || friend.getLikeDate() == null || friend.getLikeDate().length() == 0) {
            likeImage.setImageResource(R.mipmap.ic_zan2);
            likeNumber.setTextColor(0xff000000);
        } else {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            if (!((year == Integer.parseInt(friend.getLikeDate().substring(0, 4))) &&
                    (month == Integer.parseInt(friend.getLikeDate().substring(4, 6))) &&
                    (day == Integer.parseInt(friend.getLikeDate().substring(6, 8))))) {
                likeImage.setImageResource(R.mipmap.ic_zan2);
                likeNumber.setTextColor(0xff888888);
            } else {
                likeImage.setImageResource(R.mipmap.ic_zan);
                likeNumber.setTextColor(0xffc562ff);
            }
        }
        friendName.setText(user.getNickName());
        likeNumber.setText(user.getLikeNumberForHistory() + "");

        likeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (friend == null){
                    friend = new Friend(MyApplication.applicationMap.get("userObjectId"), user.getObjectId(), 0, "");
                    friend.save();
                }
                MyUtils.like(1, likeImage, friend, user, FriendsDetailsActivity.this, likeNumber);
            }
        });

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.all:
                        onClickRadioButton(user, sportDistance, averageSpeed, timeCost, kCal, "all", 0);
                        break;
                    case R.id.walk:
                        onClickRadioButton(user, sportDistance, averageSpeed, timeCost, kCal, "walk", 1);
                        break;
                    case R.id.run:
                        onClickRadioButton(user, sportDistance, averageSpeed, timeCost, kCal, "run", 2);
                        break;
                    case R.id.ride:
                        onClickRadioButton(user, sportDistance, averageSpeed, timeCost, kCal, "ride", 3);
                        break;
                }
            }
        });

        goChatting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<Map<String, String>> msgChatArrayList = new ArrayList<>();
                final Map<String, String> msgObjectIdMap = new HashMap<>();
                goChat(new String[]{MyApplication.applicationMap.get("userObjectId"), user.getObjectId()},
                        "userMsgObjectId", user, msgChatArrayList, msgObjectIdMap);
                goChat(new String[]{user.getObjectId(), MyApplication.applicationMap.get("userObjectId")},
                        "friendMsgObjectId", user, msgChatArrayList, msgObjectIdMap);
            }
        });

        addFriend.setOnClickListener(new View.OnClickListener() {
            private boolean hasAddFriend_0 = false;
            private boolean hasAddFriend_1 = false;
            private void createFriend_0(final String userObjectId, final String friendObjectId) {
                new BmobQuery<Friend>().setSQL("select * from Friend where userObjectId=? and friendObjectId=?")
                        .setPreparedParams(new String[]{userObjectId, friendObjectId})
                        .doSQLQuery(new SQLQueryListener<Friend>() {
                            @Override
                            public void done(BmobQueryResult<Friend> bmobQueryResult, BmobException e) {
                                if (e == null) {
                                    Friend friend_update;
                                    if (bmobQueryResult.getResults().size() == 0) {
                                        friend_update = new Friend(userObjectId, friendObjectId, 1, "");
                                        friend_update.save(new SaveListener<String>() {
                                            @Override
                                            public void done(String s, BmobException e) {
                                                if (e == null){
                                                    hasAddFriend_0 = true;
                                                    if (hasAddFriend_1){
                                                        Toast.makeText(FriendsDetailsActivity.this, "Add friend successfully", Toast.LENGTH_SHORT).show();
                                                        addFriend.setVisibility(View.GONE);
                                                    }
                                                }
                                            }
                                        });
                                    } else {
                                        friend_update = bmobQueryResult.getResults().get(0);
                                        friend_update.setFriend(1);
                                        friend_update.update(bmobQueryResult.getResults().get(0).getObjectId(), new UpdateListener() {
                                            @Override
                                            public void done(BmobException e) {
                                                if (e == null){
                                                    hasAddFriend_0 = true;
                                                    if (hasAddFriend_1){
                                                        Toast.makeText(FriendsDetailsActivity.this, "Add friend successfully", Toast.LENGTH_SHORT).show();
                                                        addFriend.setVisibility(View.GONE);
                                                    }
                                                } else {
                                                    Toast.makeText(FriendsDetailsActivity.this, "Add friend failed", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        });
            }
            private void createFriend_1(final String userObjectId, final String friendObjectId) {
                new BmobQuery<Friend>().setSQL("select * from Friend where userObjectId=? and friendObjectId=?")
                        .setPreparedParams(new String[]{userObjectId, friendObjectId})
                        .doSQLQuery(new SQLQueryListener<Friend>() {
                            @Override
                            public void done(BmobQueryResult<Friend> bmobQueryResult, BmobException e) {
                                if (e == null) {
                                    Friend friend_update;
                                    if (bmobQueryResult.getResults().size() == 0) {
                                        friend_update = new Friend(userObjectId, friendObjectId, 1, "");
                                        friend_update.save(new SaveListener<String>() {
                                            @Override
                                            public void done(String s, BmobException e) {
                                                if (e == null){
                                                    hasAddFriend_1 = true;
                                                    if (hasAddFriend_0){
                                                        Toast.makeText(FriendsDetailsActivity.this, "Add friend successfully", Toast.LENGTH_SHORT).show();
                                                        addFriend.setVisibility(View.GONE);
                                                    }
                                                }
                                            }
                                        });
                                    } else {
                                        friend_update = bmobQueryResult.getResults().get(0);
                                        friend_update.setFriend(1);
                                        friend_update.update(bmobQueryResult.getResults().get(0).getObjectId(), new UpdateListener() {
                                            @Override
                                            public void done(BmobException e) {
                                                if (e == null){
                                                    hasAddFriend_1 = true;
                                                    if (hasAddFriend_0){
                                                        Toast.makeText(FriendsDetailsActivity.this, "Add friend successfully", Toast.LENGTH_SHORT).show();
                                                        addFriend.setVisibility(View.GONE);
                                                    }
                                                } else {
                                                    Toast.makeText(FriendsDetailsActivity.this, "Add friend failed", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        });

            }

            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(FriendsDetailsActivity.this)
                        .setTitle("Add friend").setMessage("Are you sure to add each other to your own friends?")
                        .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                createFriend_0(MyApplication.applicationMap.get("userObjectId"), user.getObjectId());
                                createFriend_1(user.getObjectId(), MyApplication.applicationMap.get("userObjectId"));
                            }
                        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
            }
        });

        myListView = (ListView)findViewById(R.id.friend_details_activity_listview);
        ll_friend_details=(LinearLayout)findViewById(R.id.ll_friend_details_no_activity);
        myApplication=(MyApplication)getApplicationContext();
        GetFromBmob.getActivityIdsByUserId(user.getObjectId(),handler);
        myApplication.showProgressDialog(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initMsgCount();

    }

    private void onClickRadioButton(User user, TextView sportDistanceTextView, TextView averageSpeedTextView,
                                    TextView timeCostTextView, TextView kCalTextView, String sportCategory, int index) {
        for (RadioButton radioButton : mRadioButtons) {
            radioButton.setTextColor(0xff878787);
        }
        mRadioButtons[index].setTextColor(0xff21187f);

        int walkDistance = user.getWalkDistance();
        int runDistance = user.getRunDistance();
        int rideDistance = user.getRideDistance();
        int totalDistance = walkDistance + runDistance + rideDistance;
        int walkKcal = user.getWalkKcal();
        int runKcal = user.getRunKcal();
        int rideKcal = user.getRideKcal();
        int totalKcal = walkKcal + rideKcal + runKcal;
        long walkTime = user.getWalkTime();
        long runTime = user.getRunTime();
        long rideTime = user.getRideTime();
        long totalTime = walkTime + rideTime + runTime;

        switch (sportCategory) {
            case "all":
                sportDistanceTextView.setText(totalDistance + " m");
                if (totalTime != 0) {
                    averageSpeedTextView.setText((totalDistance / 1000 * 60 * 60 / totalTime) + " km/h");
                } else {
                    averageSpeedTextView.setText("0 hour cost");
                }
                timeCostTextView.setText(formatTime(totalTime));
                kCalTextView.setText(totalKcal + " Kcal");
                break;
            case "walk":
                sportDistanceTextView.setText(walkDistance + " m");
                if (walkTime != 0) {
                    averageSpeedTextView.setText((walkDistance / 1000 * 60 * 60 / walkTime) + " km/h");
                } else {
                    averageSpeedTextView.setText("0 hour cost");
                }
                timeCostTextView.setText(formatTime(walkTime));
                kCalTextView.setText(walkKcal + " Kcal");
                break;
            case "run":
                sportDistanceTextView.setText(runDistance + " m");
                if (runTime != 0) {
                    averageSpeedTextView.setText((runDistance / 1000 * 60 * 60 / runTime) + " km/h");
                } else {
                    averageSpeedTextView.setText("0 hour cost");
                }
                timeCostTextView.setText(formatTime(runTime));
                kCalTextView.setText(runKcal + " Kcal");
                break;
            case "ride":
                sportDistanceTextView.setText(rideDistance + " m");
                if (rideTime != 0) {
                    averageSpeedTextView.setText((rideDistance / 1000 * 60 * 60 / rideTime) + " km/h");
                } else {
                    averageSpeedTextView.setText("0 hour cost");
                }
                timeCostTextView.setText(formatTime(rideTime));
                kCalTextView.setText(rideKcal + " Kcal");
                break;
        }
    }

    private String formatTime(long time) {
        StringBuilder stringBuilder = new StringBuilder("");
        long hour = time / 60 / 60;
        stringBuilder.append(hour + ":");
        long minute = ((time) - (hour * 60 * 60)) / 60;
        stringBuilder.append(minute + ":");
        stringBuilder.append(((time) - (hour * 60 * 60) - (minute * 60)) + "");
        return stringBuilder.toString();
    }

    private void goChat(final String[] params, final String key, final User user, final ArrayList<Map<String, String>> msgChatArrayList,
                        final Map<String, String> msgObjectIdMap) {
        new BmobQuery<MsgChat>().setSQL("select * from MsgChat where userObjectId=? and friendObjectId=?")
                .setPreparedParams(params).doSQLQuery(new SQLQueryListener<MsgChat>() {
            @Override
            public void done(BmobQueryResult<MsgChat> bmobQueryResult, BmobException e) {
                if (e == null) {
                    final Bundle data = new Bundle();
                    data.putString("fName", user.getNickName());
                    final MsgChat msgChat;
                    if (bmobQueryResult.getResults().size() == 0) {
                        msgChat = new MsgChat(params[0], params[1], 0 + "", 0 + "", 0);
                        msgChat.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    synchronized (MsgChat.class) {
                                        msgObjectIdMap.put(key, msgChat.getObjectId());
                                        if (msgObjectIdMap.size() == 2) {
                                            msgChatArrayList.add(msgObjectIdMap);
                                            data.putSerializable("msgChatArrayList", msgChatArrayList);
                                            startActivity(new Intent(FriendsDetailsActivity.this, ChatActivity.class).putExtras(data));
                                        }
                                    }
                                } else {
                                    Toast.makeText(FriendsDetailsActivity.this, "Going chatting failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        synchronized (MsgChat.class) {
                            msgObjectIdMap.put(key, bmobQueryResult.getResults().get(0).getObjectId());
                            if (msgObjectIdMap.size() == 2) {
                                msgChatArrayList.add(msgObjectIdMap);
                                data.putSerializable("msgChatArrayList", msgChatArrayList);
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

    public class MsgCountReceiver extends BroadcastReceiver {
        public MsgCountReceiver(){
            super();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Map<String, Integer> msgCountMap =
                    ((ArrayList<Map<String, Integer>>)intent.getSerializableExtra("msgCountMapLoader")).get(0);
            Integer msgCount = msgCountMap.get(user.getObjectId());
            if (msgCount == null || msgCount == 0){
                msgCountTextView.setVisibility(View.GONE);
            } else {
                msgCountTextView.setVisibility(View.VISIBLE);
                msgCountTextView.setText(msgCount + "");
                if (msgCount > 99){
                    msgCountTextView.setText("99+");
                }
            }
        }
    }
    private void initMsgCount(){
        new BmobQuery<MsgChat>().setSQL("select * from MsgChat where friendObjectId=? and userObjectId=?")
                .setPreparedParams(new String[]{MyApplication.applicationMap.get(MyApplication.userObjectIdKey), user.getObjectId()})
                .doSQLQuery(new SQLQueryListener<MsgChat>() {
                    @Override
                    public void done(BmobQueryResult<MsgChat> bmobQueryResult, BmobException e) {
                        if (e == null){
                            List<MsgChat> msgChatList = bmobQueryResult.getResults();
                            if (msgChatList.size() == 0){
                                msgCountTextView.setVisibility(View.GONE);
                            } else {
                                MsgChat msgChat = msgChatList.get(0);
                                String data = msgChat.getLeaveMsg();
                                if (data == null || data.equals("0")){
                                    msgCountTextView.setVisibility(View.GONE);
                                } else {
                                    int msgCount = data.split("\\.\\*\\.\\|\\*\\|").length - 1;
                                    msgCountTextView.setVisibility(View.VISIBLE);
                                    msgCountTextView.setText(msgCount + "");
                                    if (msgCount > 99){
                                        msgCountTextView.setText("99+");
                                    }
                                }
                            }
                        }
                    }
                });
    }

    private void queryImage(String imageUri){
            String saveName = MyUtils.getStringToday() + user.getObjectId() + ".png";
            File saveFile = new File(Environment.getExternalStorageDirectory() + File.separator + "lovesportimage", saveName);
            if (imageUri == null || imageUri.length() == 0) {
                headerImageRect.setImageResource(R.mipmap.ic_shangchuangtupiang);
                headerImageCircle.setImageResource(R.mipmap.ic_shangchuangtupiang);
            } else {
                new BmobFile(saveName, "", imageUri).download(saveFile, new DownloadFileListener() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            headerImageRect.setImageBitmap(BitmapFactory.decodeFile(s));
                            headerImageCircle.setImageBitmap(BitmapFactory.decodeFile(s));
                        } else {
                            headerImageRect.setImageResource(R.mipmap.ic_shangchuangtupiang);
                            headerImageCircle.setImageResource(R.mipmap.ic_shangchuangtupiang);
                            Toast.makeText(FriendsDetailsActivity.this, "load images failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onProgress(Integer integer, long l) {
                    }
                });
            }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(msgCountReceiver);
    }

    ListView myListView;
    LinearLayout ll_friend_details;
    List<ActivityData> alAD;
    List listToShow1;
    MyApplication myApplication;
    final static int oneBox=1;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Gson gson=new Gson();
            Bundle bundle=msg.getData();
            String str;
            String creatorId;
            switch (msg.what){
                //这里获取活动和成员的关系表，可以进而得到 当前用户加入的所有活动 的id（就很尴尬）
                case 5:
                    str=bundle.getString("MTAlist");
                    List<ActivityAndMember> ps = gson.fromJson(str, new TypeToken<List<ActivityAndMember>>(){}.getType());
                    //外卖菜名
                    if(ps.size()>0) {
                        ArrayList<String> al = new ArrayList<>();
                        for (ActivityAndMember gameScore : ps) {
                            al.add(gameScore.getActivityId());
                        }
                        //根据菜名叫外卖，到了的外卖放到一号邮箱
                        GetFromBmob.getActivitysByActivityIds(oneBox, al, handler);
                    } else {
                        useAdapter1(new ArrayList());
                        myApplication.stopProgressDialog();
                    }
                    break;
                //上面的过程执行后，获得一系列的Activity，以为没事了吗，还要获取创建者的相关信息
                //1号邮箱拿了外卖，该去拿餐具了，取了一个箱子，分格子，分别标上那份外卖的厨师名，重复的覆盖
                case 6:
                    str=bundle.getString("alAD1");
                    alAD=gson.fromJson(str,new TypeToken<List<ActivityData>>(){}.getType());
                    Log.e("database","alAD.size:"+alAD.size());
                    Map<String, User> creatorMap = new HashMap<>();
                    for (int i = 0; i < alAD.size(); i++) {
                        creatorId = alAD.get(i).getCreatorId();
                        creatorMap.put(creatorId, new User());
                    }
                    //叫外卖小哥送餐具过来
                    GetFromBmob.getUsersByUserIds(oneBox, creatorMap, handler);
                    break;
                //这里获得了含多个键为userId,值为User的键值对的Map
                //餐具也取好了，为每份外卖分配一种餐具，放到桌面上
                case 7:
                    //打开1号邮箱餐具包装
                    str=bundle.getString("mapUser1");
                    //到这里，终于把 外卖和对应的餐具，放到了一起，并放到桌子上。
                    listToShow1=getActivityDataTopicData(putTheTable(str,alAD));
                    myApplication.listToShow=listToShow1;
                    useAdapter1(listToShow1);
                    myApplication.stopProgressDialog();
                    break;
                default:break;
            }
        }
    };
    //摆桌子，也就是把各数据分门别类地放好，以便显示
    private ArrayList putTheTable(String CanjuPackage,List<ActivityData> openedWaimaiPackage){
        Gson gson=new Gson();
        String creatorId;
        Type type = new TypeToken<Map<String, User>>() {}.getType();
        Map<String, User> userMap = gson.fromJson(CanjuPackage, type);
        //放一套餐具、外卖的大格子
        Map<String,Object> mapToAdd;
        //放餐具的小格子
        User userToPut;
        //放外卖的小格子
        ActivityData ADtoPut;
        //放18套餐具、外卖的桌子
        ArrayList<Map<String,Object>> list=new ArrayList<>();
        //开始摆放
        for(int i=0;i<openedWaimaiPackage.size();i++){
            mapToAdd=new HashMap<>();
            ADtoPut=openedWaimaiPackage.get(i);
            creatorId=ADtoPut.getCreatorId();
            userToPut=userMap.get(creatorId);
            mapToAdd.put("AD",ADtoPut);
            mapToAdd.put("creator",userToPut);
            list.add(mapToAdd);
        }
        return list;
    }

    public List<ActivityTopicForCircle> getActivityDataTopicData(List<Map<String,Object>> list) {
        List<ActivityTopicForCircle> topicList = new ArrayList<>();
        ActivityTopicForCircle activityTopicForCircle;
        ActivityData AD;
        User creator;
        for(int i=0;i<list.size();i++){
            activityTopicForCircle = new ActivityTopicForCircle();
            AD=(ActivityData) list.get(i).get("AD");
            creator=(User) list.get(i).get("creator");
            activityTopicForCircle.setADid(AD.getObjectId());
            activityTopicForCircle.setTopicImage(AD.getBackgroundURL());
            activityTopicForCircle.setTopicTitle(AD.getActivityName());
            activityTopicForCircle.setUserHeadImage(creator.getHeaderImageUri());
            activityTopicForCircle.setUserName(creator.getNickName());
            activityTopicForCircle.setTopicProgressbar(String.valueOf(R.mipmap.progressbar));
            topicList.add(activityTopicForCircle);
        }
        Collections.sort(topicList);
        return topicList;
    }

    //新的使用适配器
    public void useAdapter1(List list) {
        if(list.size()!=0) {
            myListView.setVisibility(View.VISIBLE);
            ll_friend_details.setVisibility(View.GONE);
            final List<ActivityTopicForCircle> topicList = list;
            ListViewAdapterForCircle listViewAdapterForCircle = new ListViewAdapterForCircle(this);
            listViewAdapterForCircle.setTopicList(topicList);
            myListView.setAdapter(listViewAdapterForCircle);
            myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String ADid = topicList.get(position).getADid();
                    CirclePushCardActivity.jump(ADid, FriendsDetailsActivity.this);
                }
            });
        } else {
            myListView.setVisibility(View.GONE);
            ll_friend_details.setVisibility(View.VISIBLE);
        }
    }
}