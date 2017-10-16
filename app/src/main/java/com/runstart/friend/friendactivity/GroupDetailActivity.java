package com.runstart.friend.friendactivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.runstart.BmobBean.Group;
import com.runstart.BmobBean.User;
import com.runstart.R;
import com.runstart.friend.adapter.ListViewForScrollView;
import com.runstart.friend.adapter.MyUtils;
import com.runstart.friend.adapter.PhotoUtils;
import com.runstart.history.MyApplication;

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
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class GroupDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout goGroupChatLayout, seePeopleOfGroupLayout;
    private TextView titleGroupName, groupName, memberCount, distance, groupDetail;
    private Button goBack, menuButton, goGroupChat, seePeopleOfGroup;
    private FloatingActionButton joinGroup;
    private MyHeaderImageView groupImage, latestTalkerImage, imageByKcal0, imageByKcal1,
            imageByKcal2, imageByKcal3, imageByKcal4, imageByKcal5;
    private MyHeaderImageView[] headerImageViews;
    private ListViewForScrollView activityListView;

    private PopupWindow menuPopupWindow;
    private boolean flag = true;

    private Map<String, Bitmap> bitmapMap = new ArrayMap<>();
    private List<User> userList = new ArrayList<>();
    private String groupObjectId = "";

    private Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        goGroupChatLayout = (LinearLayout)findViewById(R.id.goGroupChatLayout);
        seePeopleOfGroupLayout = (LinearLayout)findViewById(R.id.seePeopleOfGroupLayout);
        titleGroupName = (TextView)findViewById(R.id.titleGroupName);
        groupName = (TextView)findViewById(R.id.groupName);
        memberCount = (TextView)findViewById(R.id.memberCount);
        distance = (TextView)findViewById(R.id.distance);
        groupDetail = (TextView)findViewById(R.id.groupDetail);
        goBack = (Button)findViewById(R.id.goBack);
        menuButton = (Button)findViewById(R.id.menuButton);
        goGroupChat = (Button)findViewById(R.id.goGroupChat);
        seePeopleOfGroup = (Button)findViewById(R.id.seePeopleOfGroup);
        joinGroup = (FloatingActionButton)findViewById(R.id.joinGroup);
        groupImage = (MyHeaderImageView) findViewById(R.id.groupImage);
        latestTalkerImage = (MyHeaderImageView) findViewById(R.id.latestTalkerImage);

        imageByKcal0 = (MyHeaderImageView) findViewById(R.id.imageByKcal0);
        imageByKcal1 = (MyHeaderImageView) findViewById(R.id.imageByKcal1);
        imageByKcal2 = (MyHeaderImageView) findViewById(R.id.imageByKcal2);
        imageByKcal3 = (MyHeaderImageView) findViewById(R.id.imageByKcal3);
        imageByKcal4 = (MyHeaderImageView) findViewById(R.id.imageByKcal4);
        imageByKcal5 = (MyHeaderImageView) findViewById(R.id.imageByKcal5);
        headerImageViews = new MyHeaderImageView[]{imageByKcal0, imageByKcal1, imageByKcal2,
                imageByKcal3, imageByKcal4, imageByKcal5};

        activityListView = (ListViewForScrollView)findViewById(R.id.activityListView);

        goBack.setOnClickListener(this);
        menuButton.setOnClickListener(this);
        goGroupChat.setOnClickListener(this);
        seePeopleOfGroup.setOnClickListener(this);
        goGroupChatLayout.setOnClickListener(this);
        seePeopleOfGroupLayout.setOnClickListener(this);
        groupImage.setOnClickListener(this);

        Map<String, Object> groupMap = ((ArrayList<Map<String, Object>>) getIntent().getSerializableExtra("groupDetail")).get(0);

        final List<String> memberObjectIdList = (List<String>) groupMap.get("memberObjectIdList");

        String memberCountStr = groupMap.get("memberCount").toString();
        String distanceStr = groupMap.get("distance").toString();
        group = new Group(groupMap.get("creatorId").toString(), null,
                Integer.parseInt(memberCountStr.substring(0, memberCountStr.length() - " people".length())),
                Integer.parseInt(distanceStr.substring(0, distanceStr.length() - 2)), groupMap.get("groupName").toString(),
                groupMap.get("groupDetail").toString(), memberObjectIdList);

        groupObjectId = groupMap.get("objectId").toString();
        initPopupMenuWindow(groupMap.get("creatorId").toString(), groupMap.get("objectId").toString(), memberObjectIdList);

        titleGroupName.setText(groupMap.get("groupName").toString());
        groupName.setText(groupMap.get("groupName").toString());
        memberCount.setText(groupMap.get("memberCount").toString());
        distance.setText(groupMap.get("distance").toString());
        groupDetail.setText(groupMap.get("groupDetail").toString());
        groupImage.setImageBitmap((Bitmap) groupMap.get("groupImage"));
        if (groupMap.get("groupImage") == null){
            groupImage.setImageResource(R.mipmap.ic_shangchuangtupiang);
        }

        for (final String memberObjectId: memberObjectIdList){
           new BmobQuery<User>().setSQL("select * from User where objectId=?")
                   .setPreparedParams(new String[]{memberObjectId}).doSQLQuery(new SQLQueryListener<User>() {
               @Override
               public void done(BmobQueryResult<User> bmobQueryResult, BmobException e) {
                   if (e == null){
                       synchronized (GroupDetailActivity.class){
                           User user = bmobQueryResult.getResults().get(0);
                           userList.add(user);
                           queryBitmap(user, memberObjectIdList.size());
                       }
                   }else {
                       e.printStackTrace();                   }
               }
           });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void queryBitmap(User user, final int memberCount){
        final int objectIdLength = user.getObjectId().length();
        String saveName = MyUtils.getStringToday() + user.getObjectId() + ".png";
        String imageUri = user.getHeaderImageUri();
        File saveFile = new File(Environment.getExternalStorageDirectory() + File.separator + "lovesportimage", saveName);
        if (imageUri == null || imageUri.length() == 0){
            bitmapMap.put(saveFile.toString().substring(saveFile.toString().length() - objectIdLength - 4, saveFile.toString().length() - 4), null);
            if (bitmapMap.size() == memberCount){
                showOrderedImagesByKcal(memberCount);
            }
            return;
        }
        new BmobFile(saveName, "", imageUri).download(saveFile, new DownloadFileListener() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null){
                    bitmapMap.put(s.substring(s.length() - objectIdLength - 4, s.length() - 4), BitmapFactory.decodeFile(s));
                    if (bitmapMap.size() == memberCount){
                        showOrderedImagesByKcal(memberCount);
                    }
                }
            }
            @Override
            public void onProgress(Integer integer, long l) {
            }
        });
    }

    private void showOrderedImagesByKcal(int memberCount){
        User[] orderedUsers = new User[memberCount];
        userList.toArray(orderedUsers);
        for (int i = 0; i < memberCount - 1; i++){
            for (int j = i + 1; j < memberCount; j++){
                if (getTotalKcal(orderedUsers[i]) < getTotalKcal(orderedUsers[j])){
                    User tempUser = orderedUsers[i];
                    orderedUsers[i] = orderedUsers[j];
                    orderedUsers[j] = tempUser;
                }
            }
        }
        for (int i = 0; i < memberCount && i < headerImageViews.length; i++){
            headerImageViews[i].setImageBitmap(bitmapMap.get(orderedUsers[i].getObjectId()));
            if (bitmapMap.get(orderedUsers[i].getObjectId()) == null){
                headerImageViews[i].setImageResource(R.mipmap.ic_shangchuangtupiang);
            }
        }
    }

    private int getTotalKcal(User user){
        return user.getWalkKcal() + user.getRunKcal() + user.getRideKcal();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.goBack:
                finish();
                break;
            case R.id.menuButton:
                if (flag){
                    menuPopupWindow.showAsDropDown(menuButton, -290, 20);
                    flag = false;
                }else {
                    menuPopupWindow.dismiss();
                }
                break;
            case R.id.goGroupChat:
                //群聊
                break;
            case R.id.goGroupChatLayout:
                //群聊
                break;
            case R.id.seePeopleOfGroup:
                Intent intent = new Intent(GroupDetailActivity.this, PeopleOfGroupActivity.class);
                intent.putExtra("groupObjectId", groupObjectId);
                startActivity(intent);
                break;
            case R.id.seePeopleOfGroupLayout:
                Intent intent2 = new Intent(GroupDetailActivity.this, PeopleOfGroupActivity.class);
                intent2.putExtra("groupObjectId", groupObjectId);
                startActivity(intent2);
                break;
            case R.id.groupImage:
                if(MyApplication.applicationMap.get("userObjectId").equals(group.getCreatorId())){
                    options();
                }
                break;

        }
    }

    private void exitGroup(String groupObjectId){
        new BmobQuery<Group>().setSQL("select * from Group where objectId=?")
                .setPreparedParams(new String[]{groupObjectId}).doSQLQuery(new SQLQueryListener<Group>() {
            @Override
            public void done(BmobQueryResult<Group> bmobQueryResult, BmobException e) {
                if (e == null){
                    Group group = bmobQueryResult.getResults().get(0);
                    List<String> memberObjectIdList = group.getMemberObjectIdList();
                    memberObjectIdList.remove(memberObjectIdList.indexOf(MyApplication.applicationMap.get("userObjectId")));
                    group.setMemberObjectIdList(memberObjectIdList);
                    group.setMemberCount(memberObjectIdList.size());
                    group.update();
                    finish();
                    Toast.makeText(GroupDetailActivity.this, "Exiting group successfully", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(GroupDetailActivity.this, "Exiting group failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void disbandGroup(String objectId){
        new BmobQuery<Group>().setSQL("select * from Group where objectId=?")
                .setPreparedParams(new String[]{objectId}).doSQLQuery(new SQLQueryListener<Group>() {
            @Override
            public void done(BmobQueryResult<Group> bmobQueryResult, BmobException e) {
                Group group = bmobQueryResult.getResults().get(0);
                group.delete(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null){
                            Toast.makeText(GroupDetailActivity.this, "Disbanding group successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            Toast.makeText(GroupDetailActivity.this, "Disbanding group failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void joinGroup(String groupObjectId){
        new BmobQuery<Group>().setSQL("select * from Group where objectId=?")
                .setPreparedParams(new String[]{groupObjectId}).doSQLQuery(new SQLQueryListener<Group>() {
            @Override
            public void done(BmobQueryResult<Group> bmobQueryResult, BmobException e) {
                if (e == null){
                    Group group = bmobQueryResult.getResults().get(0);
                    List<String> list = group.getMemberObjectIdList();
                    list.add(MyApplication.applicationMap.get("userObjectId"));
                    group.setMemberObjectIdList(list);
                    group.setMemberCount(list.size());
                    group.update();
                    finish();
                    Toast.makeText(GroupDetailActivity.this, "Joined group successfully", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(GroupDetailActivity.this, "Joining group failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initPopupMenuWindow(final String creatorId, final String objectId, final List<String> memberObjectIdList){
        int[] menuIons = new int[]{R.mipmap.ic_tuichu, R.mipmap.ic_fenxian};
        String[] menuNames = new String[]{"Exit group", "invite friends"};
        if (MyApplication.applicationMap.get("userObjectId").equals(creatorId)){
            menuNames[0] = "Disbanded group";
        } else {
            boolean hasJoined = false;
            for (String memberObjectId: memberObjectIdList){
                if (MyApplication.applicationMap.get("userObjectId").equals(memberObjectId)){
                    hasJoined = true;
                    break;
                }
            }
            if (! hasJoined){
                menuButton.setVisibility(View.GONE);
                joinGroup.setVisibility(View.VISIBLE);
                joinGroup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        joinGroup(groupObjectId);
                    }
                });
            }
        }

        final String[] from = new String[]{"menuIcon", "menuName"};
        int[] to = new int[]{R.id.item_image, R.id.item_text};
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < menuNames.length; i++){
            Map<String, Object> map = new HashMap<>();
            map.put(from[0], menuIons[i]);
            map.put(from[1], menuNames[i]);
            list.add(map);
        }
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(GroupDetailActivity.this).inflate(R.layout.menu_view, null);
        ListView listView = (ListView) linearLayout.findViewById(R.id.menuListView);
        SimpleAdapter adapter = new SimpleAdapter(GroupDetailActivity.this, list, R.layout.item_menu, from, to);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String menuName = ((TextView) view.findViewById(R.id.item_text)).getText().toString();
                switch (menuName){
                    case "Exit group":
                        menuPopupWindow.dismiss();
                        new AlertDialog.Builder(GroupDetailActivity.this)
                                .setTitle("Exit group").setMessage("Are you sure you want to exit the group?")
                                .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        exitGroup(groupObjectId);
                                    }
                                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}
                        }).show();
                        break;
                    case "Disbanded group":
                        menuPopupWindow.dismiss();
                        new AlertDialog.Builder(GroupDetailActivity.this)
                                .setTitle("Disband group").setMessage("Are you sure you want to disband the group?")
                                .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        disbandGroup(objectId);
                                    }
                                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}
                        }).show();
                        break;
                    case "invite friends":
                        menuPopupWindow.dismiss();
                        startActivity(new Intent(GroupDetailActivity.this,
                                InviteFriendToGroupActivity.class).putExtra("groupObjectId", groupObjectId));
                        break;
                }
            }
        });
        menuPopupWindow = new PopupWindow(linearLayout,
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        menuPopupWindow.setOutsideTouchable(true);
        menuPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(200);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        flag = true;
                    }
                }).start();
            }
        });
    }

    private static final String TAG = CreateGroupActivity.class.getSimpleName();
    private String path;
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
                    picture = new File(GroupDetailActivity.this.getFilesDir() + PhotoUtils.imageName);
                }
            }
            path = PhotoUtils.getPath(this);
            if (TextUtils.isEmpty(path)) {
                Log.e(TAG, "随机生成的用于存放剪辑后的图片的地址失败");
                return;
            }
            Uri imageUri = Uri.fromFile(new File(path));
            PhotoUtils.startPhotoZoom(GroupDetailActivity.this, Uri.fromFile(picture), PhotoUtils.PICTURE_HEIGHT, PhotoUtils.PICTURE_WIDTH, imageUri);
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
            PhotoUtils.startPhotoZoom(GroupDetailActivity.this, data.getData(), PhotoUtils.PICTURE_HEIGHT, PhotoUtils.PICTURE_WIDTH, imageUri);
        }
        if (requestCode == PhotoUtils.PHOTORESOULT) {
            Bitmap bitmap = PhotoUtils.convertToBitmap(path, PhotoUtils.PICTURE_HEIGHT, PhotoUtils.PICTURE_WIDTH);
            if(bitmap != null){
                groupImage.setImageBitmap(bitmap);
                final BmobFile bmobFile = new BmobFile(new File(path));
                bmobFile.uploadblock(new UploadFileListener() {
                    @Override
                    public void done(BmobException e) {
                        group.setGroupImageUri(bmobFile.getUrl());
                        group.update(groupObjectId, new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null){
                                    Toast.makeText(GroupDetailActivity.this, "uploaded image successfully", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void options(){
        new AlertDialog.Builder(this).setTitle("请选择图片来源").setCancelable(true)
                .setItems(new String[]{"拍照", "相册"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0){
                            try {
                                PhotoUtils.photograph(GroupDetailActivity.this);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }else {
                            PhotoUtils.selectPictureFromAlbum(GroupDetailActivity.this);
                        }
                    }
                }).show();
    }

}
