package com.runstart.circle;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.runstart.BmobBean.ActivityAndMember;
import com.runstart.BmobBean.Friend;
import com.runstart.BmobBean.User;
import com.runstart.R;
import com.runstart.circle.JianjiansListView.CircleActivityTopic;
import com.runstart.circle.JianjiansListView.NowJianjiansListView;
import com.runstart.history.MyApplication;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CircleFriendListActivity extends AppCompatActivity {

    ListView listView;
    ProgressDialog progressDialog;
    String userId;
    Map<String,Object> isTodayLikedMap;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Gson gson=new Gson();
            Bundle bundle=msg.getData();
            String str;
            switch (msg.what){
                case 1:
                    str=bundle.getString("friends");
                    List<Friend> friends = gson.fromJson(str, new TypeToken<List<Friend>>(){}.getType());
                    putTheLikeMap(friends);
                    GetFromBmob.getActivityMember(getIntent().getStringExtra("activityId"),handler);
                    break;
                case 5:
                    str=bundle.getString("MTAlist");
                    List<ActivityAndMember> ps = gson.fromJson(str, new TypeToken<List<ActivityAndMember>>(){}.getType());
                    Map<String,User> myMap=new HashMap<>();
                    for(int i=0;i<ps.size();i++){
                        myMap.put(ps.get(i).getMemberId(),new User());
                    }
                    GetFromBmob.getUsersByUserIds(3,myMap,handler);
                    break;
                case 11:
                    str=bundle.getString("mapUser3");
                    putTheTable(str);
                    handler.sendEmptyMessage(13);
                    break;
                case 13:
                    progressDialog.cancel();
                    break;
                default:break;
            }
        }
    };

    private void putTheLikeMap(List<Friend> friends){
        isTodayLikedMap=new HashMap<>();
        Friend friend;
        for(int i=0;i<friends.size();i++){
            friend=friends.get(i);
            isTodayLikedMap.put(friend.getFriendObjectId()+"friendId",friend);
            String likeDate;
            if(friend.getLikeDate()!=null)
                if(!friend.getLikeDate().equals("")){
                    likeDate=friend.getLikeDate();
                    Log.e("database","myid:"+friend.getUserObjectId()+",friendId:"+friend.getFriendObjectId()+",isFriend:"+friend.isFriend()+
                            ",friendLikedate:"+likeDate);
                    Log.e("database", "CommonUtils.getYimanTwoDay(friend.getLikeDate()):" + CommonUtils.getYimanTwoDay(likeDate));
                    if (CommonUtils.getYimanTwoDay(likeDate) >= 1) {
                        isTodayLikedMap.put(friend.getFriendObjectId()+"isLiked", false);
                    } else {
                        isTodayLikedMap.put(friend.getFriendObjectId() + "isLiked", true);
                    }
                }
        }
    }

    private void putTheTable(String str){
        Gson gson=new Gson();
        Type type = new TypeToken<Map<String, User>>() {}.getType();
        Map<String, User> userMap = gson.fromJson(str, type);
        User user;
        int i=1;
        List<CircleActivityTopic> topicList = new ArrayList<>();
        for (Map.Entry entry : userMap.entrySet()) {
            user=(User)entry.getValue();

            CircleActivityTopic activityTopic = new CircleActivityTopic();
            Friend friend=(Friend) isTodayLikedMap.get(user.getObjectId()+"friendId");
            activityTopic.setFriend(friend);
            activityTopic.setMemberId(user.getObjectId());
            activityTopic.setNumber(i);
            activityTopic.setUserHeadImage(user.getHeaderImageUri());
            activityTopic.setUserName(user.getNickName());
            activityTopic.setUserDistance((user.getRideDistance()+user.getRunDistance()+user.getWalkDistance())+"m");
            activityTopic.setLikeTimes(user.getLikeNumberForHistory());
            if(isTodayLikedMap.get(user.getObjectId()+"isLiked")!=null) {
                activityTopic.setLiked((Boolean)isTodayLikedMap.get(user.getObjectId()+"isLiked"));
            } else
                activityTopic.setLiked(false);

            topicList.add(activityTopic);
            i++;
        }
        Collections.sort(topicList);
        NowJianjiansListView.useAdapter(this,listView,topicList,isTodayLikedMap);
    }

    public static void jump(String activityId,Activity activity){
        Intent intent=new Intent(activity,CircleFriendListActivity.class);
        intent.putExtra("activityId",activityId);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_friend_list);
        this.userId=((MyApplication)getApplicationContext()).applicationMap.get("userObjectId");
        GetFromBmob.getFriendsByUserId(userId,handler);
        init_view();
    }

    private void init_view(){
        listView=(ListView)findViewById(R.id.lv);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(CircleFriendListActivity.this,"点击了整条listview，位置是："+position,Toast.LENGTH_SHORT).show();
            }
        });
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("正在获取数据...");
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(13);
            }
        }).start();
    }
}
