package com.runstart.friend.friendactivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.runstart.BmobBean.Friend;
import com.runstart.BmobBean.Group;
import com.runstart.BmobBean.User;
import com.runstart.R;
import com.runstart.friend.adapter.AdapterForAddFriends;
import com.runstart.friend.adapter.MyUtils;
import com.runstart.history.MyApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.SQLQueryListener;

public class InviteFriendToGroupActivity extends AppCompatActivity implements AdapterForAddFriends.Callback {

    private ListView friendsListView;
    private Button determine;

    private Group group = null;
    private List<User> userList = new ArrayList<>();
    private List<Friend> friendList;
    private Map<String, Bitmap> bitmapMap = new ArrayMap<>();
    private List<User> orderedUserList = new ArrayList<>();
    private Map<String, String> selectedUserObjectIdMap = new HashMap();

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friend_to_group);

        progressDialog = new ProgressDialog(InviteFriendToGroupActivity.this);

        friendsListView = (ListView) findViewById(R.id.friendsListView);
        determine = (Button)findViewById(R.id.determine);

        MyUtils.showProgressDialog(progressDialog);
        final String groupObjectId = getIntent().getStringExtra("groupObjectId");
        new BmobQuery<Group>().setSQL("select * from Group where objectId=?").setPreparedParams(new String[]{groupObjectId})
                .doSQLQuery(new SQLQueryListener<Group>() {
                    @Override
                    public void done(BmobQueryResult<Group> bmobQueryResult, BmobException e) {
                        if (e == null) {
                            group = bmobQueryResult.getResults().get(0);
                            group.setObjectId(group.getObjectId());
                        }
                    }
                });

        new BmobQuery<Friend>().setSQL("select * from Friend where userObjectId=? and isFriend=1")
                .setPreparedParams(new String[]{MyApplication.applicationMap.get("userObjectId")})
                .doSQLQuery(new SQLQueryListener<Friend>() {
            @Override
            public void done(BmobQueryResult<Friend> bmobQueryResult, BmobException e) {
                if (e == null){
                    friendList = bmobQueryResult.getResults();
                    queryUser();
                }else {
                    e.printStackTrace();                }
            }
        });

        determine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> memberObjectIdList = group.getMemberObjectIdList();
                Set<String> keySet = selectedUserObjectIdMap.keySet();
                for (String memberObjectId: keySet){
                    memberObjectIdList.add(selectedUserObjectIdMap.get(memberObjectId));
                }
                group.setMemberObjectIdList(memberObjectIdList);
                group.setMemberCount(memberObjectIdList.size());
                group.update();
                Toast.makeText(InviteFriendToGroupActivity.this, "Invited friends successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void queryUser(){
        if (friendList.size() == 0){
            return;
        }
        String sql = "select * from User where objectId=?";
        for (int i = 0; i < friendList.size(); i++){
            new BmobQuery<User>().setSQL(sql).setPreparedParams(new String[]{friendList.get(i).getFriendObjectId()})
                    .doSQLQuery(new SQLQueryListener<User>() {
                        @Override
                        public void done(BmobQueryResult<User> bmobQueryResult, BmobException e) {
                            if (e == null){
                                synchronized (InviteFriendToGroupActivity.class) {
                                    User user = bmobQueryResult.getResults().get(0);
                                    userList.add(user);
                                    queryBitmap(user);
                                }
                            }else {
                                e.printStackTrace();                            }
                        }
                    });
        }
    }
    private void queryBitmap(User user){
        final int objectIdLength = user.getObjectId().length();
        String saveName = MyUtils.getStringToday() + user.getObjectId() + ".png";
        String headerImageUri = user.getHeaderImageUri();
        File saveFile = new File(Environment.getExternalStorageDirectory() + File.separator + "lovesportimage", saveName);
        if (headerImageUri == null || headerImageUri.length() == 0){
            synchronized (Object.class){
                bitmapMap.put(saveFile.toString().substring(saveFile.toString().length() - objectIdLength - 4, saveFile.toString().length() - 4), null);
                if (bitmapMap.size() == friendList.size()){
                    if (progressDialog.isShowing()){
                        MyUtils.dismissProgressDialog(progressDialog);
                    }
                    showResult();
                }
                return;
            }
        }
        new BmobFile(saveName, "", headerImageUri).download(saveFile, new DownloadFileListener() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null){
                    synchronized (Object.class){
                        bitmapMap.put(s.substring(s.length() - objectIdLength - 4, s.length() - 4), BitmapFactory.decodeFile(s));
                        if (bitmapMap.size() == friendList.size()){
                            if (progressDialog.isShowing()){
                                MyUtils.dismissProgressDialog(progressDialog);
                            }
                            showResult();
                        }
                    }
                }else {
                    e.printStackTrace();                }
            }
            @Override
            public void onProgress(Integer integer, long l) {}
        });
    }
    private void showResult(){

        String[] mItemCols = new String[]{"rankings", "headerImage", "nickName", "sportDistance", "addFriendToMyGroup"};
        int[] mItemIds = new int[]{R.id.rankings, R.id.headerImage, R.id.nickName, R.id.sportDistance, R.id.addFriendToMyGroup};

        AdapterForAddFriends adapter = new AdapterForAddFriends(this, orderedByDistance(),
                R.layout.item_adding_friend, mItemCols, mItemIds, this);
        friendsListView.setAdapter(adapter);
    }
    private ArrayList<Map<String, Object>> orderedByDistance(){
        List<String> memberObjectIdList = group.getMemberObjectIdList();

        User[] orderedUsers = getOrderedUsers();
        orderedUserList = Arrays.asList(orderedUsers);

        ArrayList<Map<String, Object>> mapList = new ArrayList<>();
        for (int i = 0; i < userList.size(); i++){
            User user = orderedUsers[i];
            Map<String, Object> map = new HashMap<>();
            map.put("rankings", i + 1);
            map.put("headerImage", bitmapMap.get(user.getObjectId()));
            map.put("nickName", user.getNickName());
            map.put("sportDistance", getSportDistance(user) + " m");
            map.put("addFriendToMyGroup", R.mipmap.add);
            boolean hasJoined = false;
            for (String memberObjectId:memberObjectIdList){
                if (memberObjectId.equals(user.getObjectId())){
                    hasJoined = true;
                }
            }
            if (hasJoined){
                map.put("addFriendToMyGroup", R.mipmap.ic_gou);
            }
            mapList.add(map);
        }
        return mapList;
    }
    private User[] getOrderedUsers(){
        User[] orderedUsers = new User[userList.size()];
        userList.toArray(orderedUsers);
        for (int i = 0; i < userList.size() - 1; i++){
            for (int j = i + 1; j < userList.size(); j++){
                if (getSportDistance(orderedUsers[i]) < getSportDistance(orderedUsers[j])){
                    User tempUser = orderedUsers[i];
                    orderedUsers[i] = orderedUsers[j];
                    orderedUsers[j] = tempUser;
                }
            }
        }
        return orderedUsers;
    }
    private int getSportDistance(User user){
        return user.getWalkDistance() + user.getRunDistance() + user.getRideDistance();
    }

    @Override
    public void click(View view) {
        LinearLayout parent = (LinearLayout)view.getParent();
        int index = Integer.parseInt(((TextView)parent.findViewById(R.id.rankings)).getText().toString()) - 1;

        List<String> memberObjectIdList = group.getMemberObjectIdList();
        for (String memberObjectId: memberObjectIdList){
            if (memberObjectId.equals(orderedUserList.get(index).getObjectId())){
                return;
            }
        }

        int tag = Integer.parseInt((String)view.getTag());
        if (tag == 0){
            ((ImageView)view).setImageResource(R.mipmap.rectangle_39copy_2);
            view.setTag(1 + "");
            selectedUserObjectIdMap.put(orderedUserList.get(index).getObjectId(), orderedUserList.get(index).getObjectId());
        }else {
            ((ImageView)view).setImageResource(R.mipmap.add);
            view.setTag(0 + "");
            selectedUserObjectIdMap.remove(orderedUserList.get(index).getObjectId());
        }
    }
}
