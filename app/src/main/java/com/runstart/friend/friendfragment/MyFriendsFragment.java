package com.runstart.friend.friendfragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
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


import com.runstart.bean.Friend;
import com.runstart.bean.User;
import com.runstart.R;
import com.runstart.friend.adapter.ListViewForScrollView;
import com.runstart.friend.adapter.MySimpleAdapter;
import com.runstart.friend.adapter.MyUtils;
import com.runstart.friend.friend_activity.FriendsDetailsActivity;
import com.runstart.MyApplication;

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

public class MyFriendsFragment extends Fragment implements MySimpleAdapter.Callback {
    private View view;
    private ListViewForScrollView listView;
    private ImageView imageForNo1;
    private RadioGroup mRadioGroup;
    private RadioButton[] mRadioButtons = new RadioButton[4];

    private List<Friend> friendList = new ArrayList<>();
    private Map<String, Friend> friendMap = new HashMap<>();
    private List<User> userList = new ArrayList<>();
    private Map<String, Bitmap> bitmapMap = new ArrayMap<>();

    private Map<String, Bitmap> forDetailsBitmap = new ArrayMap<>();
    private User[] orderedUserArr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyUtils.showProgressDialog(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();

        queryFriend();
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init(){
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
                data.putSerializable("friend", friendMap.get(orderedUserArr[index].getObjectId()));
                data.putSerializable("user", orderedUserArr[index]);
                intent.putExtras(data);
                MyFriendsFragment.this.startActivity(intent);
            }
        });
    }

    private void queryFriend(){
        userList.clear();
        bitmapMap.clear();
        forDetailsBitmap.clear();
        String sql_friend = "select * from Friend where userObjectId=? and isFriend=1";
        BmobQuery<Friend> query = new BmobQuery<>();
        query.setSQL(sql_friend);
        query.setPreparedParams(new String[]{MyApplication.applicationMap.get("userObjectId")});
        query.doSQLQuery(new SQLQueryListener<Friend>() {
            @Override
            public void done(BmobQueryResult<Friend> bmobQueryResult, BmobException e) {
                if (e == null){
                    friendList = bmobQueryResult.getResults();

                    //设置无好友时界面
                    if (friendList.size() == 0){
                        MyUtils.dismissProgressDialog(getActivity());
                        ((ScrollView)view).removeAllViews();
                        ((ScrollView)view).addView(LayoutInflater.from(getActivity()).inflate(R.layout.zero_friend, null));
                    }

                    //获取好友详细详细
                    for (Friend friend:friendList){
                        friendMap.put(friend.getFriendObjectId(), friend);
                        queryUser(friend);
                    }
                }else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void queryUser(Friend friend){
        new BmobQuery<User>().setSQL("select * from User where objectId=?").setPreparedParams(new String[]{friend.getFriendObjectId()})
            .doSQLQuery(new SQLQueryListener<User>() {
                @Override
                public void done(BmobQueryResult<User> bmobQueryResult, BmobException e) {
                    if (e == null){
                        synchronized (MyFriendsFragment.class){
                            if (bmobQueryResult.getResults().size() == 1){
                                User user = bmobQueryResult.getResults().get(0);
                                userList.add(user);
                                queryBitmap(user);
                                MyUtils.dismissProgressDialog(getActivity());
                            }
                        }
                    }else {
                        e.printStackTrace();
                    }
                }
            });
    }

    private void queryBitmap(final User user){
        final int objectIdLength = user.getObjectId().length();
        String saveName = MyUtils.getStringToday() + user.getObjectId() + ".png";
        String headerImageUri = user.getHeaderImageUri();
        File saveFile = new File(Environment.getExternalStorageDirectory() + File.separator + "lovesportimage", saveName);
        if (headerImageUri == null || headerImageUri.length() == 0){
            synchronized (Bitmap.class){
                bitmapMap.put(saveFile.toString().substring(saveFile.toString().length() - objectIdLength - 4, saveFile.toString().length() - 4), null);
                showResult("walkDistance+runDistance+rideDistance", 0);
                return;
            }
        }
        new BmobFile(saveName, "", headerImageUri).download(saveFile, new DownloadFileListener() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null){
                    synchronized (Bitmap.class){
                        bitmapMap.put(s.substring(s.length() - objectIdLength - 4, s.length() - 4), BitmapFactory.decodeFile(s));
                        showResult("walkDistance+runDistance+rideDistance", 0);
                    }
                }else {
                    e.printStackTrace();                }
            }
            @Override
            public void onProgress(Integer integer, long l) {}
        });
    }

    private void showResult(String sportCategory, int radioButtonIndex){
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

        ArrayList<Map<String, Object>> mapList = new ArrayList<>();
        for (int i = 0; i < userList.size(); i++){
            User user = orderedUsers[i];
            Friend friend = friendMap.get(user.getObjectId());
            Map<String, Object> map = new HashMap<>();
            map.put("rankings", i + 1);
            map.put("headerImage", bitmapMap.get(user.getObjectId()));
            forDetailsBitmap.put(user.getObjectId(), bitmapMap.get(user.getObjectId()));
            map.put("nickName", user.getNickName());
            map.put("sportDistance", getSportDistance(user, sportCategory) + " m");

            if (friend == null || friend.getLikeDate() == null || friend.getLikeDate().length() == 0) {
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
                imageForNo1.setImageBitmap(bitmapMap.get(user.getObjectId()));
            }
            map.put("likeNumberForHistory", user.getLikeNumberForHistory());
            if (user.getLikeNumberForHistory() > 99){
                map.put("likeNumberForHistory", "99+");
            }
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

    //点赞
    @Override
    public void click(View view) {
        ImageView likeImage = (ImageView)view;
        TextView rankingText = (TextView) ((LinearLayout)view.getParent()).findViewById(R.id.rankings);
        int index = Integer.parseInt(rankingText.getText().toString()) - 1;
        TextView likeNumberText = (TextView) ((LinearLayout)view.getParent()).findViewById(R.id.likeNumber);

        User user = orderedUserArr[index];
        user.setObjectId(orderedUserArr[index].getObjectId());
        Friend friend = friendMap.get(user.getObjectId());
        friend.setObjectId(friend.getObjectId());

        MyUtils.like(0, likeImage, friend, user, getActivity(), likeNumberText);
    }
}
