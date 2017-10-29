<<<<<<< Updated upstream
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
=======
package com.runstart;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import com.runstart.bottom.CircleFragment;
import com.runstart.bottom.FriendsFragment;
import com.runstart.bottom.HeadPageFragment;
import com.runstart.bottom.MineFragment;
import com.runstart.friend.chat.ListenMsgService;
import com.runstart.friend.chat.LocalChatLog;
import com.runstart.help.ActivityCollector;
import com.runstart.help.GetSHA1;


/**
 * 主界面的总体布局的实现
 * @author
 * @version 1.0
 */
public class MainActivity extends FragmentActivity {
    MyApplication myApplication;
    //定义FragmentLayout布局
    private FrameLayout frameLayout;
    //定义存放fragment的数组
    private Fragment[] mFragments;
    //定义RadioGroup组件
    private RadioGroup radioGroup;
    //定义选项index
    int mIndex;
    public static String activityKeep;
    private Intent intent;



    //声明需要使用的运行时权限
    private static final String[] PERMISSION = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myApplication=(MyApplication)getApplicationContext();
        ActivityCollector.addActivity(this);
        //监听消息
        intent = new Intent(this, ListenMsgService.class);
        startService(intent);
        //初始化底部状态栏的fragment
        initFragment();
        //底部状态栏切换fragment
        setRadioGroup();
        getPermission(PERMISSION);
        LocalChatLog localChatLog=LocalChatLog.getLocalChatLog(this);


    }

    public void getPermission(String[] permissions) {
        boolean isPermist = true;
        //判断权限是否获取

        for (String permission : permissions) {
            if (checkSelfPermission(permission)
                    != PackageManager.PERMISSION_GRANTED) {
                isPermist=false;
                Log.d("Path", " permission =" + permission);
            }
        }
        if (!isPermist) {
            //如果没有获取权限就主动申请
            ActivityCompat.requestPermissions(this, permissions, 990);
        }
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {

        return super.onCreateView(name, context, attrs);
    }

    @Override
    protected void onResume() {
        /**
         * 设置为竖屏
         */
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        if((mIndex==0)&(myApplication.isFragmentWalkShouldRefresh)) {
            myApplication.fragmentWalkFirstPage.refreshUI();
            myApplication.isFragmentWalkShouldRefresh=false;
        }
        super.onResume();
        GetSHA1.getCertificateSHA1Fingerprint(this);
    }

    /**
     * 初始化Fragment框架
     */
    public void initFragment(){
        frameLayout = (FrameLayout) findViewById(R.id.fl_content);
        radioGroup = (RadioGroup) findViewById(R.id.rg_radioGroup);
        HeadPageFragment headPageFragment = new HeadPageFragment();
        FriendsFragment friendsFragment = new FriendsFragment();
        CircleFragment circleFragment=new CircleFragment();
        MineFragment mineFragment=new MineFragment();
        //初始化mFragments数组
        mFragments = new Fragment[]{headPageFragment, friendsFragment, circleFragment, mineFragment};
        //定义事务
        FragmentTransaction fg=getSupportFragmentManager().beginTransaction();
        //设置默认首页
        fg.add(R.id.fl_content,headPageFragment).commit();
        //默认设置为0
        setIndexSelected(0);

    }

    /**
     * 底层状态栏的切换选择的Item
     * @param index
     */
    private void setIndexSelected(int index) {
        if(mIndex==index){
            return;
        }
        //开始事务
        FragmentTransaction fg=getSupportFragmentManager().beginTransaction();
        //隐藏frgment
        fg.hide(mFragments[mIndex]);
        //判断是否添加fragment
        if (!mFragments[index].isAdded()) {
            fg.add(R.id.fl_content, mFragments[index]).show(mFragments[index]);
        }else {
            fg.show(mFragments[index]);
        }
        //提交事务
        fg.commit();
        //再次赋值
        mIndex=index;

    }

    /**
     * 底层状态栏的切换选择
     */
    private void setRadioGroup() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkId) {
                switch (checkId){
                    case R.id.rb_headPage:
                        setIndexSelected(0);
                        if(myApplication.isFragmentWalkShouldRefresh) {
                            myApplication.fragmentWalkFirstPage.refreshUI();
                            myApplication.isFragmentWalkShouldRefresh=false;
                        }
                        break;
                    case R.id.rb_friends:
                        setIndexSelected(1);
                        break;
                    case R.id.rb_circle:
                        setIndexSelected(2);
                        break;
                    case R.id.rb_mine:
                        setIndexSelected(3);
                        break;
                    default:
                        break;
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
        stopService(intent);
    }

    @Override
    public void onBackPressed() {
        Intent home=new Intent(Intent.ACTION_MAIN);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }
}
>>>>>>> Stashed changes
