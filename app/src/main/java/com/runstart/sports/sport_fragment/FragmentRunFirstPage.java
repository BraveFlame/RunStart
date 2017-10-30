package com.runstart.sports.sport_fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.runstart.R;
import com.runstart.bean.ActivityTopicForCircle;
import com.runstart.circle.circle_activity.CirclePushCardActivity;
import com.runstart.help.CountDown;
import com.runstart.help.GetMap;
import com.runstart.help.ToastShow;
import com.runstart.MyApplication;
import com.runstart.history.mysportdb.HistoryChartActivity;
import com.runstart.middle.ListViewAdapterForCircle;
import com.runstart.sports.sport_activity.SportingActivity;
import com.runstart.view.LinearCircles;

import java.util.List;

/**
 * Created by zhonghao.song on 17-9-26.
 */

public class FragmentRunFirstPage extends Fragment implements View.OnClickListener {

    private ListView myListView;
    private View view;

    private TextView allDistanceText, weatherText, averageSpeedText,sportAllData;
    private Button startRunnigBtn;
    private ImageView historyData;


    private LinearCircles linearCircles;
    private SharedPreferences preferences;
    private GetMap getMap;


    public void initView(View view) {


        allDistanceText = (TextView) view.findViewById(R.id.total_run_distance);
        weatherText = (TextView) view.findViewById(R.id.run_weather_temp);

        linearCircles = (LinearCircles) view.findViewById(R.id.pace_compass);
        averageSpeedText = (TextView) view.findViewById(R.id.average_running_speed);


        sportAllData=(TextView)view.findViewById(R.id.sport_all_data);
        sportAllData.setText(preferences.getInt("all_distance",0)/1000+"km");



        weatherText.setOnClickListener(this);
        startRunnigBtn = (Button) view.findViewById(R.id.start_running);
        startRunnigBtn.setOnClickListener(this);

        historyData = (ImageView) view.findViewById(R.id.iv_run_data);
        historyData.setOnClickListener(this);

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_run_firstpage, container, false);
        ((MyApplication)getContext().getApplicationContext()).fragmentRunFirstPage=FragmentRunFirstPage.this;
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        initView(view);
        getMap = GetMap.getMap();
        setView();
        initView();
        useAdapter_new(((MyApplication)getContext().getApplicationContext()).listToShow);

        return view;
    }

    /**
     * 初始化view
     */
    public void initView(){
        myListView = (ListView) view.findViewById(R.id.lv_walk_myactivity);
        linearLayout=(LinearLayout)view.findViewById(R.id.ll_main_page_no_activity);
    }

    public void setView() {


        linearCircles.isNeedDraw=true;
        averageSpeedText.setText(preferences.getString("last_run_speed", "0") + "km/h");
        allDistanceText.setText(preferences.getInt("all_run_distance", 0)/1000 + "km");
        float pace = Float.valueOf(preferences.getString("last_run_distance", "0"));
        linearCircles.show(pace * 1000, "run");


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.run_weather_temp:
                //getMap.getLocation(weatherText,weatherImg,getContext());
                getMap.getLocation(getContext(),true);
                break;

            case R.id.start_running:
                if (!SportingActivity.isSporting) {
                    Intent intent = new Intent(getContext(), CountDown.class);
                    intent.putExtra("activity", "running");
                    startActivity(intent);
                } else ToastShow.showToast(getContext(), "请先结束本次运动！");

                break;
            case R.id.iv_run_data:
                startActivity(new Intent(getActivity(), HistoryChartActivity.class));
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

    //////////////////////新做的useAdapter，之后网上获取的activity就能显示了///////////////////////////
    LinearLayout linearLayout;

    public void useAdapter_new(List list){
        if(list.size()!=0) {
            myListView.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
            final List<ActivityTopicForCircle> topicList = list;
            ListViewAdapterForCircle listViewAdapterForCircle = new ListViewAdapterForCircle(getContext());
            listViewAdapterForCircle.setTopicList(topicList);
            myListView.setAdapter(listViewAdapterForCircle);
            myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String ADid = topicList.get(position).getADid();
                    CirclePushCardActivity.jump(ADid, getActivity());
                }
            });
        } else {
            linearLayout.setVisibility(View.VISIBLE);
            myListView.setVisibility(View.GONE);
        }
    }
}
