package com.runstart.help;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.runstart.R;

/**
 * Created by user on 17-10-13.
 */

public class GetPushFromBmob extends Activity {
    private TextView textView;
    private String remain;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bmob_push_layout);
        textView = (TextView) findViewById(R.id.bmob_remain);
        remain = getIntent().getStringExtra("remain");
        textView.setText(remain);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(9);
        final String version;
        if (remain.length() > 22) {
            version = remain.substring(11, 22);
        } else {
            version = "";
        }
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alter = new AlertDialog.Builder(GetPushFromBmob.this);
                alter.setTitle("更新")
                        .setMessage("是否联网下载最新版本" + version)
                        .setPositiveButton("下载", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //下载逻辑
                                downRunStartAPP();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
        });
    }

    public void downRunStartAPP() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
