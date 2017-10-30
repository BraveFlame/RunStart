package com.runstart.sports.sport_activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.runstart.R;
import com.runstart.sports.sport_service.RideService;

import static com.runstart.R.id.sport_end_btn;

/**
 * Created by zhonghao.song on 17-9-9.
 */

public class RidingActivity extends SportingActivity {
    private Button endBtn;
    private TextView titleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        intent = new Intent(RidingActivity.this, RideService.class);
        super.onCreate(savedInstanceState);
        endBtn = (Button) findViewById(R.id.sport_end_btn);
        endBtn.setOnClickListener(this);
        titleText = (TextView) findViewById(R.id.sporting_status_text);
        titleText.setText("Riding...");

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
    }
}