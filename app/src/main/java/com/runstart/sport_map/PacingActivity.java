package com.runstart.sport_map;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.runstart.R;
import com.runstart.help.GetSHA1;
import com.runstart.history.MyApplication;

import static com.runstart.R.id.sport_end_btn;

/**
 * 记步主页
 */
public class PacingActivity extends SportingActivity {
    private Button endBtn;
    private TextView titleText;
    private WalkService walkService=new WalkService();



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
        float mno = 55.0f * miss * 30 / (400 * miss / distances / 60) / 3600;
        //"userID","month","week","day","distance","time","cal","type"
        MyApplication myApplication=
        (MyApplication)getApplication();
        myApplication.nowDB.insert(new String[]{},new double[]{12306,9,40,3,Float.valueOf(a),miss,mno,0});
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