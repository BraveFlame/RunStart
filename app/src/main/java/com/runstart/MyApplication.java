package com.runstart;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;

import com.runstart.history.NowDB;
import com.runstart.sport_fragment.FragmentRideFirstPage;
import com.runstart.sport_fragment.FragmentRunFirstPage;
import com.runstart.sport_fragment.FragmentWalkFirstPage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobInstallationManager;
import cn.bmob.v3.InstallationListener;
import cn.bmob.v3.exception.BmobException;

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

        //注册bmob key

        Bmob.initialize(this, applicationID);

        //bmob推送
        BmobInstallationManager.getInstance().initialize(new InstallationListener<BmobInstallation>() {
            @Override
            public void done(BmobInstallation bmobInstallation, BmobException e) {

                if(e==null){
                    Log.e("bmob",bmobInstallation.getObjectId()+"-"+bmobInstallation.getInstallationId());
                }else {
                    Log.e("bmob",e.getMessage());
                }
            }
        });
        try {
            BmobPush.startWork(this);

        }catch (Exception e){
            Log.e("bmob","推送异常"+e.getMessage());
        }


        //存放bmob活动，好友照片
        File imageCacheFile = new File(Environment.getExternalStorageDirectory() + File.separator + "lovesportimage");
        if (!imageCacheFile.exists()) {
            imageCacheFile.mkdir();
        }
        nowDB = new NowDB("exerciseData", getFilesDir().getPath() + "stu.db",
                new String[]{"userID"}, new String[]{"month", "week", "day", "distance", "time", "cal", "type"});
    }

//    public static String getMyProcessName() {
//        try {
//            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
//            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
//            String processName = mBufferedReader.readLine().trim();
//            mBufferedReader.close();
//            return processName;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }


    //////////////////////毕小福//////////////////////////////////
    private ProgressDialog progressDialog;
    public void showProgressDialog(Activity activity){
        if(progressDialog!=null&&progressDialog.isShowing())
            progressDialog.cancel();
            progressDialog = new ProgressDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Getting the data...");
            progressDialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000);
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
           if (progressDialog.isShowing()){
                progressDialog.cancel();
            }
        }
    };

    public boolean isFragmentWalkShouldRefresh=false;
    public FragmentWalkFirstPage fragmentWalkFirstPage;
    public FragmentRunFirstPage fragmentRunFirstPage;
    public FragmentRideFirstPage fragmentRideFirstPage;
    public List listToShow=new ArrayList();
}