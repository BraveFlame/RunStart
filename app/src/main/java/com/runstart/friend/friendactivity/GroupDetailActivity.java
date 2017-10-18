package com.runstart.friend.friendactivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.runstart.BmobBean.ActivityAndMember;
import com.runstart.BmobBean.ActivityData;
import com.runstart.BmobBean.Group;
import com.runstart.BmobBean.User;
import com.runstart.R;
import com.runstart.bean.ActivityTopicForCircle;
import com.runstart.circle.CircleJoinActivity;
import com.runstart.circle.GetFromBmob;
import com.runstart.friend.adapter.ListViewForScrollView;
import com.runstart.friend.adapter.MyUtils;
import com.runstart.friend.adapter.PhotoUtils;
import com.runstart.MyApplication;
import com.runstart.middle.ListViewAdapterForCircle;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
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

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        progressDialog = new ProgressDialog(GroupDetailActivity.this);

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

        MyUtils.showProgressDialog(this);
        queryGroupImage();

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
        //////////////////////////////////////获取activity信息/////////////////////////////////////////////
        GetFromBmob.getADbyFriendsIds(memberObjectIdList,handler);
        ll_friend_detail_no_activity=(LinearLayout)findViewById(R.id.ll_group_detail_no_activity);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void queryGroupImage(){
        new BmobQuery<Group>().setSQL("select * from Group where objectId=?").setPreparedParams(new String[]{groupObjectId})
                .doSQLQuery(new SQLQueryListener<Group>() {
                    @Override
                    public void done(BmobQueryResult<Group> bmobQueryResult, BmobException e) {
                        if (e == null){
                            if (bmobQueryResult.getResults().size() > 0){
                                String imageUri = bmobQueryResult.getResults().get(0).getGroupImageUri();
                                if (imageUri == null || imageUri.length() == 0){
                                    groupImage.setImageResource(R.mipmap.ic_shangchuangtupiang);
                                } else {
                                    String saveName = group.getObjectId() + ".png";
                                    File saveFile = new File(Environment.getExternalStorageDirectory() + File.separator + "lovesportimage", saveName);
                                    new BmobFile(saveName, "", imageUri).download(saveFile, new DownloadFileListener() {
                                        @Override
                                        public void done(String s, BmobException e) {
                                            if (e == null){
                                                groupImage.setImageBitmap(BitmapFactory.decodeFile(s));
                                            } else {
                                                e.printStackTrace();
                                            }
                                        }
                                        @Override
                                        public void onProgress(Integer integer, long l) {}
                                    });
                                }

                            }
                        }
                    }
                });
    }

    private void queryBitmap(User user, final int memberCount){
        final int objectIdLength = user.getObjectId().length();
        String saveName = MyUtils.getStringToday() + user.getObjectId() + ".png";
        String imageUri = user.getHeaderImageUri();
        File saveFile = new File(Environment.getExternalStorageDirectory() + File.separator + "lovesportimage", saveName);
        if (imageUri == null || imageUri.length() == 0){
            bitmapMap.put(saveFile.toString().substring(saveFile.toString().length() - objectIdLength - 4, saveFile.toString().length() - 4), null);
            if (bitmapMap.size() == memberCount){
                MyUtils.dismissProgressDialog(GroupDetailActivity.this);
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
                        MyUtils.dismissProgressDialog(GroupDetailActivity.this);
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
                    menuPopupWindow.showAsDropDown(menuButton, -410, 30);
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

    ////////////////////////////添加activity////////////////////////////////////////////////////////
    List<ActivityData> alAD;
    ArrayList<ActivityData> friendReleaseActivities;
    ArrayList<Map<String,Object>> ListToShow2;
    LinearLayout ll_friend_detail_no_activity;
    final static int oneBox=1;
    final static int twoBox=2;

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
                    Log.e("database","5");
                    str=bundle.getString("MTAlist");
                    List<ActivityAndMember> ps = gson.fromJson(str, new TypeToken<List<ActivityAndMember>>(){}.getType());
                    //外卖菜名
                    ArrayList<String> al=new ArrayList<>();
                    for (ActivityAndMember gameScore : ps) {
                        al.add(gameScore.getActivityId());
                    }
                    //根据菜名叫外卖，到了的外卖放到一号邮箱
                    GetFromBmob.getActivitysByActivityIds(oneBox,al,handler);
                    break;
                //上面的过程执行后，获得一系列的Activity，以为没事了吗，还要获取创建者的相关信息
                //1号邮箱拿了外卖，该去拿餐具了，取了一个箱子，分格子，分别标上那份外卖的厨师名，重复的覆盖
                case 6:
                    Log.e("database","6");
                    str=bundle.getString("alAD1");
                    alAD=gson.fromJson(str,new TypeToken<List<ActivityData>>(){}.getType());
                    Map<String,User> creatorMap=new HashMap<>();
                    for(int i=0;i<alAD.size();i++){
                        creatorId=alAD.get(i).getCreatorId();
                        creatorMap.put(creatorId,new User());
                    }
                    //叫外卖小哥送餐具过来
                    GetFromBmob.getUsersByUserIds(oneBox,creatorMap,handler);
                    break;
                case 10:
                    Log.e("database","10");
                    //打开2号邮箱餐具包装
                    str=bundle.getString("mapUser2");
                    //到这里，终于把 外卖和对应的餐具，放到了一起，并放到桌子上。
                    ListToShow2=putTheTable(str,friendReleaseActivities);
                    useAdapter2(ListToShow2);
                    ((MyApplication)getApplicationContext()).stopProgressDialog();
                    break;
                //getADbyFriendsIds调用后（通过好友的IDs获取他们创建的活动s），会来到
                case 9:
                    Log.e("database","9");
                    str=bundle.getString("friendReleaseActivities");
                    friendReleaseActivities=gson.fromJson(str,new TypeToken<List<ActivityData>>(){}.getType());
                    //取餐具
                    Map<String,User> friendCreatorMap=new HashMap<>();
                    Log.e("database","size:"+friendReleaseActivities.size());
                    if(friendReleaseActivities.size()==0){
                        useAdapter2(new ArrayList());
                        ((MyApplication)getApplicationContext()).stopProgressDialog();
                    } else {
                        for (int i = 0; i < friendReleaseActivities.size(); i++) {
                            creatorId = friendReleaseActivities.get(i).getCreatorId();
                            friendCreatorMap.put(creatorId, new User());
                            Log.e("database", "creatorMap:" + friendCreatorMap);
                        }
                        //叫外卖小哥送餐具过来2号邮箱
                        GetFromBmob.getUsersByUserIds(twoBox, friendCreatorMap, handler);
                    }
                    break;
                default:break;
            }
        }
    };

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
            Log.e("database","创建者头像："+creator.getHeaderImageUri());
            activityTopicForCircle.setUserName(creator.getNickName());
            activityTopicForCircle.setTopicProgressbar(String.valueOf(R.mipmap.progressbar));
            topicList.add(activityTopicForCircle);
        }
        return topicList;
    }

    public void useAdapter2(List list) {
        if(list.size()!=0) {
            activityListView.setVisibility(View.VISIBLE);
            ll_friend_detail_no_activity.setVisibility(View.GONE);
            final List<ActivityTopicForCircle> topicList = getActivityDataTopicData(list);
            Collections.sort(topicList);
            ListViewAdapterForCircle listViewAdapterForCircle = new ListViewAdapterForCircle(this);
            listViewAdapterForCircle.setTopicList(topicList);
            activityListView.setAdapter(listViewAdapterForCircle);
            activityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String ADid = topicList.get(position).getADid();
                    CircleJoinActivity.jump(ADid, GroupDetailActivity.this);
                }
            });
        } else {
            activityListView.setVisibility(View.GONE);
            ll_friend_detail_no_activity.setVisibility(View.VISIBLE);
        }
    }
}
