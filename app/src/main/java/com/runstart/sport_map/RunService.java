package com.runstart.sport_map;

import android.app.PendingIntent;
import android.content.Intent;

public class RunService extends ServiceLocation {

    @Override
    public void updateNotify() {
        Intent hangIntent = new Intent(this, RunningActivity.class);
        PendingIntent hangPendingIntent = PendingIntent.getActivity(this, 0, hangIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentText("Running  " + distance + " m" + "  " + format(miss))
                .setContentIntent(hangPendingIntent)
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示
                .build();
        super.updateNotify();
    }
    @Override
    void initBroadcastReceiver() {
        lockscreenIntent=new Intent(this,RunningActivity.class);
        lockscreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        super.initBroadcastReceiver();
    }
}
