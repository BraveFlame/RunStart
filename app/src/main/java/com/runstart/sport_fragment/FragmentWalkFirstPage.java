package com.runstart.sport_fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.runstart.BmobBean.ActivityAndMember;
import com.runstart.BmobBean.ActivityData;
import com.runstart.BmobBean.User;
import com.runstart.R;
import com.runstart.bean.ActivityTopic;
import com.runstart.bean.ActivityTopicForCircle;
import com.runstart.circle.CirclePushCardActivity;
import com.runstart.circle.GetFromBmob;
import com.runstart.friend.adapter.ListViewForScrollView;
import com.runstart.help.CountDown;
import com.runstart.help.GetMap;
import com.runstart.help.ToastShow;
import com.runstart.history.HistoryChartActivity;
import com.runstart.history.MyApplication;
import com.runstart.middle.ListViewAdapter;
import com.runstart.middle.ListViewAdapterForCircle;
import com.runstart.sport_map.SportingActivity;
import com.runstart.view.LinearCircles;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by zhouj on 2017-09-18.
 */

public class FragmentWalkFirstPage extends Fragment implements View.OnClickListener {


    private LinearCircles linearCircles;
    private TextView weather, lastDistance, lastSpeed;
    private Button startPace;
    private ImageView historyData;
    private ListView myListView;

    View view;
    GetMap map;

    private SharedPreferences sp;


    private void assignViews(View view) {

        linearCircles = (LinearCircles) view.findViewById(R.id.pace_compass);
        weather = (TextView) view.findViewById(R.id.weather_temp);
        startPace = (Button) view.findViewById(R.id.pace_button);
        lastDistance = (TextView) view.findViewById(R.id.last_pace_distance);
        lastSpeed = (TextView) view.findViewById(R.id.last_pace_speed);
        historyData = (ImageView) view.findViewById(R.id.iv_walk_data);
        historyData.setOnClickListener(this);


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        view = inflater.inflate(R.layout.fragment_walk_firstpage, container, false);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        assignViews(view);
        initData();
//        initView();
//        useAdapter();
        map = GetMap.getMap();
        map.getLocation(getContext());
///////////////拓展的/////////////////////////////////////////////////////////////////////////////
        ((MyApplication)getContext().getApplicationContext()).fragmentWalkFirstPage=FragmentWalkFirstPage.this;
        initView();
        refreshUI();

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        addListener();

    }

//    /**
//     * 初始化view
//     */
//    public void initView(){
//        myListView = (ListView) view.findViewById(R.id.lv_walk_myactivity);
//    }
//    /**
//     * 初始化数据
//     */
//    public List<ActivityTopic> getActivityTopicData(){
//        List<ActivityTopic> topicList = new ArrayList<>();
//
//        ActivityTopic activityTopic=new ActivityTopic();
//        activityTopic.setTopicImage(String.valueOf(R.mipmap.bitmap_walk));
//        activityTopic.setTopicTitle("every day");
//        activityTopic.setUserHeadImage(String.valueOf(R.mipmap.arvin_febry_302935_copy3));
//        activityTopic.setUserName("alien");
//        activityTopic.setTopicProgressbar(String.valueOf(R.mipmap.progressbar));
//        topicList.add(activityTopic);
//        ActivityTopic activityTopic2=new ActivityTopic();
//        activityTopic2.setTopicImage(String.valueOf(R.mipmap.bitmap_walk));
//        activityTopic2.setTopicTitle("every day");
//        activityTopic2.setUserHeadImage(String.valueOf(R.mipmap.arvin_febry_302935_copy3));
//        activityTopic2.setUserName("alien");
//        activityTopic2.setTopicProgressbar(String.valueOf(R.mipmap.progressbar));
//        topicList.add(activityTopic2);
//
//        return topicList;
//    }
//    /**
//     * 使用ListViewAdapter
//     */
//    public  void useAdapter(){
//        List<ActivityTopic> topicList=getActivityTopicData();
//        ListViewAdapter listViewAdapter=new ListViewAdapter(getContext());
//        listViewAdapter.setTopicList(topicList);
//        myListView.setAdapter(listViewAdapter);
//
//    }
    private void addListener() {
        startPace.setOnClickListener(this);
        weather.setOnClickListener(this);

    }


