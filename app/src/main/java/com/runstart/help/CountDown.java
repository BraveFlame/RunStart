package com.runstart.help;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.runstart.R;
import com.runstart.sport_map.PacingActivity;
import com.runstart.sport_map.RidingActivity;
import com.runstart.sport_map.RunningActivity;


/**
 * Created by user on 17-9-14.
 */

public class CountDown extends Activity {

    private Button startRightAway;
    private TextView backwardsText;
    private volatile int i = 3;
    private Intent intent;
    private String whichActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.downcount_layout);
        backwardsText = (TextView) findViewById(R.id._321);
        startRightAway = (Button) findViewById(R.id.start_right_away);
        whichActivity = getIntent().getStringExtra("activity");
        getActivityIntent();
        timePrepare();
        startRightAway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("activity",whichActivity);
                startActivity(intent);
                i=-2;
                finish();
            }
        });

    }

    public void getActivityIntent() {


        switch (whichActivity) {
            case "running":
                intent = new Intent(CountDown.this, RunningActivity.class);
                break;
            case "pacing":
                intent = new Intent(CountDown.this, PacingActivity.class);
                break;
            case "riding":
                intent = new Intent(CountDown.this, RidingActivity.class);
                break;
            default:
                break;
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:
                    if (i > 0)
                        backwardsText.setText("" + i);
                    else if(i==0) {
                        backwardsText.setText("GO");
                    }else if(i==-1){
                        intent.putExtra("activity",whichActivity);
                        startActivity(intent);
                        finish();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public synchronized void timePrepare() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (i >= -1) {
                    try {
                        Thread.sleep(1000);
                        i--;
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                i = 3;
            }
        }).start();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler = null;
        finish();
    }
}
