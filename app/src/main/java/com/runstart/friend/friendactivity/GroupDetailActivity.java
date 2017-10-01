package com.runstart.friend.friendactivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
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
import com.runstart.history.MyApplication;

import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

public class GroupDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout goGroupChatLayout, seePeopleOfGroupLayout;
    private TextView titleGroupName, groupName, memberCount, distance, groupDetail;
    private Button goBack, menuButton, goGroupChat, seePeopleOfGroup, joinGroup;
    private MyHeaderImageView groupImage, latestTalkerImage, imageByKcal0, imageByKcal1,
            imageByKcal2, imageByKcal3, imageByKcal4, imageByKcal5;
    private MyHeaderImageView[] headerImageViews;
    private ListViewForScrollView activityListView;

    private PopupWindow menuPopupWindow;
    private boolean flag = true;

    private Map<String, Bitmap> bitmapMap = new ArrayMap<>();
    private List<User> userList = new ArrayList<>();
    private String groupObjectId = "";


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
        joinGroup = (Button)findViewById(R.id.joinGroup);
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

        Map<String, Object> groupMap = ((ArrayList<Map<String, Object>>) getIntent().getSerializableExtra("groupDetail")).get(0);

        final List<String> memberObjectIdList = (List<String>) groupMap.get("memberObjectIdList");
        groupObjectId = groupMap.get("objectId").toString();
        initPopupMenuWindow(groupMap.get("creatorId").toString(), groupMap.get("objectId").toString(), memberObjectIdList);

        titleGroupName.setText(groupMap.get("groupName").toString());
        groupName.setText(groupMap.get("groupName").toString());
        memberCount.setText(groupMap.get("memberCount").toString());
        distance.setText(groupMap.get("distance").toString());
        groupDetail.setText(groupMap.get("groupDetail").toString());
        groupImage.setImageBitmap((Bitmap) groupMap.get("groupImage"));

        for (final String memberObjectId: memberObjectIdList){
           new BmobQuery<User>().setSQL("select * from User where objectId=?")
                   .setPreparedParams(new String[]{memberObjectId}).doSQLQuery(new SQLQueryListener<User>() {
               @Override
               public void done(BmobQueryResult<User> bmobQueryResult, BmobException e) {
                   if (e == null){
                       synchronized (GroupDetailActivity.class){
                           User user = bmobQueryResult.getResults().get(0);
                           userList.add(user);
                           getBitmap(user, memberObjectIdList.size());
                       }
                   }else {
                       Toast.makeText(GroupDetailActivity.this, "load group member images failed", Toast.LENGTH_SHORT).show();
                   }
               }
           });
        }
    }

    private void getBitmap(User user, final int memberCount){
        final int objectIdLength = user.getObjectId().length();
        new BmobFile(getStringToday() + user.getObjectId() + ".png", "", user.getHeaderImageUri())
                .download(new File(Environment.getExternalStorageDirectory() + File.separator + "lovesportimage",
                        getStringToday() + user.getObjectId() + ".png"), new DownloadFileListener() {
                    @Override
                    public void done(String s, BmobException e) {
                        bitmapMap.put(s.substring(s.length() - objectIdLength - 4, s.length() - 4), BitmapFactory.decodeFile(s));
                        if (bitmapMap.size() == memberCount){
                            showOrderedImagesByKcal(memberCount);
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
        }
    }

    private int getTotalKcal(User user){
        return user.getWalkKcal() + user.getRunKcal() + user.getRideKcal();
    }

    private String getStringToday() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.goBack:
                finish();
                break;
            case R.id.menuButton:
                if (flag){
                    menuPopupWindow.showAsDropDown(menuButton, 0, 30);
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

    private void joinTheGroup(String groupObjectId){
        new BmobQuery<Group>().setSQL("select * from Group where objectId=?")
                .setPreparedParams(new String[]{groupObjectId}).doSQLQuery(new SQLQueryListener<Group>() {
            @Override
            public void done(BmobQueryResult<Group> bmobQueryResult, BmobException e) {
                if (e == null){
                    Group group = bmobQueryResult.getResults().get(0);
                    List<String> list = group.getMemberObjectIdList();
                    list.add(MyApplication.applicationMap.get("userObjectId"));
                    group.setMemberObjectIdList(list);
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
                        joinTheGroup(groupObjectId);
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
}
