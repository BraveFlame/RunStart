package com.runstart.sport_map;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.runstart.BmobBean.DaySport;
import com.runstart.BmobBean.User;
import com.runstart.R;
import com.runstart.help.GetSHA1;
import com.runstart.help.ToastShow;
import com.runstart.history.ExerciseData;
import com.runstart.history.MyApplication;

import java.util.Calendar;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;

import static com.runstart.R.id.sport_end_btn;

/**
 * Created by zhonghao.song on 2017-09-18.
 * 记步主页
 */
public class PacingActivity extends SportingActivity {
    private Button endBtn;
    private TextView titleText;
    private String a;

    private Calendar c;
//   WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
 //   WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
  //  | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        intent = new Intent(PacingActivity.this, WalkService.class);

        super.onCreate(savedInstanceState);
        endBtn = (Button) findViewById(R.id.sport_end_btn);
        endBtn.setOnClickListener(this);
        titleText = (TextView) findViewById(R.id.sporting_status_text);

        titleText.setText("Walking...");
        Log.e("SHA", GetSHA1.getCertificateSHA1Fingerprint(this));



    }


    public void saveDate() {
        String v, s;
        float t;
        v = decimalFormat.format(distances * 3600 / 1000 / miss);
        s = decimalFormat.format(distances / 1000);
        t = Float.valueOf(s) + Float.valueOf(preferences.getString("all_pace_distance", "0"));
        a = decimalFormat.format(t);

        editor.putString("last_pace_distance", s);
        editor.putString("last_pace_speed", v);
        editor.putString("all_pace_distance", a);
        editor.commit();
        //"userID","month","week","day","distance","time","cal","type"
        final float mno= 55.0f * miss * 30 / (400 * miss / distances / 60) / 3600;
        c=Calendar.getInstance();
        MyApplication myApplication=
        (MyApplication)getApplication();
        myApplication.nowDB.insert(new String[]{},new double[]{12306,c.get(c.MONTH),c.get(Calendar.WEEK_OF_YEAR),c.get(Calendar.DAY_OF_MONTH),Float.valueOf(a)*1000,miss,mno,0});
        Log.e("Date","Kcal:"+mno+"  distance:"+Float.valueOf(a)*1000+"  time:"+miss);
        Log.e("Date"," "+c.get(c.MONTH)+" "+c.get(Calendar.WEEK_OF_YEAR)+" "+c.get(Calendar.DAY_OF_MONTH));

        BmobQuery<User>query=new BmobQuery<>();

        query.getObject(MyApplication.applicationMap.get("userObjectId"), new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e==null){
                    user.setWalkTime(miss*1000);
                    user.setWalkKcal((int)(mno*1000));
                    user.setWalkDistance((int)(Float.valueOf(a)*1000));
                    user.update();
                    DaySport daySport=new DaySport();
                    daySport.setCal((int)(mno*1000));
                    daySport.setDay(c.get(Calendar.DAY_OF_MONTH));
                    daySport.setMonth(c.get(c.MONTH));
                    daySport.setWeek(c.get(Calendar.WEEK_OF_YEAR));
                    daySport.setType(0);
                    daySport.setTime(miss*1000);
                    daySport.setDistance(distances);
                    daySport.setUserID(Double.valueOf(user.getPhoneNumber()));
                    daySport.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e==null){
                                ToastShow.showToast(PacingActivity.this,"chenggong!!");
                            }
                        }
                    });
                }else {
                    ToastShow.showToast(PacingActivity.this,"获取运动对象失败");
                }
            }
        });




    }


    @Override
    public void onClick(View v) {
        super.onClick(v);

        if (v.getId() == sport_end_btn) {
            titleText.setText("恭喜您完成本次步行！");
        }

    }

    @Override
    public void onBackPressed() {
        textDialog="Pacing";
        imageDialog=R.mipmap.ic_buxing;
        super.onBackPressed();

    }
    @Override
    protected void onPause() {
        super.onPause();
        saveDate();
    }
}