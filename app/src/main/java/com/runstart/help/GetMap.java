package com.runstart.help;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.runstart.R;

/**
 * Created by zhonghao.song on 17-9-11.
 */

public class GetMap implements LocationSource, AMapLocationListener, WeatherSearch.OnWeatherSearchListener {
    private String location;
    private  Context context;
    private OnLocationChangedListener onLocationChangedListener;
    private AMapLocationClientOption mLocationOption;
    private AMapLocationClient client;
    private ProgressDialog dialog;


    private MapView mapView;
    private AMap aMap;

    private WeatherSearch search;

    public void setWeather(String result,String tempe){

        Activity activity=(Activity) context;
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(context);
        TextView walkWt=(TextView)activity.findViewById(R.id.weather_temp);
        ImageView walkWI=(ImageView)activity.findViewById(R.id.iv_weather);
        TextView runWt=(TextView)activity.findViewById(R.id.run_weather_temp);
        ImageView runWI=(ImageView)activity.findViewById(R.id.run_iv_weather);
        TextView rideWt=(TextView)activity.findViewById(R.id.ride_weather_temp);
        ImageView rideWI=(ImageView)activity.findViewById(R.id.ride_iv_weather);

        walkWt.setText(tempe);
        runWt.setText(tempe);
        rideWt.setText(tempe);

        walkWI.setImageResource(preferences.getInt(result,R.mipmap.yuxue));
        runWI.setImageResource(preferences.getInt(result,R.mipmap.yuxue));
        rideWI.setImageResource(preferences.getInt(result,R.mipmap.yuxue));


    }
    public void setNoWeather(){

        Activity activity=(Activity)context;
        TextView walkWt=(TextView)activity.findViewById(R.id.weather_temp);
        TextView runWt=(TextView)activity.findViewById(R.id.run_weather_temp);
        TextView rideWt=(TextView)activity.findViewById(R.id.ride_weather_temp);
        walkWt.setText("error");
        runWt.setText("error");
        rideWt.setText("error");


    }



    public void getLocation(Context context,boolean isFirstLoad) {
        this.context=context;
        dialog = new ProgressDialog(context);
        dialog.setMessage("getting location...");
        dialog.setCancelable(true);
        if(isFirstLoad){

            dialog.show();
        }

        View view = View.inflate(context, R.layout.follow, null);
        mapView = (MapView) view.findViewById(R.id.map);
        aMap = mapView.getMap();
        mapView.onCreate(null);
        mapView.onResume();
        aMap.setLocationSource(this);
        aMap.setMyLocationEnabled(true);


    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && null != onLocationChangedListener) {
            location = aMapLocation.getDistrict();
            WeatherSearchQuery mQuery = new WeatherSearchQuery(location, WeatherSearchQuery.WEATHER_TYPE_LIVE);
            search = new WeatherSearch(context);
            search.setOnWeatherSearchListener(this);
            search.setQuery(mQuery);
            search.searchWeatherAsyn();

        } else {
            dialog.dismiss();
            aMap=null;
            setNoWeather();
            deactivate();
            mapView.onDestroy();
        }

    }






    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        this.onLocationChangedListener = onLocationChangedListener;
        //设置定位监听
        client = new AMapLocationClient(context);
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        client.setLocationListener(this);
        //设置为高精度定位模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位参数
        client.setLocationOption(mLocationOption);
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
        client.startLocation();

    }

    @Override
    public void deactivate() {
        aMap=null;
        onLocationChangedListener = null;
        if (client != null) {
            client.stopLocation();
            client.onDestroy();
        }
        client = null;
    }

    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult localWeatherLiveResult, int i) {
        LocalWeatherLive weatherLive = null;

        dialog.dismiss();
        if (i == 1000) {
            weatherLive = localWeatherLiveResult.getLiveResult();
            if (localWeatherLiveResult != null && weatherLive != null) {
                String text=weatherLive.getWeather();
                //getWeatherText.setText( text+" "+weatherLive.getTemperature()+"℃");
               // getWeatherImg.setImageResource(WalkFragment.weatherImg.get(text));
                setWeather(text," "+weatherLive.getTemperature()+"℃");
                aMap=null;
                mapView.onPause();
                deactivate();
                mapView.onDestroy();
                context = null;
            }
        } else {
            search=null;
            aMap=null;
            onLocationChangedListener=null;
            mapView.onPause();
            deactivate();
            mapView.onDestroy();
            setNoWeather();
        }
    }

    @Override
    public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {

    }

    private static class GetInstance {
        private static GetMap myMap = new GetMap();
    }

    private GetMap() {

    }

    public static GetMap getMap() {

        return GetInstance.myMap;
    }
}
