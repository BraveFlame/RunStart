package com.runstart.sport_map;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.runstart.R;

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
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);

        if (v.getId() == sport_end_btn) {
            titleText.setText("恭喜您完成本次步行！");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        saveDate();
    }
}