package com.runstart.bottom;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.runstart.BmobBean.ActivityAndMember;
import com.runstart.BmobBean.ActivityData;
import com.runstart.BmobBean.User;
import com.runstart.R;
import com.runstart.bean.ActivityTopicForCircle;
import com.runstart.circle.CircleCreateActivity;
import com.runstart.circle.CircleJoinActivity;
import com.runstart.circle.GetFromBmob;
import com.runstart.history.MyApplication;
import com.runstart.middle.ListViewAdapterForCircle;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhouj on 2017-09-08.
 */

public class CircleFragment extends BaseFragment{
    ListView firstListView;
    ListView secondListView;
    SwipeRefreshLayout mSwipeLayout;
    View view;
    MyApplication myApplication;
    private String userId;
    List<ActivityData> alAD;
    ArrayList<ActivityData> friendReleaseActivities;
    ArrayList<Map<String,Object>> listToShow1,ListToShow2;
    ArrayList<String> friendIds;
    final static String SystemRecommandedActivity[]={"EVwFAAAI","p5I2333K"};
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
                //这里获得了含多个键为userId,值为User的键值对的Map
                //餐具也取好了，为每份外卖分配一种餐具，放到桌面上
                case 7:
                    //打开1号邮箱餐具包装
                    Log.e("database","7");
                    str=bundle.getString("mapUser1");
                    //到这里，终于把 外卖和对应的餐具，放到了一起，并放到桌子上。
                    listToShow1=putTheTable(str,alAD);
                    useAdapter1(listToShow1);
                    break;
                case 10:
                    Log.e("database","10");
                    //打开2号邮箱餐具包装
                    str=bundle.getString("mapUser2");
                    //到这里，终于把 外卖和对应的餐具，放到了一起，并放到桌子上。
                    ListToShow2=putTheTable(str,friendReleaseActivities);
                    useAdapter2(ListToShow2);
                    ((MyApplication)getContext().getApplicationContext()).stopProgressDialog();
                    mSwipeLayout.setRefreshing(false);
                    break;
                //getFriendsIdsByUserId（通过自己的id获取好友们的IDs）调用后，会来到
                case 8:
                    Log.e("database","8");
                    str=bundle.getString("friendIds");
                    friendIds=gson.fromJson(str,new TypeToken<List<String>>(){}.getType());
                    GetFromBmob.getADbyFriendsIds(friendIds,handler);
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
                        ((MyApplication)getContext().getApplicationContext()).stopProgressDialog();
                        mSwipeLayout.setRefreshing(false);
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_circle, container, false);
        myApplication=(MyApplication)getActivity().getApplicationContext();
        userId=myApplication.applicationMap.get("userObjectId");
        initBasicView();
        refreshUI();
        return view;
    }

    public void refreshUI(){
        initView();
        //这个是入口函数，显示系统的那两个activity
        showSystemRecommandedActivity();
    }

    private void showSystemRecommandedActivity(){
        ArrayList<String> al=new ArrayList<>();
        al.add(SystemRecommandedActivity[0]);
        al.add(SystemRecommandedActivity[1]);
        //用1号邮箱叫2份系统外卖
        GetFromBmob.getActivitysByActivityIds(oneBox,al,handler);
        //获取利用电话本，打电话给朋友，之后一步步叫大家的外卖
        GetFromBmob.getFriendsIdsByUserId(userId,handler);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * 初始化view
     */
    private void initBasicView(){
        firstListView = (ListView) view.findViewById(R.id.circle_recommendactivity_first_listview);
        secondListView = (ListView) view.findViewById(R.id.circle_recommendactivity_second_listview);
        mSwipeLayout=(SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showSystemRecommandedActivity();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mSwipeLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });
        FloatingActionButton btn_floataction = (FloatingActionButton) view.findViewById(R.id.circle_btnFloatingAction);
        btn_floataction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createActivityIntent = new Intent(getActivity(), CircleCreateActivity.class);
                startActivity(createActivityIntent);
            }
        });
    }

    public void initView() {
        ((MyApplication)getContext().getApplicationContext()).showProgressDialog(getActivity());
    }

    /**
     * 初始化数据
     */
    public List<ActivityTopicForCircle> getActivityTopicData() {
        List<ActivityTopicForCircle> topicList = new ArrayList<>();

        ActivityTopicForCircle activityTopicForCircle = new ActivityTopicForCircle();
        activityTopicForCircle.setTopicImage(String.valueOf(R.mipmap.bitmap_walk));
        activityTopicForCircle.setTopicTitle("every day3");
        activityTopicForCircle.setUserHeadImage(String.valueOf(R.mipmap.arvin_febry_302935_copy3));
        activityTopicForCircle.setUserName("alien1");
        activityTopicForCircle.setTopicProgressbar(String.valueOf(R.mipmap.progressbar));
        topicList.add(activityTopicForCircle);

        ActivityTopicForCircle activityTopicForCircle2 = new ActivityTopicForCircle();
        activityTopicForCircle2.setTopicImage(String.valueOf(R.mipmap.bitmap_walk));
        activityTopicForCircle2.setTopicTitle("every day3");
        activityTopicForCircle2.setUserHeadImage(String.valueOf(R.mipmap.arvin_febry_302935_copy3));
        activityTopicForCircle2.setUserName("alien2");
        activityTopicForCircle2.setTopicProgressbar(String.valueOf(R.mipmap.progressbar));
        topicList.add(activityTopicForCircle2);

        ActivityTopicForCircle activityTopicForCircle3 = new ActivityTopicForCircle();
        activityTopicForCircle3.setTopicImage(String.valueOf(R.mipmap.bitmap_walk));
        activityTopicForCircle3.setTopicTitle("every day3");
        activityTopicForCircle3.setUserHeadImage(String.valueOf(R.mipmap.arvin_febry_302935_copy3));
        activityTopicForCircle3.setUserName("alien3");
        activityTopicForCircle3.setTopicProgressbar(String.valueOf(R.mipmap.progressbar));
        topicList.add(activityTopicForCircle3);

        return topicList;
    }

    //这里返回真正的网上获取的数据，而不是示例数据
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

    /**
     * 使用ListViewAdapter
     */
    public void useAdapter1(List list) {
        final List<ActivityTopicForCircle> topicList = getActivityDataTopicData(list);
        Collections.sort(topicList);
        for (int i=0;i<topicList.size();i++){
            Log.e("database",topicList.get(i).getUserName());
            Log.e("database",topicList.get(i).getTopicTitle());
            Log.e("database",topicList.get(i).getTopicImage());
        }
        ListViewAdapterForCircle listViewAdapterForCircle = new ListViewAdapterForCircle(getContext());
        listViewAdapterForCircle.setTopicList(topicList);
        firstListView.setAdapter(listViewAdapterForCircle);
        firstListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String ADid=topicList.get(position).getADid();
                CircleJoinActivity.jump(ADid,getActivity());
            }
        });
    }
    public void useAdapter2(List list) {
        final List<ActivityTopicForCircle> topicList = getActivityDataTopicData(list);
        Collections.sort(topicList);
        ListViewAdapterForCircle listViewAdapterForCircle = new ListViewAdapterForCircle(getContext());
        listViewAdapterForCircle.setTopicList(topicList);
        secondListView.setAdapter(listViewAdapterForCircle);
        secondListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String ADid=topicList.get(position).getADid();
                CircleJoinActivity.jump(ADid,getActivity());
            }
        });
    }

    /**
     * 创建ListView的OnItemListener事件监听
     */
    public void setOnItemListener1(){
        firstListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ActivityData activityData=(ActivityData) listToShow1.get(position).get("AD");
                CircleJoinActivity.jump(activityData.getObjectId(),getActivity());
            }
        });
    }
    public void setOnItemListener2(){
        secondListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ActivityData activityData=(ActivityData) ListToShow2.get(position).get("AD");
                CircleJoinActivity.jump(activityData.getObjectId(),getActivity());
            }
        });
    }
}
