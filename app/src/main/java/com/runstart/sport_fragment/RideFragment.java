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
import com.runstart.help.GetMap;
import com.runstart.help.CountDown;
import com.runstart.help.ToastShow;
import com.runstart.sport_map.SportingActivity;
import com.runstart.view.LinearCircles;


public class RideFragment extends Fragment implements View.OnClickListener{

    private TextView allDistanceText, weatherText, averageSpeedText;
    View view;
    LinearCircles linearCircles;
    private SharedPreferences preferences;
    GetMap getMap;
    Button startBtn;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_ride, container, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        initView(view);
        setView();
        getMap=GetMap.getMap();
        return view;
    }


    public void initView(View view) {


        allDistanceText = (TextView) view.findViewById(R.id.total_ride_distance);
        weatherText = (TextView) view.findViewById(R.id.ride_weather_temp);

        linearCircles=(LinearCircles)view.findViewById(R.id.pace_compass);


        averageSpeedText = (TextView) view.findViewById(R.id.average_riding_speed);


        weatherText.setOnClickListener(this);

        startBtn=(Button) view.findViewById(R.id.start_riding);
        startBtn.setOnClickListener(this);


    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.ride_weather_temp:
                getMap.getLocation(getContext());
                break;

            case R.id.start_riding:
                if(!SportingActivity.isSporting){
                    Intent intent=new Intent(getActivity(), CountDown.class);
                    intent.putExtra("activity","riding");
                    startActivity(intent);
                }else ToastShow.showToast(getContext(),"请先结束本次运动！");

                break;

            default:
                break;
        }

    }
    public void setView() {



        averageSpeedText.setText(preferences.getString("last_ride_speed", "0")+ "km/h");
        allDistanceText.setText(preferences.getString("all_ride_distance", "0")+ "km");
        float pace=Float.valueOf( preferences.getString("last_ride_distance", "0"));

        linearCircles.show(pace*1000,"ride");

    }


    @Override
    public void onStart() {
        super.onStart();
            setView();


    }
}
