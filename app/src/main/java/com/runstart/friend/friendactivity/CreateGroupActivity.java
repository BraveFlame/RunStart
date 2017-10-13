package com.runstart.friend.friendactivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.runstart.BmobBean.Friend;
import com.runstart.BmobBean.Group;
import com.runstart.BmobBean.User;
import com.runstart.R;
import com.runstart.friend.adapter.AdapterForAddFriends;
import com.runstart.friend.adapter.ListViewForScrollView;
import com.runstart.friend.adapter.MyUtils;
import com.runstart.friend.adapter.PhotoUtils;
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
import cn.bmob.v3.listener.UploadFileListener;

public class CreateGroupActivity extends AppCompatActivity implements View.OnClickListener, AdapterForAddFriends.Callback {
    private Button goBack;
    private FloatingActionButton createGroup;
    private EditText groupName, individualSignature;
    private ListViewForScrollView friendsListView;

    private static final String TAG = CreateGroupActivity.class.getSimpleName();
    private ImageView selectImage;
    private MyHeaderImageView showHeadPortrait;

    private String path;

    private List<User> userList = new ArrayList<>();
    private List<Friend> friendList;
    private Map<String, Bitmap> bitmapMap = new ArrayMap<>();
    private List<User> orderedUserList = new ArrayList<>();
    private Map<String, String> selectedUserObjectIdMap = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        queryFriend();

        goBack = (Button)findViewById(R.id.goBack);
        createGroup = (FloatingActionButton) findViewById(R.id.createGroup);
        showHeadPortrait = (MyHeaderImageView)findViewById(R.id.showHeadPortrait);
        selectImage = (ImageView) findViewById(R.id.selectImage);
        groupName = (EditText)findViewById(R.id.groupName);
        individualSignature = (EditText)findViewById(R.id.individualSignature);
        friendsListView = (ListViewForScrollView)findViewById(R.id.friendsListView);

        goBack.setOnClickListener(this);
        selectImage.setOnClickListener(this);
        showHeadPortrait.setOnClickListener(this);
        createGroup.setOnClickListener(this);
    }

    private void queryFriend(){
        new BmobQuery<Friend>().setSQL("select * from Friend where userObjectId=? and isFriend=1")
                .setPreparedParams(new String[]{MyApplication.applicationMap.get("userObjectId")})
                .doSQLQuery(new SQLQueryListener<Friend>() {
                    @Override
                    public void done(BmobQueryResult<Friend> bmobQueryResult, BmobException e) {
                        if (e == null){
                            friendList = bmobQueryResult.getResults();
                            if (friendList.size() != 0){
                                queryUser();
                            }
                        }else {
                            Toast.makeText(CreateGroupActivity.this, "load friends failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.goBack:
                finish();
                break;
            case R.id.showHeadPortrait:
                options();
                break;
            case R.id.selectImage:
                options();
                break;
            case R.id.createGroup:
                final String groupNameStr = groupName.getText().toString();
                final String groupDetail = individualSignature.getText().toString();
                final List<String> memberObjectIdList = new ArrayList<>();
                memberObjectIdList.add(MyApplication.applicationMap.get("userObjectId"));
                Set<String> keySet = selectedUserObjectIdMap.keySet();
                for (String memberObjectId: keySet){
                    memberObjectIdList.add(selectedUserObjectIdMap.get(memberObjectId));
                }
                if (path != null){
                    final BmobFile bmobFile = new BmobFile(new File(path));
                    bmobFile.uploadblock(new UploadFileListener() {
                        @Override
                        public void done(BmobException e) {
                            new Group(MyApplication.applicationMap.get("userObjectId"), bmobFile.getUrl(),
                                    memberObjectIdList.size(), 0, groupNameStr, groupDetail, memberObjectIdList).save();
                        }
                    });
                }else {
                    new Group(MyApplication.applicationMap.get("userObjectId"), null,
                            memberObjectIdList.size(), 0, groupNameStr, groupDetail, memberObjectIdList).save();
                }

                Toast.makeText(this, "创建群成功！", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == PhotoUtils.NONE){
            return;
        }
        if (requestCode == PhotoUtils.PHOTOGRAPH) {
            File picture;
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                picture = new File(Environment.getExternalStorageDirectory() + PhotoUtils.imageName);
                if (!picture.exists()) {
                    picture = new File(Environment.getExternalStorageDirectory() + PhotoUtils.imageName);
                }
            } else {
                picture = new File(this.getFilesDir() + PhotoUtils.imageName);
                if (!picture.exists()) {
                    picture = new File(CreateGroupActivity.this.getFilesDir() + PhotoUtils.imageName);
                }
            }
            path = PhotoUtils.getPath(this);
            if (TextUtils.isEmpty(path)) {
                Log.e(TAG, "随机生成的用于存放剪辑后的图片的地址失败");
                return;
            }
            Uri imageUri = Uri.fromFile(new File(path));
            PhotoUtils.startPhotoZoom(CreateGroupActivity.this, Uri.fromFile(picture), PhotoUtils.PICTURE_HEIGHT, PhotoUtils.PICTURE_WIDTH, imageUri);
        }
        if (data == null)
            return;
        if (requestCode == PhotoUtils.PHOTOZOOM) {
            path = PhotoUtils.getPath(this);
            if (TextUtils.isEmpty(path)) {
                Log.e(TAG, "随机生成的用于存放剪辑后的图片的地址失败");
                return;
            }
            Uri imageUri = Uri.fromFile(new File(path));
            PhotoUtils.startPhotoZoom(CreateGroupActivity.this, data.getData(), PhotoUtils.PICTURE_HEIGHT, PhotoUtils.PICTURE_WIDTH, imageUri);
        }
        if (requestCode == PhotoUtils.PHOTORESOULT) {
            Bitmap bitmap = PhotoUtils.convertToBitmap(path, PhotoUtils.PICTURE_HEIGHT, PhotoUtils.PICTURE_WIDTH);
            if(bitmap != null){
                showHeadPortrait.setImageBitmap(bitmap);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void queryUser(){
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
                                Toast.makeText(CreateGroupActivity.this, "load friends failed", Toast.LENGTH_SHORT).show();
                            }
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
                            showResult();
                        }
                    }
                }else {
                    Toast.makeText(CreateGroupActivity.this, "load friends' images failed", Toast.LENGTH_SHORT).show();
                }
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

    private void options(){
        new AlertDialog.Builder(this).setTitle("请选择图片来源").setCancelable(true)
                .setItems(new String[]{"拍照", "相册"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0){
                            try {
                                PhotoUtils.photograph(CreateGroupActivity.this);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }else {
                            PhotoUtils.selectPictureFromAlbum(CreateGroupActivity.this);
                        }
                    }
                }).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
