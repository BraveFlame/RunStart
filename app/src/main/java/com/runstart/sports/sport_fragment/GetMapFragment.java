package com.runstart.sports.sport_fragment;


import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.PolylineOptions;
import com.runstart.R;
import com.runstart.sports.sport_activity.SportingActivity;

import java.util.List;

/**
 * Created by zhonghao.song on 17-8-29.
 */

public class GetMapFragment extends PreferenceFragment{
    /**
     * 定位 + 画线
     */
    /**
     * 定位 + 画线
     */

    //以前的定位点
    private LatLng oldLatLng;
    //是否是第一次定位
    private boolean isFirstLatLng = true;
    private AMap aMap;
    private MapView mapView;


    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.follow, container, false);
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        isFirstLatLng = true;
        init();
        return view;
    }
    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();

    }
    public void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }

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


    public void getLocation( AMapLocation amapLocation, LocationSource.OnLocationChangedListener mListener) {

        if (mListener != null && amapLocation != null) {
            mListener.onLocationChanged(amapLocation);
            LatLng newLatLng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
            if (isFirstLatLng) {
                //记录第一次的定位信息
                oldLatLng = newLatLng;
                isFirstLatLng = false;
            }
            if (oldLatLng != newLatLng) {
                setUpMap(oldLatLng, newLatLng);
                oldLatLng = newLatLng;

            }
            Log.e("AmapLocation", amapLocation.getLatitude() + "," + amapLocation.getLongitude());
            Log.e("AmapLocation", amapLocation.getLatitude() + "," + amapLocation.getLongitude());

        }
    }


    /**
     * 绘制两个坐标点之间的线段,从以前位置到现在位置
     */
    public void setUpMap(LatLng oldData, LatLng newData) {

        if(!SportingActivity.isStop){
            // 绘制一个大地曲线
            aMap.addPolyline((new PolylineOptions())
                    .add(oldData, newData)
                    .geodesic(true).color(R.color.pace_show));
        }else {
            // 绘制一个大地曲线
            aMap.addPolyline((new PolylineOptions())
                    .add(oldData, newData)
                    .geodesic(true).color(R.color.bg_screen1));
        }


    }

    public void RestartSetMap(List<LatLng> latLngList) {
        for (int i = 0; i < latLngList.size(); i++) {
            aMap.addPolyline((new PolylineOptions())
                    .add(oldLatLng, latLngList.get(i))
                    .geodesic(true).color(R.color.pace_show));
            oldLatLng = latLngList.get(i);
        }
    }



    public String format(int miss) {
        String hh = miss / 3600 > 9 ? miss / 3600 + "" : "0" + miss / 3600;
        String mm = (miss % 3600) / 60 > 9 ? (miss % 3600) / 60 + "" : "0" + (miss % 3600) / 60;
        String ss = (miss % 3600) % 60 > 9 ? (miss % 3600) % 60 + "" : "0" + (miss % 3600) % 60;
        return hh + ":" + mm + ":" + ss;
    }


    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
