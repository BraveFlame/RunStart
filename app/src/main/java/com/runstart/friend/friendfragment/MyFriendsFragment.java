package com.runstart.friend.friendfragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.runstart.BmobBean.Friend;
import com.runstart.BmobBean.User;
import com.runstart.R;
import com.runstart.friend.adapter.ListViewForScrollView;
import com.runstart.friend.adapter.MySimpleAdapter;
import com.runstart.friend.adapter.MyUtils;
import com.runstart.friend.friendactivity.FriendsDetailsActivity;
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

public class MyFriendsFragment extends Fragment implements MySimpleAdapter.Callback {
    private View view;
    private ListViewForScrollView listView;
    private ImageView imageForNo1;
    private RadioGroup mRadioGroup;
    private RadioButton[] mRadioButtons = new RadioButton[4];

    private List<Friend> friends = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private Map<String, Bitmap> bitmapMap = new ArrayMap<>();

    private Map<String, Bitmap> forDetailsBitmap = new ArrayMap<>();
    private User[] orderedUserArr;
    private Friend[] orderedFriendArr;

    private final int FINISH_LOADING_FRIEND = 0x111;
    private final int FINISH_LOADING_BITMAP = 0x112;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == FINISH_LOADING_FRIEND){
                init();
                getUser();
            }
            if (msg.what == FINISH_LOADING_BITMAP){
                showResult("walkDistance+runDistance+rideDistance", 0);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_friends, container, false);
        listView = (ListViewForScrollView)view.findViewById(R.id.friendsListView);
        imageForNo1 = (ImageView)view.findViewById(R.id.imageForNo1);
        mRadioGroup = (RadioGroup)view.findViewById(R.id.buttonRadioGroup);
        mRadioButtons = new RadioButton[]{(RadioButton) view.findViewById(R.id.all), (RadioButton) view.findViewById(R.id.walk),
                (RadioButton) view.findViewById(R.id.run), (RadioButton) view.findViewById(R.id.ride)};

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

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
                    Toast.makeText(getActivity(), "load friends failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void init(){
        if (friends.size() == 0){
            ((ScrollView)view).removeAllViews();
            ((ScrollView)view).addView(LayoutInflater.from(getActivity()).inflate(R.layout.zero_friend, null));
            return;
        }
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.all:
                        showResult("walkDistance+runDistance+rideDistance", 0);
                        break;
                    case R.id.walk:
                        showResult("walkDistance", 1);
                        break;
                    case R.id.run:
                        showResult("runDistance", 2);
                        break;
                    case R.id.ride:
                        showResult("rideDistance", 3);
                        break;
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), FriendsDetailsActivity.class);
                int index = Integer.parseInt(((TextView)view.findViewById(R.id.rankings)).getText().toString()) - 1;
                Bundle data = new Bundle();
                data.putSerializable("friend", orderedFriendArr[index]);
                data.putSerializable("user", orderedUserArr[index]);
                ArrayList<Bitmap> bitmapList = new ArrayList<>();
                bitmapList.add(bitmapMap.get(orderedUserArr[index].getObjectId()));
                data.putSerializable("headerImage", bitmapList);
                intent.putExtras(data);
                MyFriendsFragment.this.startActivity(intent);
            }
        });
    }

    private void getUser(){
        if (friends.size() == 0){
            return;
        }
        for (int i = 0; i < friends.size(); i++){
            String sql = "select * from User where objectId=?";
            new BmobQuery<User>().setSQL(sql).setPreparedParams(new String[]{friends.get(i).getFriendObjectId()})
                    .doSQLQuery(new SQLQueryListener<User>() {
                        @Override
                        public void done(BmobQueryResult<User> bmobQueryResult, BmobException e) {
                            if (e == null){
                                synchronized (GroupFragment.class){
                                    User user = bmobQueryResult.getResults().get(0);
                                    users.add(user);
                                    getBitmap(user);
                                }
                            }else {
                                Toast.makeText(getActivity(), "load friends failed", Toast.LENGTH_SHORT).show();
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

    private void showResult(String sportCategory, int radioButtonIndex){

        if (users.size() == 0){
            return;
        }

        String[] mItemCols = new String[]{"rankings", "headerImage", "nickName", "sportDistance", "likeImage", "likeNumberForHistory"};
        int[] mItemIds = new int[]{R.id.rankings, R.id.headerImage, R.id.nickName, R.id.sportLength, R.id.likeImage, R.id.likeNumber};

        //运动数据显示控制按钮
        for (RadioButton radioButton:mRadioButtons){
            radioButton.setTextColor(0xff878787);
        }
        mRadioButtons[radioButtonIndex].setTextColor(0xff21187f);
        mRadioButtons[radioButtonIndex].setChecked(true);

        MySimpleAdapter adapter = new MySimpleAdapter(getActivity(),
                orderBySportCategory(sportCategory), R.layout.item_friend, mItemCols, mItemIds, this);
        listView.setAdapter(adapter);
    }

    private ArrayList<Map<String, Object>> orderBySportCategory(String sportCategory){
        User[] orderedUsers = getOrderedUsers(sportCategory);
        orderedUserArr = orderedUsers;

        Friend[] orderedFriends = new Friend[friends.size()];
        orderedFriendArr = orderedFriends;

        for (int i = 0; i < friends.size(); i++){
            User user = orderedUsers[i];
            for (int j = 0; j < friends.size(); j++){
                String userObjectId = user.getObjectId();
                String friendObjectId = friends.get(j).getFriendObjectId();
                if (userObjectId.equals(friendObjectId)){
                    orderedFriends[i] = friends.get(j);
                }
            }
        }

        ArrayList<Map<String, Object>> mapList = new ArrayList<>();
        for (int i = 0; i < friends.size(); i++){
            User user = orderedUsers[i];
            Friend friend = orderedFriends[i];
            Map<String, Object> map = new HashMap<>();
            map.put("rankings", i + 1);
            map.put("headerImage", bitmapMap.get(user.getObjectId()));
            forDetailsBitmap.put(user.getObjectId(), bitmapMap.get(user.getObjectId()));
            map.put("nickName", user.getNickName());

            map.put("sportDistance", getSportDistance(user, sportCategory) + " m");

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
            if (i == 0){
                //imageForNo1.setImageBitmap(bitmapMap.get(user.getObjectId()));
            }
            map.put("likeNumberForHistory", user.getLikeNumberForHistory());
            mapList.add(map);
        }
        return mapList;
    }

    private User[] getOrderedUsers(String sportCategory){
        User[] orderedUsers = new User[users.size()];
        users.toArray(orderedUsers);
        for (int i = 0; i < users.size() - 1; i++){
            for (int j = i + 1; j < users.size(); j++){
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

    //点赞
    @Override
    public void click(View view) {
        ImageView likeImage = (ImageView)view;
        TextView rankingText = (TextView) ((LinearLayout)view.getParent()).findViewById(R.id.rankings);
        int index = Integer.parseInt(rankingText.getText().toString());
        TextView likeNumberText = (TextView) ((LinearLayout)view.getParent()).findViewById(R.id.likeNumber);

        Friend friend = orderedFriendArr[index];
        User user = orderedUserArr[index];

        MyUtils.like(likeImage, friend, user, getActivity(), likeNumberText);
    }

    public static String getStringToday() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }
}
