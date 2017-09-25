package com.runstart.sport_fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.runstart.R;
import com.runstart.help.CountDown;
import com.runstart.help.GetMap;
import com.runstart.help.ToastShow;
import com.runstart.history.HistoryChartActivity;
import com.runstart.sport_map.SportingActivity;
import com.runstart.view.LinearCircles;


/**
 * Created by zhouj on 2017-09-18.
 */

public class FragmentWalkFirstPage extends Fragment implements View.OnClickListener {


    private LinearCircles linearCircles;
    private TextView weather, lastDistance, lastSpeed;
    private Button startPace;
    private ImageView historyData;


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

        map = GetMap.getMap();
        map.getLocation(getContext());
        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        addListener();

    }

    private void addListener() {
        startPace.setOnClickListener(this);
        weather.setOnClickListener(this);

    }


    private void initData() {

        //获取用户设置的计划锻炼步数，没有设置过的话默认7000
        String lastPace = sp.getString("last_pace_count", "181");
        linearCircles.show(Integer.valueOf(lastPace), "pace");
        lastDistance.setText(sp.getString("last_pace_distance", "0") + "km");
        lastSpeed.setText(sp.getString("last_pace_speed", "0") + "km/h");


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
}