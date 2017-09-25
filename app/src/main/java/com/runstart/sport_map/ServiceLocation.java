package com.runstart.sport_map;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.runstart.R;
import com.runstart.help.GetLocationData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 17-9-19.
 */

public class ServiceLocation extends Service implements LocationSource, AMapLocationListener {

    private AMap aMap;
    private MapView mapView;
    //以前的定位点
    private AMapLocation aMapLocation;
    private LatLng oldLatLng,newLatLng;
    private boolean isFirstLatLng=true;
    public List<LatLng>latLngList=new ArrayList<>();

    private NotificationManager mNotificationManager;
    private Notification notification;

    float distance = 0.0f;


    private View view;
    private TimeCount timer;
    int miss = 0;
    public static boolean isActivityLive=true;



    /**
     * IBinder对象，向Activity传递数据的桥梁
     */
    private LocationBinder locationBinder = new LocationBinder();
    GetLocationData getLocationData;
    NotificationCompat.Builder mBuilder;

    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;


    @Override
    public void onCreate() {
        super.onCreate();
        view = LayoutInflater.from(this).inflate(R.layout.follow, null);
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(null);// 此方法必须重写
        isActivityLive=true;
        RidingActivity.isEnd=false;
        init();
        initNotification();
        startTimeCount();
    }


    public String format(int miss) {

        String hh = miss / 3600 > 9 ? miss / 3600 + "" : "0" + miss / 3600;
        String mm = (miss % 3600) / 60 > 9 ? (miss % 3600) / 60 + "" : "0" + (miss % 3600) / 60;
        String ss = (miss % 3600) % 60 > 9 ? (miss % 3600) % 60 + "" : "0" + (miss % 3600) % 60;

        return hh + ":" + mm + ":" + ss;
    }


    /**
     * 保存记步数据
     */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            // 如果计时器正常结束，则开始计步
            if(!SportingActivity.isStop){
                miss++;
            }
            updateNotify();
            timer.cancel();
            if(getLocationData!=null){
                getLocationData.updateTime(miss,distance);
            }

            if(!SportingActivity.isEnd)
                startTimeCount();
            else mNotificationManager.cancel(101);

        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

    }
    private void startTimeCount() {
        if (timer == null) {
            timer = new TimeCount(1000, 1000);
        }
        timer.start();
    }

    public void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }

        //定位
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位 LOCATION_TYPE_LOCATE、跟随 LOCATION_TYPE_MAP_FOLLOW 或地图根据面向方向旋转 LOCATION_TYPE_MAP_ROTATE
        aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_ROTATE);

        //画线
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
        //使用 aMap.setMapTextZIndex(2) 可以将地图底图文字设置在添加的覆盖物之上
        aMap.setMapTextZIndex(2);
        //
        // setUpMap(new LatLng(43.828, 87.621), new LatLng(43.800, 87.621));
    }

    /**
     * 更新步数通知
     */
    private void initNotification() {

        mBuilder = new NotificationCompat.Builder(this);
        mBuilder
                .setContentText("sporting  " + distance + " m" + "  " + format(miss))
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示
                .setPriority(Notification.PRIORITY_DEFAULT)//设置该通知优先级
                .setAutoCancel(false)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(true)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setSmallIcon(R.mipmap.logo);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notification = mBuilder.build();
        startForeground(101, notification);
    }

    public void updateNotify(){

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notification=mBuilder.build();
        mNotificationManager.notify(101,notification);
        if (getLocationData != null) {
            getLocationData.updateTime(miss, distance);
            getLocationData.updateLL(aMapLocation,mListener);
        }
    }

    /**
     * 注册UI更新监听
     *
     * @param
     */
    public void registerCallback(GetLocationData getLocationData) {
        this.getLocationData = getLocationData;
    }

    /**
     * 向Activity传递数据的纽带
     */
    public class LocationBinder extends Binder {

        /**
         * 获取当前service对象
         *
         * @return StepService
         */
        public ServiceLocation getService() {
            return ServiceLocation.this;
        }
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                this.aMapLocation = amapLocation;
                newLatLng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());

                if (isFirstLatLng) {
                    //记录第一次的定位信息
                    oldLatLng = newLatLng;
                    isFirstLatLng = false;
                    latLngList.add(newLatLng);
                }
                //位置有变化
                if (oldLatLng != newLatLng) {
                    latLngList.add(newLatLng);
                   // Log.e("AmapLocation", amapLocation.getLatitude() + "," + amapLocation.getLongitude());
                    if (getLocationData != null) {

                        getLocationData.updateLL(amapLocation, mListener);
                        if (!SportingActivity.isStop) {
                            distance += AMapUtils.calculateLineDistance(oldLatLng, newLatLng);

                        }
                      //  getLocationData.updateTime(miss, distance);
                        oldLatLng = newLatLng;
                    }
                } else {
                    String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                    Log.e("AmapLocation", "AmapErr" + errText);

                }
            }

        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            mLocationOption.setOnceLocation(false);
            /**
             * 设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
             * 注意：只有在高精度模式下的单次定位有效，其他方式无效
             */
            mLocationOption.setGpsFirst(true);
            // 设置发送定位请求的时间间隔,最小值为1000ms,1秒更新一次定位信息
            mLocationOption.setInterval(1000);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }


    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mapView.onResume();
        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onPause();
        deactivate();
        mapView.onDestroy();
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return locationBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}
