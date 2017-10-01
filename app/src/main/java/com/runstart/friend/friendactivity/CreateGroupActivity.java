package com.runstart.friend.friendactivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.runstart.friend.adapter.PhotoUtils;
import com.runstart.history.MyApplication;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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

    private List<Friend> friends;
    private List<User> users = new ArrayList<>();
    private Map<String, Bitmap> bitmapMap = new ArrayMap<>();
    private List<User> orderedUserList = new ArrayList<>();
    private Map<String, String> selectedUserObjectIdMap = new HashMap();

    private Button goBack, createGroup;
    private EditText groupName, individualSignature;
    private ListViewForScrollView friendsListView;

    private static final String TAG = CreateGroupActivity.class.getSimpleName();
    private ImageView selectImage;
    private MyHeaderImageView showHeadPortrait;

    private String path;
    private Bitmap groupImageBitmap = null;

    private final int FINISH_LOADING_FRIEND = 0x111;
    private final int FINISH_LOADING_BITMAP = 0x112;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == FINISH_LOADING_FRIEND){
                getUser();
            }
            if (msg.what == FINISH_LOADING_BITMAP){
                showResult();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        String sql_friend = "select * from Friend where userObjectId=? and isFriend=1";
        BmobQuery<Friend> query = new BmobQuery<>();
        query.setSQL(sql_friend);
        query.setPreparedParams(new String[]{MyApplication.applicationMap.get("userObjectId")});
        query.doSQLQuery(new SQLQueryListener<Friend>() {
            @Override
            public void done(BmobQueryResult<Friend> bmobQueryResult, BmobException e) {
                if (e == null){
                    friends = bmobQueryResult.getResults();
                    handler.sendEmptyMessage(FINISH_LOADING_FRIEND);
                }else {
                    Toast.makeText(CreateGroupActivity.this, "load friends failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        goBack = (Button)findViewById(R.id.goBack);
        createGroup = (Button)findViewById(R.id.createGroup);
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
                final BmobFile bmobFile = new BmobFile(new File(path));
                bmobFile.uploadblock(new UploadFileListener() {
                    @Override
                    public void done(BmobException e) {
                        new Group(MyApplication.applicationMap.get("userObjectId"), bmobFile.getUrl(),
                                memberObjectIdList.size(), 0, groupNameStr, groupDetail, memberObjectIdList).save();
                    }
                });
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
            groupImageBitmap = bitmap;
            if(bitmap != null){
                showHeadPortrait.setImageBitmap(bitmap);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getUser(){
        if ((users.size() == friends.size()) && (friends.size() != 0)){
            return;
        }
        for (int i = 0; i < friends.size(); i++){
            String sql = "select * from User where objectId=?";
            new BmobQuery<User>().setSQL(sql).setPreparedParams(new String[]{friends.get(i).getFriendObjectId()})
                    .doSQLQuery(new SQLQueryListener<User>() {
                        @Override
                        public void done(BmobQueryResult<User> bmobQueryResult, BmobException e) {
                            if (e == null){
                                User user = bmobQueryResult.getResults().get(0);
                                users.add(user);
                                getBitmap(user);
                            }else {
                                Toast.makeText(CreateGroupActivity.this, "load friends failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    private void getBitmap(User user){
        final int objectIdLength = user.getObjectId().length();
        new BmobFile(getStringToday() + user.getObjectId() + ".png", "", user.getHeaderImageUri())
                .download(new File(Environment.getExternalStorageDirectory() + File.separator + "lovesportimage",
                        getStringToday() + user.getObjectId() + ".png"), new DownloadFileListener() {
                    @Override
                    public void done(String s, BmobException e) {
                        bitmapMap.put(s.substring(s.length() - objectIdLength - 4, s.length() - 4), BitmapFactory.decodeFile(s));
                        if (bitmapMap.size() == friends.size()){
                            handler.sendEmptyMessage(FINISH_LOADING_BITMAP);
                        }
                    }
                    @Override
                    public void onProgress(Integer integer, long l) {
                    }
                });
    }
    private void showResult(){

        if (users.size() == 0){
            return;
        }

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
        for (int i = 0; i < users.size(); i++){
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
        User[] orderedUsers = new User[users.size()];
        users.toArray(orderedUsers);
        for (int i = 0; i < users.size() - 1; i++){
            for (int j = i + 1; j < users.size(); j++){
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
    public static String getStringToday() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = formatter.format(currentTime);
        return dateString;
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
