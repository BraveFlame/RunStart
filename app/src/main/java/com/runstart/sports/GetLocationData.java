package com.runstart.sports;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.LocationSource;


/**
 * Created by zhonghao.song on 17-9-19.
 * 获取运动时的时间s和距离m以及位置信息
 *
 */

public interface GetLocationData {
    /**
     * 运动的时间s和距离m
     * @param time
     * @param distance
     */
    void updateTime(int time,float distance);

    /**
     * 位置信息，和位置的监听
     * @param location
     * @param mListener
     */
    void updateLL(AMapLocation location,LocationSource.OnLocationChangedListener mListener);
}
