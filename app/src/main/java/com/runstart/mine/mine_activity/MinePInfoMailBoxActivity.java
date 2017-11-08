package com.runstart.mine.mine_activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.runstart.R;
import com.runstart.help.ActivityCollector;

/**
 * Created by zhouj on 2017-10-13.
 */

public class MinePInfoMailBoxActivity extends Activity implements View.OnClickListener {
    private EditText et_mailbox;
    private Intent mailboxIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_mine_personalinformation_mailbox);
        ActivityCollector.addActivity(this);
        mailboxInitView();
        //获取到Activity的意图
        mailboxIntent = getIntent();
        Bundle bundle = mailboxIntent.getExtras();
        String newmaibox = bundle.getString("newmailbox");
        et_mailbox.setText(newmaibox);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    /**
     * 初始化fragment_mine_personalinformation_mailbox.xml的布局组件
     */
    public void mailboxInitView() {
        ImageView iv_zuojiantou = (ImageView) findViewById(R.id.mine_personalinformation_mailbox_iv_zuojiantou);
        iv_zuojiantou.setOnClickListener(this);
        et_mailbox = (EditText) findViewById(R.id.mine_personalinformation_mailbox_et_mailbox);
        et_mailbox.setOnClickListener(this);

    }

    /**
     * OnClickListener事件监听的总方法
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_personalinformation_mailbox_iv_zuojiantou:

                mailboxIntent.putExtra("setmailbox", et_mailbox.getText().toString());
                setResult(13, mailboxIntent);
                MinePInfoMailBoxActivity.this.finish();

                break;
            default:
                break;
        }
    }

}
