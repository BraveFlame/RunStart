package com.runstart.friend.friendactivity;

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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import com.runstart.BmobBean.Friend;
import com.runstart.BmobBean.Group;
import com.runstart.BmobBean.User;
import com.runstart.R;
import com.runstart.friend.adapter.ListViewForScrollView;
import com.runstart.friend.adapter.MySimpleAdapter;
import com.runstart.friend.adapter.MyUtils;
import com.runstart.friend.friendfragment.GroupFragment;
import com.runstart.history.MyApplication;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

public class PeopleOfGroupActivity extends AppCompatActivity implements MySimpleAdapter.Callback {

    private Button goBack;
    private ListViewForScrollView peopleOfGroupListView;

    private List<User> userList = new ArrayList<>();
    private Map<String, Bitmap> bitmapMap = new ArrayMap<>();
    private Map<String, Friend> friendMap = new ArrayMap<>();
    private Map<String, Bitmap> forDetailsBitmap = new ArrayMap<>();
    private User[] orderedUserArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_of_group);

        goBack = (Button)findViewById(R.id.goBack);
        peopleOfGroupListView = (ListViewForScrollView)findViewById(R.id.peopleOfGroupListView);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String groupObjectId = getIntent().getStringExtra("groupObjectId");
        new BmobQuery<Group>().setSQL("select * from Group where objectId=?").setPreparedParams(new String[]{groupObjectId})
                .doSQLQuery(new SQLQueryListener<Group>() {
                    @Override
                    public void done(BmobQueryResult<Group> bmobQueryResult, BmobException e) {
                        if (e == null){
                            List<String> memberObjectIdList = bmobQueryResult.getResults().get(0).getMemberObjectIdList();
                            getUser(memberObjectIdList);
                        } else {
                            Toast.makeText(PeopleOfGroupActivity.this, "load group member failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getUser(final List<String> memberObjectIdList){
        if (memberObjectIdList.size() == 0){
            return;
        }
        for (int i = 0; i < memberObjectIdList.size(); i++){
            String sql = "select * from User where objectId=?";
            new BmobQuery<User>().setSQL(sql).setPreparedParams(new String[]{memberObjectIdList.get(i)})
                    .doSQLQuery(new SQLQueryListener<User>() {
                        @Override
                        public void done(BmobQueryResult<User> bmobQueryResult, BmobException e) {
                            if (e == null){
                                synchronized (GroupFragment.class){
                                    User user = bmobQueryResult.getResults().get(0);
                                    userList.add(user);
                                    getBitmap(user, memberObjectIdList.size());
                                    getFriend(user, memberObjectIdList.size());
                                }
                            }else {
                                Toast.makeText(PeopleOfGroupActivity.this, "load group member failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void getFriend(final User user, final int memberCount){
        new BmobQuery<Friend>().setSQL("select * from Friend where userObjectId=? and friendObjectId=?")
                .setPreparedParams(new String[]{MyApplication.applicationMap.get("userObjectId"), user.getObjectId()})
                .doSQLQuery(new SQLQueryListener<Friend>() {
            @Override
            public void done(BmobQueryResult<Friend> bmobQueryResult, BmobException e) {
                if (e == null){
                    synchronized (GroupFragment.class){
                        Friend friend;
                        if (bmobQueryResult.getResults().size() == 0){
                            friend = new Friend(MyApplication.applicationMap.get("userObjectId"),
                                    user.getObjectId(), 0, "");
                            friend.save();
                        } else {
                            friend = bmobQueryResult.getResults().get(0);
                        }
                        friendMap.put(user.getObjectId(), friend);
                        if ((friendMap.size() == memberCount) && (bitmapMap.size() == memberCount)){
                            showResult();
                        }
                    }
                }else {
                    Toast.makeText(PeopleOfGroupActivity.this, "load group member failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getBitmap(User user, final int memberCount){
        final int objectIdLength = user.getObjectId().length();
        new BmobFile(getStringToday() + user.getObjectId() + ".png", "", user.getHeaderImageUri())
                .download(new File(Environment.getExternalStorageDirectory() + File.separator + "lovesportimage",
                        getStringToday() + user.getObjectId() + ".png"), new DownloadFileListener() {
                    @Override
                    public void done(String s, BmobException e) {
                        bitmapMap.put(s.substring(s.length() - objectIdLength - 4, s.length() - 4), BitmapFactory.decodeFile(s));
                        if ((friendMap.size() == memberCount) && (bitmapMap.size() == memberCount)){
                            showResult();
                        }
                    }
                    @Override
                    public void onProgress(Integer integer, long l) {
                    }
                });
    }

    private void showResult(){
        String[] mItemCols = new String[]{"rankings", "headerImage", "nickName", "sportDistance", "likeImage", "likeNumberForHistory"};
        int[] mItemIds = new int[]{R.id.rankings, R.id.headerImage, R.id.nickName, R.id.sportLength, R.id.likeImage, R.id.likeNumber};

        MySimpleAdapter adapter = new MySimpleAdapter(this, orderBySportDiance(), R.layout.item_friend, mItemCols, mItemIds, this);
        peopleOfGroupListView.setAdapter(adapter);
    }

    private ArrayList<Map<String, Object>> orderBySportDiance(){
        User[] orderedUsers = getOrderedUsers();
        orderedUserArr = orderedUsers;

        ArrayList<Map<String, Object>> mapList = new ArrayList<>();
        for (int i = 0; i < userList.size(); i++){
            User user = orderedUsers[i];
            Friend friend = friendMap.get(user.getObjectId());
            Map<String, Object> map = new HashMap<>();
            map.put("rankings", i + 1);
            map.put("headerImage", bitmapMap.get(user.getObjectId()));
            forDetailsBitmap.put(user.getObjectId(), bitmapMap.get(user.getObjectId()));
            map.put("nickName", user.getNickName());
            map.put("sportDistance", getTotalDistance(user) + " m");

            if (friend.getLikeDate() == null || friend.getLikeDate().length() == 0) {
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

    private User[] getOrderedUsers(){
        User[] orderedUsers = new User[userList.size()];
        userList.toArray(orderedUsers);
        for (int i = 0; i < userList.size() - 1; i++){
            for (int j = i + 1; j < userList.size(); j++){
                if (getTotalDistance(orderedUsers[i]) < getTotalDistance(orderedUsers[j])){
                    User tempUser = orderedUsers[i];
                    orderedUsers[i] = orderedUsers[j];
                    orderedUsers[j] = tempUser;
                }
            }
        }
        return orderedUsers;
    }

    private int getTotalDistance(User user){
        return user.getWalkDistance() + user.getRunDistance() + user.getRideDistance();
    }

    private String getStringToday() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }
    //点赞
    @Override
    public void click(View view) {
        ImageView likeImage = (ImageView)view;
        TextView rankingText = (TextView) ((LinearLayout)view.getParent()).findViewById(R.id.rankings);
        int index = Integer.parseInt(rankingText.getText().toString()) - 1;
        TextView likeNumberText = (TextView) ((LinearLayout)view.getParent()).findViewById(R.id.likeNumber);

        User user = orderedUserArr[index];
        Friend friend = friendMap.get(user.getObjectId());

        MyUtils.like(likeImage, friend, user, this, likeNumberText);
    }


}
