package com.runstart.slidingpage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.runstart.R;

/**
 * Created by zhouj on 2017-09-13.
 */

public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
   // Context _context;

    int PRIVATE_MODE = 0;

    //SharedPreferences 文件名
    private static final String PREF_NAME = "intro_slider";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    public PrefManager(Context context){
       // this._context = context;
        //pref = _context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        pref= PreferenceManager.getDefaultSharedPreferences(context);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime){
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public void setWeatherIcon(){

        editor.putInt("晴", R.mipmap.taiyan);
        editor.putInt("多云", R.mipmap.duoyun);
        editor.putInt("小雨", R.mipmap.xiaoyu);
        editor.putInt("阴", R.mipmap.yun);
        editor.putInt("阵雨", R.mipmap.zhengyu);
        editor.putInt("中雨", R.mipmap.zhonyu);
        editor.putInt("暴雨", R.mipmap.baoyu);
        editor.putInt("大雨", R.mipmap.dayu);
        editor.putInt("雷阵雨", R.mipmap.leizhengyu);
        editor.putInt("雾", R.mipmap.wu);
        editor.commit();
    }

    public boolean isFirstTimeLaunch(){
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

}