    private void initData() {

        //获取用户设置的计划锻炼步数，没有设置过的话默认172
        //String lastPace = sp.getString("last_pace_count", "172");
        int lastPace = sp.getInt("walk_day_step", 172);
        linearCircles.isNeedDraw=true;
        linearCircles.show(lastPace, "pace");
        lastDistance.setText(sp.getString("last_walk_distance", "0") + "km");
        lastSpeed.setText(sp.getString("last_walk_speed", "0") + "km/h");


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.weather_temp:
                map.getLocation(getContext());
                break;
            case R.id.pace_button:
                if (!SportingActivity.isSporting) {
                    Intent intent = new Intent(getContext(), CountDown.class);
                    intent.putExtra("activity", "pacing");
                    startActivity(intent);
                } else ToastShow.showToast(getContext(), "请先结束本次运动！");

                break;

            case R.id.iv_walk_data:

                startActivity(new Intent(getActivity(), HistoryChartActivity.class));
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    /////////////////////////////////屏蔽了原来的，并扩展，此后显示的是网上获取的activity了/////////////////////////
    public void refreshUI(){
        GetFromBmob.getActivityIdsByUserId(userId,handler);
        ((MyApplication)getContext().getApplicationContext()).showProgressDialog(getActivity());
    }

    /**
     * 初始化view
     */
    public void initView(){
        myListView = (ListView) view.findViewById(R.id.lv_walk_myactivity);
        myApplication=(MyApplication)getActivity().getApplicationContext();
        userId=myApplication.applicationMap.get("userObjectId");
    }

    List<ActivityData> alAD;
    List listToShow1;
    MyApplication myApplication;
    private String userId;
    final static int oneBox=1;

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
                    str=bundle.getString("MTAlist");
                    List<ActivityAndMember> ps = gson.fromJson(str, new TypeToken<List<ActivityAndMember>>(){}.getType());
                    //外卖菜名
                    if(ps.size()>0) {
                        ArrayList<String> al = new ArrayList<>();
                        for (ActivityAndMember gameScore : ps) {
                            al.add(gameScore.getActivityId());
                        }
                        //根据菜名叫外卖，到了的外卖放到一号邮箱
                        GetFromBmob.getActivitysByActivityIds(oneBox, al, handler);
                    } else {
                        useAdapter1(new ArrayList());
                        ((MyApplication)getContext().getApplicationContext()).stopProgressDialog();
                    }
                    break;
                //上面的过程执行后，获得一系列的Activity，以为没事了吗，还要获取创建者的相关信息
                //1号邮箱拿了外卖，该去拿餐具了，取了一个箱子，分格子，分别标上那份外卖的厨师名，重复的覆盖
                case 6:
                    str=bundle.getString("alAD1");
                    alAD=gson.fromJson(str,new TypeToken<List<ActivityData>>(){}.getType());
                    Log.e("database","alAD.size:"+alAD.size());
                    Map<String, User> creatorMap = new HashMap<>();
                    for (int i = 0; i < alAD.size(); i++) {
                        creatorId = alAD.get(i).getCreatorId();
                        creatorMap.put(creatorId, new User());
                    }
                    //叫外卖小哥送餐具过来
                    GetFromBmob.getUsersByUserIds(oneBox, creatorMap, handler);
                    break;
                //这里获得了含多个键为userId,值为User的键值对的Map
                //餐具也取好了，为每份外卖分配一种餐具，放到桌面上
                case 7:
                    //打开1号邮箱餐具包装
                    str=bundle.getString("mapUser1");
                    //到这里，终于把 外卖和对应的餐具，放到了一起，并放到桌子上。
                    listToShow1=getActivityDataTopicData(putTheTable(str,alAD));
                    myApplication.listToShow=listToShow1;
                    useAdapter1(listToShow1);
                    myApplication.stopProgressDialog();
                    break;
                default:break;
            }
        }
    };
    //摆桌子，也就是把各数据分门别类地放好，以便显示
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
            activityTopicForCircle.setUserName(creator.getNickName());
            activityTopicForCircle.setTopicProgressbar(String.valueOf(R.mipmap.progressbar));
            topicList.add(activityTopicForCircle);
        }
        Collections.sort(topicList);
        return topicList;
    }

    //新的使用适配器
    public void useAdapter1(List list) {
        final List<ActivityTopicForCircle> topicList = list;
        ListViewAdapterForCircle listViewAdapterForCircle = new ListViewAdapterForCircle(getContext());
        listViewAdapterForCircle.setTopicList(topicList);
        myListView.setAdapter(listViewAdapterForCircle);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String ADid=topicList.get(position).getADid();
                CirclePushCardActivity.jump(ADid,getActivity());
            }
        });
        if(myApplication.fragmentRunFirstPage!=null)
            myApplication.fragmentRunFirstPage.useAdapter_new(topicList);
        if(myApplication.fragmentRideFirstPage!=null)
            myApplication.fragmentRideFirstPage.useAdapter_new(topicList);
    }
}