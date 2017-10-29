package com.runstart.sports.sport_activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.LocationSource;
import com.runstart.MainActivity;
import com.runstart.R;
import com.runstart.help.GetLocationData;
import com.runstart.sports.sport_fragment.GetMapFragment;
import com.runstart.sports.sport_service.ServiceLocation;

/**
 * Created by zhonghao.song on 17-9-21.
 * 作为三个类型的活动父类，主要和service进行通信，将service的运动数据：时间，距离，经纬度传递过来
 */

public class SportingActivity extends Activity implements View.OnClickListener {

    private GetMapFragment getMapFragment;

    private Button stopBtn, carryBtn, endBtn;
    private TextView distanceText, speedText, calorieText, titleText, hideText;
    private Chronometer timer;


    private ImageView screenMap, halfMap;
    private ViewGroup.LayoutParams titleParams, mapParams;
    private RelativeLayout titleRL, mapRL;


    public static boolean isStop;
    public static boolean isEnd;
    public static boolean isSporting = false;
    private boolean isReCreate;
    private boolean isReMap;

    DecimalFormat decimalFormat = new DecimalFormat(".000");
    private boolean isBind = false, isScreen = false;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Intent intent;
    int miss;
    float distances = 0;
    int imageDialog;
    String textDialog;

    ServiceLocation locationService;


    //  PowerManager.WakeLock wakeLock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sporting_status);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        PowerManager manager=(PowerManager)getSystemService(Context.POWER_SERVICE);
//        wakeLock=manager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP|PowerManager.SCREEN_BRIGHT_WAKE_LOCK,"bright");
//        wakeLock.acquire();
        editor = preferences.edit();

