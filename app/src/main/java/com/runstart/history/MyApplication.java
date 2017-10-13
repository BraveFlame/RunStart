package com.runstart.history;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;


import com.runstart.sport_fragment.FragmentWalkSecondPage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;


import cn.bmob.v3.Bmob;

/**
 * Created by 10605 on 2017/9/8.
 */

public class MyApplication extends Application {

    private final static String applicationID = "6bbf7fb6372e29f54eead6a98a204621";
    public static final String userObjectIdKey = "userObjectId";
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
            Bmob.initialize(this, applicationID);        }

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

//////////////////////毕小福//////////////////////////////////
    private ProgressDialog progressDialog;
    public void showProgressDialog(Activity activity){
        progressDialog=new ProgressDialog(activity);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("正在获取数据...");
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                myhandler.sendEmptyMessage(11);
            }
        }).start();
    }

    public void stopProgressDialog(){
        myhandler.sendEmptyMessage(11);
    }

    Handler myhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDialog.cancel();
        }
    };

    public boolean isFragmentWalkShouldRefresh=false;
    public FragmentWalkSecondPage fragmentWalkSecondPage;
}
