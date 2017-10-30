package com.runstart.friend.friend_activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.runstart.bean.Friend;
import com.runstart.bean.User;
import com.runstart.R;
import com.runstart.friend.adapter.AdapterForSearchFriends;
import com.runstart.friend.adapter.ListViewForScrollView;
import com.runstart.friend.adapter.MyLock;
import com.runstart.friend.adapter.MyUtils;
import com.runstart.MyApplication;

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
import cn.bmob.v3.listener.SQLQueryListener;

public class AddFriendActivity extends AppCompatActivity {
    private ListViewForScrollView listView;
    private EditText search_edit_input;
    private ImageView search_iv_delete;
    private LinearLayout rootView;

    private List<User> userList = new ArrayList<>();
    private Map<String, Friend> friendMap = new ArrayMap<>();
    private Map<String, Bitmap> bitmapMap = new ArrayMap<>();
    private List<Map<String, Object>> totalMapList;

    private User[] orderedUserArr;

    private boolean finishLoadingUser = false;
    private boolean finishLoadingFriend = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        rootView = (LinearLayout)findViewById(R.id.rootView);
        listView = (ListViewForScrollView)findViewById(R.id.search_lv_tips);
        search_edit_input = (EditText)findViewById(R.id.search_edit_input);
        search_iv_delete = (ImageView)findViewById(R.id.search_iv_delete);

        queryUser();
        queryFriend();

        init();
    }

    private void queryUser(){
        new BmobQuery<User>().setSQL("select * from User").setPreparedParams(null).doSQLQuery(new SQLQueryListener<User>() {
            @Override
            public void done(BmobQueryResult<User> bmobQueryResult, BmobException e) {
                if (e == null){
                    userList = bmobQueryResult.getResults();
                    getOrderedUsers("walkDistance+runDistance+rideDistance");
                    for (User user:userList){
                        queryBitmap(user);
                    }
                    finishLoadingUser = true;
                    if (finishLoadingFriend && finishLoadingUser){
                        orderBySportCategory();
                    }
                }else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void init(){
        final String[] mItemCols = new String[]{"rankings", "headerImage", "phoneNumber", "isFriend", "nickName"};
        final int[] mItemIds = new int[]{R.id.rankings, R.id.headerImage, R.id.phoneNumber, R.id.isFriend, R.id.nickName};

        search_edit_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if ((!"".equals(s.toString())) && (totalMapList != null)) {
                    rootView.setBackgroundColor(0xff888888);
                    search_iv_delete.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.VISIBLE);

                    if (totalMapList.size() != 0){
                        List<Map<String, Object>> searchResultList = new ArrayList<>();
                        for (Map<String, Object> map: totalMapList){
                            Object nickNameObj = map.get("nickName");
                            String nickName = nickNameObj == null? null:nickNameObj.toString();
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
            public void afterTextChanged(Editable s) {

            }
        });

        search_iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_edit_input.setText("");
                search_iv_delete.setVisibility(View.GONE);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AddFriendActivity.this, FriendsDetailsActivity.class);
                int index = Integer.parseInt(((TextView)view.findViewById(R.id.rankings)).getText().toString()) - 1;
                User user = orderedUserArr[index];
                Friend friend = friendMap.get(MyApplication.applicationMap.get("userObjectId") + user.getObjectId());
                Bundle data = new Bundle();
                data.putSerializable("friend", friend);
                data.putSerializable("user", user);
                intent.putExtras(data);
                startActivity(intent);
            }
        });
    }

    private void queryFriend(){
        new BmobQuery<Friend>().setSQL("select * from Friend").setPreparedParams(null)
                .doSQLQuery(new SQLQueryListener<Friend>() {
                    @Override
                    public void done(BmobQueryResult<Friend> bmobQueryResult, BmobException e) {
                        if (e == null){
                            List<Friend> friendList = bmobQueryResult.getResults();
                            for (Friend friend: friendList){
                                friendMap.put(friend.getUserObjectId() + friend.getFriendObjectId(), friend);
                            }
                            finishLoadingFriend = true;
                            if (finishLoadingFriend && finishLoadingUser){
                                orderBySportCategory();
                            }
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void queryBitmap(User user){
        File saveFile;
        String saveName;
        String headerImageUri;
        final int objectIdLength = user.getObjectId().length();
        synchronized (MyLock.lock2){
            saveName = MyUtils.getStringToday() + user.getObjectId() + ".png";
            headerImageUri = user.getHeaderImageUri();
            saveFile = new File(Environment.getExternalStorageDirectory() + File.separator + "lovesportimage", saveName);
            if (headerImageUri == null || headerImageUri.length() == 0){
                bitmapMap.put(saveFile.toString().substring(saveFile.toString().length() - objectIdLength - 4, saveFile.toString().length() - 4), null);
                if (bitmapMap.size() == userList.size()){
                    orderBySportCategory();
                }
                return;
            }
        }
        new BmobFile(saveName, "", headerImageUri).download(saveFile, new DownloadFileListener() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null){
                    synchronized (MyLock.lock2){
                        bitmapMap.put(s.substring(s.length() - objectIdLength - 4, s.length() - 4), BitmapFactory.decodeFile(s));
                        if (bitmapMap.size() == userList.size()){
                            orderBySportCategory();
                        }
                    }
                }else {
                    e.printStackTrace();
                }
            }
            @Override
            public void onProgress(Integer integer, long l) {}
        });
    }

    private void orderBySportCategory(){

        ArrayList<Map<String, Object>> mapList = new ArrayList<>();
        for(int i = 0; i < userList.size(); i ++){
            User user = orderedUserArr[i];
            Map<String, Object> map = new HashMap<>();
            map.put("rankings", i + 1);
            map.put("headerImage", bitmapMap.get(user.getObjectId()));
            map.put("nickName", user.getNickName());
            map.put("phoneNumber", user.getPhoneNumber() + "");

            map.put("isFriend", "（You are not friends）");
            String myUserObjectId = MyApplication.applicationMap.get("userObjectId");
            Friend friend = friendMap.get(myUserObjectId + user.getObjectId());
            if (friend != null) {
                if (friend.isFriend() == 1) {
                    map.put("isFriend", "（You are friends）");
                } else if(user.getObjectId().equals(myUserObjectId)){
                    map.put("isFriend", "  ");
                }else {
                    map.put("isFriend", "（You are not friends）");
                }
            } else if (myUserObjectId.equals(user.getObjectId())){
                map.put("isFriend", "");
            }else {
                map.put("isFriend", "（You are not friends）");
            }
            mapList.add(map);

        }
        totalMapList =  mapList;

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
        orderedUserArr = orderedUsers;
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
