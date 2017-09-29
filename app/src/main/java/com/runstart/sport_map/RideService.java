package com.runstart.sport_map;

import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by user on 17-9-22.
 */

public class RideService extends ServiceLocation {
    @Override
    public void updateNotify() {
        Intent hangIntent = new Intent(this, RidingActivity.class);
        PendingIntent hangPendingIntent = PendingIntent.getActivity(this, 0, hangIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentText("Riding  " + distance + " m" + "  " + format(miss))
                .setContentIntent(hangPendingIntent)
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示
                .build();
        super.updateNotify();
    }

    @Override
    void initBroadcastReceiver() {
        lockscreenIntent=new Intent(this,RidingActivity.class);
        lockscreenIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        super.initBroadcastReceiver();
    }
}
