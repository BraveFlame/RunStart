package com.runstart.sports.sport_activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.runstart.R;
import com.runstart.sports.sport_service.RunService;

import static com.runstart.R.id.sport_end_btn;

/**
 * Created by zhonghao.song on 17-9-8.
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



    @Override
    public void onClick(View v) {
        super.onClick(v);


            if(v.getId()==sport_end_btn){
                titleText.setText("恭喜您完成本次跑步！");
        }


    }

    @Override
    public void onBackPressed() {
        textDialog="Running";
        imageDialog=R.mipmap.ic_paobu;
        super.onBackPressed();

    }
    @Override
    protected void onPause() {
        super.onPause();

    }
}