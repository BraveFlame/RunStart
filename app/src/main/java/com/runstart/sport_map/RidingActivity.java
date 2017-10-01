package com.runstart.sport_map;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.runstart.BmobBean.DaySport;
import com.runstart.BmobBean.User;
import com.runstart.R;
import com.runstart.help.ToastShow;
import com.runstart.history.MyApplication;

import java.util.Calendar;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;

import static com.runstart.R.id.sport_end_btn;

/**
 * Created by user on 17-9-9.
 */

public class RidingActivity extends SportingActivity {
    private Button endBtn;
    private TextView titleText;
    private String a;

    private Calendar c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        intent = new Intent(RidingActivity.this, RideService.class);
        super.onCreate(savedInstanceState);
        endBtn = (Button) findViewById(R.id.sport_end_btn);
        endBtn.setOnClickListener(this);
        titleText = (TextView) findViewById(R.id.sporting_status_text);
        titleText.setText("Riding...");

    }


    public void saveDate() {
        String v, s;
        float t;
        v = decimalFormat.format(distances * 3600 / 1000 / miss);
        s = decimalFormat.format(distances / 1000);
        t = Float.valueOf(s) + Float.valueOf(preferences.getString("all_ride_distance", "0"));
        a = decimalFormat.format(t);
        editor.putString("last_ride_distance", s);
        editor.putString("last_ride_speed", v);
        editor.putString("all_ride_distance", a);
        editor.commit();
        final Float mno = 55.0f * miss * 30 / (400 * miss / distances / 60) / 3600;
        c = Calendar.getInstance();
        MyApplication myApplication =
                (MyApplication) getApplication();
        myApplication.nowDB.insert(new String[]{}, new double[]{12306, c.get(c.MONTH), c.get(Calendar.WEEK_OF_YEAR), c.get(Calendar.DAY_OF_MONTH), Float.valueOf(a) * 1000, miss, mno, 2});
        Log.e("Date", "Kcal:" + mno + "  distance:" + Float.valueOf(a) * 1000 + "  time:" + miss);
        Log.e("Date", " " + c.get(c.MONTH) + " " + c.get(Calendar.WEEK_OF_YEAR) + " " + c.get(Calendar.DAY_OF_MONTH));

        BmobQuery<User> query=new BmobQuery<>();

        query.getObject(MyApplication.applicationMap.get("userObjectId"), new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e==null){
                    user.setRideTime(miss*1000);
                    user.setRideKcal((int)(mno*1000));
                    user.setRideDistance((int)(Float.valueOf(a)*1000));
                    user.update();
                    DaySport daySport=new DaySport();
                    daySport.setCal((int)(mno*1000));
                    daySport.setDay(c.get(Calendar.DAY_OF_MONTH));
                    daySport.setMonth(c.get(c.MONTH));
                    daySport.setWeek(c.get(Calendar.WEEK_OF_YEAR));
                    daySport.setType(2);
                    daySport.setTime(miss*1000);
                    daySport.setDistance(distances);
                    daySport.setUserID(Double.valueOf(user.getPhoneNumber()));
                    daySport.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e==null){
                                ToastShow.showToast(RidingActivity.this,"chenggong!!");
                            }
                        }
                    });
                }else {
                    ToastShow.showToast(RidingActivity.this,"获取运动对象失败");
                }
            }
        });




    }


    @Override
    public void onClick(View v) {
        super.onClick(v);

        if (v.getId() == sport_end_btn) {
            titleText.setText("恭喜您完成本次骑行！");
        }

    }

    @Override
    public void onBackPressed() {
        textDialog = "Riding";
        imageDialog = R.mipmap.ic_qixing;
        super.onBackPressed();

    }

    @Override
    protected void onPause() {
        super.onPause();
        saveDate();
    }
}