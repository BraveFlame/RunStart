package com.runstart.sport_map;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.runstart.R;
import com.runstart.help.GetSHA1;
import com.runstart.history.MyApplication;

import java.util.Calendar;

import static com.runstart.R.id.sport_end_btn;

/**
 * Created by zhonghao.song on 2017-09-18.
 * 记步主页
 */
public class PacingActivity extends SportingActivity {
    private Button endBtn;
    private TextView titleText;

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
        String v, s, a;
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
        float mno= 55.0f * miss * 30 / (400 * miss / distances / 60) / 3600;
        Calendar c=Calendar.getInstance();
        MyApplication myApplication=
        (MyApplication)getApplication();
        myApplication.nowDB.insert(new String[]{},new double[]{12306,c.get(c.MONTH),c.get(Calendar.WEEK_OF_YEAR),c.get(Calendar.DAY_OF_MONTH),Float.valueOf(a)*1000,miss,mno,0});
        Log.e("Date","Kcal:"+mno+"  distance:"+Float.valueOf(a)*1000+"  time:"+miss);
        Log.e("Date"," "+c.get(c.MONTH)+" "+c.get(Calendar.WEEK_OF_YEAR)+" "+c.get(Calendar.DAY_OF_MONTH));

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