package com.runstart.help;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.LocationSource;


/**
 * Created by user on 17-9-19.
 */

public interface GetLocationData {
    void updateTime(int time,float distance);
    void updateLL(AMapLocation location,LocationSource.OnLocationChangedListener mListener);
}
