package com.runstart.history;

import android.app.Application;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;

import com.runstart.friend.FriendMsgHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import cn.bmob.newim.BmobIM;
import cn.bmob.v3.Bmob;

/**
 * Created by 10605 on 2017/9/8.
 */

public class MyApplication extends Application {

    private final static String applicationID = "6bbf7fb6372e29f54eead6a98a204621";
    public NowDB nowDB;
    public static Map<String, String> applicationMap = new HashMap<>();

    @Override
    public void onCreate() {

        super.onCreate();
        //拍照和相册
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }


        if (getApplicationInfo().packageName.equals(getMyProcessName())) {
            Bmob.initialize(this, applicationID);
           // BmobIM.registerDefaultMessageHandler(new FriendMsgHandler());
        }

        File imageCacheFile = new File(Environment.getExternalStorageDirectory() + File.separator + "lovesportimage");
        if (! imageCacheFile.exists()){
            imageCacheFile.mkdir();
        }
        nowDB = new NowDB("exerciseData", getFilesDir().getPath() + "stu.db",
                new String[]{}, new String[]{"userID", "month", "week", "day", "distance", "time", "cal", "type"});
    }

    public static String getMyProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }




}
