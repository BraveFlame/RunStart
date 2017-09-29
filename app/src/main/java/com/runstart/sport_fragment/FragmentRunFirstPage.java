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
import android.widget.TextView;

import com.runstart.R;
import com.runstart.help.CountDown;
import com.runstart.help.GetMap;
import com.runstart.help.ToastShow;
import com.runstart.sport_map.SportingActivity;
import com.runstart.view.LinearCircles;

/**
 * Created by user on 17-9-26.
 */

public class FragmentRunFirstPage extends Fragment implements View.OnClickListener {


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
        return view;
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
