package com.runstart.history;

import android.app.Application;
import android.util.Log;

import cn.bmob.v3.Bmob;

/**
 * Created by 10605 on 2017/9/8.
 */

public class MyApplication extends Application {

    private final static String applicationID="6bbf7fb6372e29f54eead6a98a204621";
    public NowDB nowDB;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.e("EO",getFilesDir().getPath()+"stu.db");
        Bmob.initialize(this,applicationID);
        nowDB=new NowDB("exerciseData",getFilesDir().getPath()+ "stu.db",
                new String[]{},new String[]{"userID","month","week","day","distance","time","cal","type"});
    }
}
