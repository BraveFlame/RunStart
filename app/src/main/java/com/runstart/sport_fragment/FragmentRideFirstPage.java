package com.runstart.sport_fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.runstart.R;
import com.runstart.bean.ActivityTopic;
import com.runstart.help.CountDown;
import com.runstart.help.GetMap;
import com.runstart.help.ToastShow;
import com.runstart.middle.ListViewAdapter;
import com.runstart.sport_map.SportingActivity;
import com.runstart.view.LinearCircles;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 17-9-26.
 */

public class FragmentRideFirstPage extends Fragment implements View.OnClickListener {

    private TextView allDistanceText, weatherText, averageSpeedText;
    View view;
    LinearCircles linearCircles;
    private SharedPreferences preferences;
    GetMap getMap;
    Button startBtn;
    ListView myListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_ride_firstpage, container, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        initView(view);
        setView();
        initView();
        useAdapter();
        getMap = GetMap.getMap();
        return view;
    }
    /**
     * 初始化view
     */
    public void initView(){
        myListView = (ListView) view.findViewById(R.id.lv_walk_myactivity);
    }
    /**
     * 初始化数据
     */
    public List<ActivityTopic> getActivityTopicData(){
        List<ActivityTopic> topicList = new ArrayList<>();

        ActivityTopic activityTopic=new ActivityTopic();
        activityTopic.setTopicImage(String.valueOf(R.mipmap.bitmap_walk));
        activityTopic.setTopicTitle("every day");
        activityTopic.setUserHeadImage(String.valueOf(R.mipmap.arvin_febry_302935_copy3));
        activityTopic.setUserName("alien");
        activityTopic.setTopicProgressbar(String.valueOf(R.mipmap.progressbar));
        topicList.add(activityTopic);
        ActivityTopic activityTopic2=new ActivityTopic();
        activityTopic2.setTopicImage(String.valueOf(R.mipmap.bitmap_walk));
        activityTopic2.setTopicTitle("every day");
        activityTopic2.setUserHeadImage(String.valueOf(R.mipmap.arvin_febry_302935_copy3));
        activityTopic2.setUserName("alien");
        activityTopic2.setTopicProgressbar(String.valueOf(R.mipmap.progressbar));
        topicList.add(activityTopic2);

        return topicList;
    }
    /**
     * 使用ListViewAdapter
     */
    public  void useAdapter(){
        List<ActivityTopic> topicList=getActivityTopicData();
        ListViewAdapter listViewAdapter=new ListViewAdapter(getContext());
        listViewAdapter.setTopicList(topicList);
        myListView.setAdapter(listViewAdapter);

    }

    public void initView(View view) {


        allDistanceText = (TextView) view.findViewById(R.id.total_ride_distance);
        weatherText = (TextView) view.findViewById(R.id.ride_weather_temp);

        linearCircles = (LinearCircles) view.findViewById(R.id.pace_compass);


        averageSpeedText = (TextView) view.findViewById(R.id.average_riding_speed);


        weatherText.setOnClickListener(this);

        startBtn = (Button) view.findViewById(R.id.start_riding);
        startBtn.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.ride_weather_temp:
                getMap.getLocation(getContext());
                break;

            case R.id.start_riding:
                if (!SportingActivity.isSporting) {
                    Intent intent = new Intent(getActivity(), CountDown.class);
                    intent.putExtra("activity", "riding");
                    startActivity(intent);
                } else ToastShow.showToast(getContext(), "请先结束本次运动！");

                break;

            default:
                break;
        }

    }

    public void setView() {


        averageSpeedText.setText(preferences.getString("last_ride_speed", "0") + "km/h");
        allDistanceText.setText(preferences.getString("all_ride_distance", "0") + "km");
        float pace = Float.valueOf(preferences.getString("last_ride_distance", "0"));
        linearCircles.isNeedDraw=true;
        linearCircles.show(pace * 1000, "ride");

    }


    @Override
    public void onStart() {
        super.onStart();
        setView();


    }
}
