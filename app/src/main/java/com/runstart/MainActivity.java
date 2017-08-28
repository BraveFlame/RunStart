package com.runstart;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.MyLocationStyle;



public class MainActivity extends AppCompatActivity {
    MapView mapView = null;
    AMap aMap;
    int i = 3,miss=0;
    boolean isStop=false;
    TextView textView;
    Button button,stopBtn;
    Chronometer timer;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:
                    if (i > 0)
                        textView.setText(i + "");
                    else {
                        mapView.setVisibility(View.VISIBLE);

                        timer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                            @Override
                            public void onChronometerTick(Chronometer chronometer) {
                                miss++;
                                timer.setText(format(miss));
                            }
                        });
                        timer.start();

                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button=(Button)findViewById(R.id.start);

        stopBtn=(Button)findViewById(R.id.stop);

        timer=(Chronometer)findViewById(R.id.timer);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.setVisibility(View.VISIBLE);
            }
        });



        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isStop){
                    isStop=false;
                    timer.start();
                    stopBtn.setText("暂停");
                }else {
                    isStop=true;
                    timer.stop();
                    stopBtn.setText("继续");
                }

            }
        });


//        LinearLayout layout=(LinearLayout)findViewById(R.id.layout);
//        TextView textView = new TextView(this);
//        textView.setPadding(10, 10, 10, 10);
//        textView.setText("3");
//        textView.setTextSize(60);
//       LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 0);
//       params.weight=1;
//        params.setMargins(30, 10, 30, 0);
//        layout.addView(textView,params);

        textView = (TextView) findViewById(R.id._321);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (i >= 0) {
                    try {
                        Thread.sleep(1400);
                        i--;
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                i = 3;
            }
        }).start();


        mapView = (MapView) findViewById(R.id.map);
        if (aMap == null) {
            aMap = mapView.getMap();
        }

        mapView.onCreate(savedInstanceState);
        showMapView();


    }


    public String format(int miss){

        String hh=miss/3600>9?miss/3600+"":"0"+miss/3600;
        String mm=(miss%3600)/60>9?(miss%3600)/60+"":"0"+(miss%3600)/60;
        String ss=(miss%3600)%60>9?(miss%3600)%60+"":"0"+(miss%3600)%60;

        return hh+":"+mm+":"+ss;
    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    public void showMapView() {

        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        //aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）默认执行此种模式。
        myLocationStyle.showMyLocation(true);//设置是否显示定位小蓝点，用于满足只想使用定位，不想使用定位小蓝点的场景，设置false以后图面上不再有定位蓝点的概念，但是会持续回调位置信息。

        myLocationStyle.anchor(0.0f, 1.0f);
        myLocationStyle.strokeColor(Color.BLACK);
        myLocationStyle.radiusFillColor(Color.BLUE);

        aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {


            }
        });
    }


}
