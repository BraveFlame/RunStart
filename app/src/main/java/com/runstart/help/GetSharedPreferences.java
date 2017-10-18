package com.runstart.help;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.runstart.BmobBean.User;

/**
 * Created by user on 17-10-14.
 */

public class GetSharedPreferences {
    private static GetSharedPreferences pref;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private GetSharedPreferences(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor=preferences.edit();
    }


    public synchronized static GetSharedPreferences getPref(Context context) {
        if (pref == null) {
            pref = new GetSharedPreferences(context);
        }
        return pref;
    }

    public void getUser(User user) {
        if(user==null)
            return;
        user.setNickName(preferences.getString("name","no"));
        user.setMailBox(preferences.getString("mail","12345@qq.com"));
        user.setLocation(preferences.getString("location","no"));
        user.setHeaderImageUri(preferences.getString("headImg",null));
        user.setPhoneNumber(preferences.getString("phone","12345"));
        user.setPassword(preferences.getString("password","12345"));
        user.setObjectId(preferences.getString("userObjectId","12345"));
        user.setLikeNumberForHistory(preferences.getInt("allpointgreat",0));


    }

    public void saveUser(User user){
        editor.putString("name",user.getNickName());
        editor.putString("userObjectId", user.getObjectId());
        editor.putString("phone", user.getPhoneNumber());
        editor.putString("password", user.getPassword());
        editor.putString("location",user.getLocation());
        editor.putString("headImg",user.getHeaderImageUri());
        editor.putString("mail",user.getMailBox());
        editor.putInt("allpointgreat",user.getLikeNumberForHistory());
        editor.putString("joinDay",user.getCreatedAt());


        editor.putInt("ride_day_kcal",user.getRideKcal());
        editor.putInt("ride_day_distance",user.getRideDistance());
        editor.putInt("ride_day_time",user.getRideTime());


        editor.putInt("run_day_kcal",user.getRunKcal());
        editor.putInt("run_day_distance",user.getRunDistance());
        editor.putInt("run_day_time",user.getRunTime());


        editor.putInt("walk_day_kcal",user.getWalkKcal());
        editor.putInt("walk_day_distance",user.getWalkDistance());
        editor.putInt("walk_day_time",user.getWalkTime());
        editor.commit();




    }
}
