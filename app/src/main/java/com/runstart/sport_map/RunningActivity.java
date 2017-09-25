package com.runstart.sport_map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.runstart.R;

import static com.runstart.R.id.sport_end_btn;

/**
 * Created by user on 17-9-8.
 */

public class RunningActivity extends SportingActivity {
    private Button endBtn;
    private TextView titleText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        intent=new Intent(RunningActivity.this,RunService.class);
        super.onCreate(savedInstanceState);
        endBtn=(Button)findViewById(R.id.sport_end_btn);
        titleText=(TextView)findViewById(R.id.sporting_status_text);
        titleText.setText("Running...");
        endBtn.setOnClickListener(this);
    }

    public void saveDate() {
        String v, s, a;
        float t;
        v = decimalFormat.format(distances * 3600 / 1000 / miss);
        s = decimalFormat.format(distances / 1000);
            t = Float.valueOf(s) + Float.valueOf(preferences.getString("all_run_distance", "0"));
            a = decimalFormat.format(t);
            editor.putString("last_run_distance", s);
            editor.putString("last_run_speed", v);
            editor.putString("all_run_distance", a);
        editor.commit();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);


            if(v.getId()==sport_end_btn){
                titleText.setText("恭喜您完成本次跑步！");
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        saveDate();
    }
}