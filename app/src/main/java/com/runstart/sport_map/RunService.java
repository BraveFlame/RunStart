package com.runstart.sport_map;

import android.app.PendingIntent;
import android.content.Intent;

import com.runstart.BmobBean.DaySport;
import com.runstart.BmobBean.User;
import com.runstart.help.ToastShow;
import com.runstart.MyApplication;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;

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

    @Override
    public void saveDate() {

        //本次距離，速度，ｋｍ和ｋｍ／ｈ，k卡路里
        final float thisKCal;
        String v, s;
        v = decimalFormat.format(distance * 3600 / 1000 / miss);
        s = decimalFormat.format(distance / 1000);
        thisKCal = 55.0f * miss * 30 / (400 * miss / distance / 60) / 3600;

        //今天的距離和卡路里和時間,之前总距离
        float lastAllDis_F,todayKCal_F,todayDis_F;
        int todayTimeMiss;
        todayKCal_F=Float.valueOf(preferences.getInt("run_day_kcal",0));
        todayDis_F=Float.valueOf(preferences.getInt("run_day_distance",0));
        lastAllDis_F= Float.valueOf(preferences.getInt("all_run_distance", 0));
        todayTimeMiss=preferences.getInt("run_day_time",1);

        //判断是不是当天，是则叠加

        String yesterday=preferences.getString("run_date","2017-10-01");
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        String today=format.format(new Date());
        if(today.equals(yesterday)){
            nowDis_F=todayDis_F+Float.valueOf(s);
            nowTimeMiss=miss+todayTimeMiss;
            nowKCal_F=todayKCal_F+thisKCal;
        }else {
            nowDis_F=Float.valueOf(s);
            nowTimeMiss=miss;
            nowKCal_F=thisKCal;
            editor.putString("run_date",today);
        }
        allDisS = decimalFormat.format( Float.valueOf(s) +lastAllDis_F);
        //三位小数点,当天数据存到bmob
        editor.putInt("run_day_kcal",(int)nowKCal_F);
        editor.putInt("run_day_distance",(int)nowDis_F);
        editor.putInt("run_day_time",nowTimeMiss);

        //最近一次显示在页面
        editor.putString("last_run_distance", s);
        editor.putString("last_run_speed", v);
        double all=Double.valueOf(allDisS);
        editor.putInt("all_run_distance", (int)all);
        editor.commit();
        //"userID","month","week","day","distance","time","cal","type"
        c= Calendar.getInstance();
       final MyApplication myApplication=
                (MyApplication)getApplication();

        //       Log.e("Date","Kcal:"+thisKCal+"  distance:"+Float.valueOf(allDisS)*1000+"  time:"+miss);
//        Log.e("Date"," "+c.get(c.MONTH)+" "+c.get(Calendar.WEEK_OF_YEAR)+" "+c.get(Calendar.DAY_OF_MONTH));

        BmobQuery<User> query=new BmobQuery<>();

        query.getObject(MyApplication.applicationMap.get("userObjectId"), new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e==null){
                    //更新用户当天步行数据,ms，cal和m
                    user.setRunTime(nowTimeMiss);
                    user.setRunKcal((int)(nowKCal_F));
                    user.setRunDistance((int)(nowDis_F*1000));
                    user.update();
                    //新增用户步行数据
                    DaySport daySport=new DaySport();
                    daySport.setCal((int)(thisKCal));
                    daySport.setDay(c.get(Calendar.DAY_OF_MONTH));
                    daySport.setMonth(c.get(c.MONTH));
                    daySport.setWeek(c.get(Calendar.WEEK_OF_YEAR));
                    daySport.setType(1);
                    daySport.setTime(miss);
                    daySport.setDistance(distance);
                    final String userPhone=user.getPhoneNumber();
                    daySport.setUserID(userPhone);
                    daySport.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e==null){
                                myApplication.nowDB.insert(new String[]{userPhone},new double[]{c.get(c.MONTH),
                                        c.get(Calendar.WEEK_OF_YEAR),c.get(Calendar.DAY_OF_MONTH),distance,miss,thisKCal,1});
                                ToastShow.showToast(RunService.this,"successful!!");
                            }else {
                                ToastShow.showToast(RunService.this,"保存失败");
                            }
                        }
                    });
                }else {
                    ToastShow.showToast(RunService.this,"获取运动对象失败");
                }
            }
        });
        super.saveDate();

    }
}
