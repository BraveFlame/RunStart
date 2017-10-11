package com.runstart.friend.friendactivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.runstart.BmobBean.Friend;
import com.runstart.BmobBean.User;
import com.runstart.R;
import com.runstart.friend.adapter.AdapterForSearchFriends;
import com.runstart.friend.adapter.ListViewForScrollView;
import com.runstart.friend.adapter.MyLock;
import com.runstart.friend.adapter.MyUtils;
import com.runstart.history.MyApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.SQLQueryListener;

public class AddFriendActivity extends AppCompatActivity {
    private ListViewForScrollView listView;
    private EditText search_edit_input;
    private ImageView search_iv_delete;
    private LinearLayout rootView;

    private List<User> userList = new ArrayList<>();
    private Map<String, Friend> friendMap = new ArrayMap<>();
    private Map<String, Bitmap> bitmapMap = new ArrayMap<>();

    private Map<String, Bitmap> forDetailsBitmap = new ArrayMap<>();
    private User[] orderedUserArr;

    private final int FINISH_LOADING = 0x111;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == FINISH_LOADING){
                showResult("walkDistance+runDistance+rideDistance");
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        rootView = (LinearLayout)findViewById(R.id.rootView);
        listView = (ListViewForScrollView)findViewById(R.id.search_lv_tips);
        search_edit_input = (EditText)findViewById(R.id.search_edit_input);
        search_iv_delete = (ImageView)findViewById(R.id.search_iv_delete);

        queryUser();

        init();

        showResult("walkDistance+runDistance+rideDistance");
    }

    private void queryUser(){
        new BmobQuery<User>().setSQL("select * from User").setPreparedParams(null).doSQLQuery(new SQLQueryListener<User>() {
            @Override
            public void done(BmobQueryResult<User> bmobQueryResult, BmobException e) {
                if (e == null){
                    userList = bmobQueryResult.getResults();
                    queryFriend();
                    queryBitmap();
                }else {
                    Toast.makeText(AddFriendActivity.this, "load friends failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void init(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AddFriendActivity.this, FriendsDetailsActivity.class);
                int index = Integer.parseInt(((TextView)view.findViewById(R.id.rankings)).getText().toString()) - 1;
                User user = orderedUserArr[index];
                Friend friend = friendMap.get(user.getObjectId());
                Bundle data = new Bundle();
                data.putSerializable("friend", friend);
                data.putSerializable("user", user);
                ArrayList<Bitmap> bitmapList = new ArrayList<>();
                bitmapList.add(bitmapMap.get(user.getObjectId()));
                data.putSerializable("headerImage", bitmapList);
                intent.putExtras(data);
                startActivity(intent);
            }
        });
    }

    private void queryFriend(){
        for(final User user: userList){
            if (Thread.activeCount() >= 5){
                try{
                    Thread.sleep(200);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            synchronized (Friend.class){
                new BmobQuery<Friend>().setSQL("select * from Friend where friendObjectId=?")
                        .setPreparedParams(new String[]{user.getObjectId()}).doSQLQuery(new SQLQueryListener<Friend>() {
                            @Override
                            public void done(BmobQueryResult<Friend> bmobQueryResult, BmobException e) {
                                if (e == null){
                                    synchronized (MyLock.lock1) {
                                        List<Friend> friendList = bmobQueryResult.getResults();
                                        if (friendList.size() == 0){
                                            friendMap.put(user.getObjectId(), null);
                                        } else {
                                            friendMap.put(user.getObjectId(), friendList.get(0));
                                        }
                                        if (bitmapMap.size() == userList.size() && friendMap.size() == userList.size()) {
                                            handler.sendEmptyMessage(FINISH_LOADING);
                                        }
                                    }
                                }else {
                                    Toast.makeText(AddFriendActivity.this, "load friends' info failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    }

    private void queryBitmap(){
        File saveFile;
        String saveName;
        String headerImageUri;
        for(final User user: userList){
                final int objectIdLength = user.getObjectId().length();
                synchronized (MyLock.lock2){
                    saveName = MyUtils.getStringToday() + user.getObjectId() + ".png";
                    headerImageUri = user.getHeaderImageUri();
                    saveFile = new File(Environment.getExternalStorageDirectory() + File.separator + "lovesportimage", saveName);
                    if (headerImageUri == null || headerImageUri.length() == 0){
                        bitmapMap.put(saveFile.toString().substring(saveFile.toString().length() - objectIdLength - 4, saveFile.toString().length() - 4), null);
                        if (bitmapMap.size() == userList.size() && friendMap.size() == userList.size()){
                            handler.sendEmptyMessage(FINISH_LOADING);
                        }
                        continue;
                    }
                }
                new BmobFile(saveName, "", headerImageUri).download(saveFile, new DownloadFileListener() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null){
                            synchronized (MyLock.lock2){
                                bitmapMap.put(s.substring(s.length() - objectIdLength - 4, s.length() - 4), BitmapFactory.decodeFile(s));
                                if (bitmapMap.size() == userList.size() && friendMap.size() == userList.size()){
                                    handler.sendEmptyMessage(FINISH_LOADING);
                                }
                            }
                        }else {
                            Toast.makeText(AddFriendActivity.this, "load images failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onProgress(Integer integer, long l) {}
                });
        }
    }

    private void showResult(final String sportCategory){
        final String[] mItemCols = new String[]{"rankings", "headerImage", "phoneNumber", "isFriend", "nickName"};
        final int[] mItemIds = new int[]{R.id.rankings, R.id.headerImage, R.id.phoneNumber, R.id.isFriend, R.id.nickName};

        search_edit_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!"".equals(s.toString())) {
                    rootView.setBackgroundColor(0xff888888);
                    search_iv_delete.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.VISIBLE);
                    if (orderBySportCategory(sportCategory).size() != 0){
                        List<Map<String, Object>> searchResultList = new ArrayList<>();
                        for (Map<String, Object> map:orderBySportCategory(sportCategory)){
                            String nickName = map.get("nickName").toString();
                            String phoneNumber = map.get("phoneNumber").toString();
                            if (phoneNumber.contains(s.toString())){
                                searchResultList.add(map);
                            } else if (nickName != null && nickName.contains(s.toString())){
                                searchResultList.add(map);
                            }
                        }
                        listView.setAdapter(new AdapterForSearchFriends(AddFriendActivity.this,
                                searchResultList, R.layout.item_search_friend, mItemCols, mItemIds));
                    }
                } else {
                    search_iv_delete.setVisibility(View.GONE);
                    listView.setVisibility(View.GONE);
                    rootView.setBackgroundColor(0x00f2f1f1);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        search_iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_edit_input.setText("");
                search_iv_delete.setVisibility(View.GONE);
            }
        });
    }

    private ArrayList<Map<String, Object>> orderBySportCategory(String sportCategory){
        User[] orderedUsers = getOrderedUsers(sportCategory);
        orderedUserArr = orderedUsers;

        ArrayList<Map<String, Object>> mapList = new ArrayList<>();
        for (int i = 0; i < userList.size(); i++){
            User user = orderedUsers[i];
            if (MyApplication.applicationMap.get("userObjectId").equals(user.getObjectId())){
                continue;
            }
            Friend friend = friendMap.get(user.getObjectId());

            Map<String, Object> map = new HashMap<>();
            if (friend == null || friend.isFriend() == 0){
                map.put("isFriend", "（You are not friends）");
            }else {
                map.put("isFriend", "（You are friends）");
            }
            map.put("rankings", i + 1);
            map.put("headerImage", bitmapMap.get(user.getObjectId()));
            forDetailsBitmap.put(user.getObjectId(), bitmapMap.get(user.getObjectId()));
            map.put("nickName", user.getNickName());
            map.put("phoneNumber", user.getPhoneNumber() + "");
            map.put("sportDistance", getSportDistance(user, sportCategory) + " m");

            if (friend == null || friend.getLikeDate().length() == 0) {
                map.put("likeImage", R.mipmap.ic_zan2);
            } else {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                if (!((year == Integer.parseInt(friend.getLikeDate().substring(0, 4))) &&
                        (month == Integer.parseInt(friend.getLikeDate().substring(4, 6))) &&
                        (day == Integer.parseInt(friend.getLikeDate().substring(6, 8))))) {
                    map.put("likeImage", R.mipmap.ic_zan2);
                } else {
                    map.put("likeImage", R.mipmap.ic_zan);
                }
            }
            map.put("likeNumberForHistory", user.getLikeNumberForHistory());
            mapList.add(map);
        }
        return mapList;
    }

    private User[] getOrderedUsers(String sportCategory){
        User[] orderedUsers = new User[userList.size()];
        userList.toArray(orderedUsers);
        for (int i = 0; i < userList.size() - 1; i++){
            for (int j = i + 1; j < userList.size(); j++){
                if (getSportDistance(orderedUsers[i], sportCategory) < getSportDistance(orderedUsers[j], sportCategory)){
                    User tempUser = orderedUsers[i];
                    orderedUsers[i] = orderedUsers[j];
                    orderedUsers[j] = tempUser;
                }
            }
        }
        return orderedUsers;
    }

    private int getSportDistance(User user, String sportCategory){

        int sportDistance = 0;
        switch (sportCategory){
            case "walkDistance+runDistance+rideDistance":
                sportDistance = user.getWalkDistance() + user.getRunDistance() + user.getRideDistance();
                break;
            case "walkDistance":
                sportDistance = user.getWalkDistance();
                break;
            case "runDistance":
                sportDistance = user.getRunDistance();
                break;
            case "rideDistance":
                sportDistance = user.getRideDistance();
                break;
        }
        return sportDistance;
    }
}
