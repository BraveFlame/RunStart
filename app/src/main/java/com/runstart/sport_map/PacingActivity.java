package com.runstart.sport_map;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.runstart.R;
import com.runstart.help.GetSHA1;

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
    }
}