        isStop = false;
        isEnd = false;
        isSporting = true;
        isReCreate = true;
        isReMap = false;
        initView();
        setupService();
    }

    /**
     * 开启ride服务
     */
    private void setupService() {

        isBind = bindService(intent, conn, Context.BIND_AUTO_CREATE);
        startService(intent);
    }

    /**
     * 用于查询应用服务（application Service）的状态的一种interface，
     * 更详细的信息可以参考Service 和 context.bindService()中的描述，
     * 和许多来自系统的回调方式一样，ServiceConnection的方法都是进程的主线程中调用的。
     */
    ServiceConnection conn = new ServiceConnection() {
        /**
         * 在建立起于Service的连接时会调用该方法，目前Android是通过IBind机制实现与服务的连接。
         * @param name 实际所连接到的Service组件名称
         * @param service 服务的通信信道的IBind，可以通过Service访问对应服务
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            locationService = ((ServiceLocation.LocationBinder) service).getService();

            //设置监听回调
            locationService.registerCallback(new GetLocationData() {
                @Override
                public void updateTime(int time, float distance) {
                    if (!isStop) {
                        distanceText.setText(distance + "m");
                        distances = distance;
                        setTime(time);
                    }
                }

                @Override
                public void updateLL(AMapLocation location, LocationSource.OnLocationChangedListener mListener) {
//                    if (!isReCreate) {
//                        getMapFragment.RestartSetMap(locationService.latLngList);
//                        Log.e("Map","重绘地图！！！！！"+locationService.latLngList.size());
//                    }else {
//                        getMapFragment.getLocation(location, mListener);
//                        Log.e("Map","保持绘制地图！！！！！");
//                    }
//
                    if (isReCreate) {
                        if (!isReMap) {
                            getMapFragment.RestartSetMap(locationService.latLngList);
                            Log.e("Map", "重绘地图！！！！！" + locationService.latLngList.size());
                            isReMap = true;
                        } else {
                            getMapFragment.getLocation(location, mListener);
                            Log.e("Map", "保持绘制地图！！！！！");
                        }
                    }
                }
            });
        }

        /**
         * 当与Service之间的连接丢失的时候会调用该方法，
         * 这种情况经常发生在Service所在的进程崩溃或者被Kill的时候调用，
         * 此方法不会移除与Service的连接，当服务重新启动的时候仍然会调用 onServiceConnected()。
         * @param name 丢失连接的组件名称
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public void initView() {

        titleText = (TextView) findViewById(R.id.sporting_status_text);

        hideText = (TextView) findViewById(R.id.sporting_status_hide);
        hideText.setOnClickListener(this);
        distanceText = (TextView) findViewById(R.id.sporting_distance_show);
        speedText = (TextView) findViewById(R.id.sporting_speed_show);
        timer = (Chronometer) findViewById(R.id.sporting_time_show);
        calorieText = (TextView) findViewById(R.id.sporting_calorie_show);
        getMapFragment = (GetMapFragment) getFragmentManager().findFragmentById(R.id.map_fragment);

        screenMap = (ImageView) findViewById(R.id.sport_all_screen);
        screenMap.setOnClickListener(this);
        halfMap = (ImageView) findViewById(R.id.sport_half_screen);
        halfMap.setOnClickListener(this);
        titleRL = (RelativeLayout) findViewById(R.id.show_sporting_title);
        mapRL = (RelativeLayout) findViewById(R.id.layout_map_screen);
        mapParams = mapRL.getLayoutParams();
        titleParams = titleRL.getLayoutParams();


        stopBtn = (Button) findViewById(R.id.sporting_stop_btn);
        carryBtn = (Button) findViewById(R.id.sport_carry_btn);
        endBtn = (Button) findViewById(R.id.sport_end_btn);
        carryBtn.setOnClickListener(this);
        endBtn.setOnClickListener(this);
        stopBtn.setOnClickListener(this);


    }

    public void setTime(int miss) {
        this.miss = miss;
        timer.setText(getMapFragment.format(miss));
        speedText.setText(decimalFormat.format(miss * 1000 / distances / 60));
        //构造方法的字符格式这里如果小数不足2位,会以0补足.
        //format 返回的是字符串
        //体重kg，跑步时间h，30，除以速度
        float mno = 55.0f * miss * 30 / (400 * miss / distances / 60) / 3600;
        calorieText.setText(decimalFormat.format(mno));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.sporting_status_hide:
                startActivity(new Intent(SportingActivity.this, MainActivity.class));
                break;

            case R.id.sport_all_screen:

                setAllScreen();
                break;
            case R.id.sport_half_screen:
                setAllScreen();
                break;

            case R.id.sporting_stop_btn:
                if (isEnd == true) {
                    finish();
                } else {
                    isStop = true;
                    stopBtn.setVisibility(View.GONE);
                    carryBtn.setVisibility(View.VISIBLE);
                    endBtn.setVisibility(View.VISIBLE);
                }

                break;

            case R.id.sport_carry_btn:
                stopBtn.setVisibility(View.VISIBLE);
                carryBtn.setVisibility(View.GONE);
                endBtn.setVisibility(View.GONE);
                isStop = false;
                break;

            case R.id.sport_end_btn:
                isEnd = true;
                isStop = true;
                if (isBind) {
                    isBind = false;
                    unbindService(conn);
                }
                isSporting = false;
                stopService(intent);

                stopBtn.setVisibility(View.VISIBLE);
                stopBtn.setText("Exit");
                carryBtn.setVisibility(View.GONE);
                endBtn.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    public void setAllScreen() {
        if (!isScreen) {
            mapParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            halfMap.setVisibility(View.VISIBLE);
            titleParams.height = 0;

        } else {
            mapParams.height = 810;
            titleParams.height = 140;
            halfMap.setVisibility(View.GONE);
        }
        titleRL.setLayoutParams(titleParams);
        mapRL.setLayoutParams(mapParams);
        isScreen = !isScreen;
    }

    @Override
    public void onBackPressed() {
        if (isEnd) {
            finish();
        } else {
            AlertDialog.Builder alter = new AlertDialog.Builder(this);
            alter.setIcon(imageDialog)
                    .setTitle(textDialog)
                    .setMessage("是否结束本次运动？")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            isEnd = true;
                            if (isBind) {
                                isBind = false;
                                unbindService(conn);
                            }
                            stopService(intent);
                            isSporting = false;
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            alter.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("Map", "活动销毁！！");
        if (isBind)
            unbindService(conn);
        isReCreate = false;
        isReMap = false;
        //wakeLock.release();
    }


}
