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

public class FragmentRunFirstPage extends Fragment implements View.OnClickListener {

    ListView myListView;
    private View view;

    private TextView allDistanceText, weatherText, averageSpeedText;
    private Button startRunnigBtn;


    private LinearCircles linearCircles;
    private SharedPreferences preferences;
    private GetMap getMap;


    public void initView(View view) {


        allDistanceText = (TextView) view.findViewById(R.id.total_run_distance);
        weatherText = (TextView) view.findViewById(R.id.run_weather_temp);

        linearCircles = (LinearCircles) view.findViewById(R.id.pace_compass);


        averageSpeedText = (TextView) view.findViewById(R.id.average_running_speed);


        weatherText.setOnClickListener(this);

        startRunnigBtn = (Button) view.findViewById(R.id.start_running);
        startRunnigBtn.setOnClickListener(this);


    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_run_firstpage, container, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        initView(view);
        getMap = GetMap.getMap();
        setView();
        initView();
        useAdapter();
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
    public void setView() {

        linearCircles.isNeedDraw=true;
        averageSpeedText.setText(preferences.getString("last_run_speed", "0") + "km/h");
        allDistanceText.setText(preferences.getString("all_run_distance", "0") + "km");
        float pace = Float.valueOf(preferences.getString("last_run_distance", "0"));
        linearCircles.show(pace * 1000, "run");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.run_weather_temp:
                //getMap.getLocation(weatherText,weatherImg,getContext());
                getMap.getLocation(getContext());
                break;

            case R.id.start_running:
                if (!SportingActivity.isSporting) {
                    Intent intent = new Intent(getContext(), CountDown.class);
                    intent.putExtra("activity", "running");
                    startActivity(intent);
                } else ToastShow.showToast(getContext(), "请先结束本次运动！");

                break;


            default:
                break;
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        setView();


    }

    @Override
    public void onResume() {
        super.onResume();

    }

}
