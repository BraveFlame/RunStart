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

public class MinePInfoSetNameActivity extends Activity implements View.OnClickListener {
    private EditText et_setname;
    private Intent setnameIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        setContentView(R.layout.fragment_mine_personalinformation_setname);
        setNameInitView();
        //获取到Activity的意图
        setnameIntent = getIntent();
        Bundle bundle = setnameIntent.getExtras();
        String newname = bundle.getString("newname");
        et_setname.setText(newname);
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
     * 初始化fragment_mine_personalinformation_setname.xml的布局组件
     */
    public void setNameInitView() {
        ImageView iv_zuojiantou = (ImageView) findViewById(R.id.mine_personalinformation_setname_iv_zuojiantou);
        iv_zuojiantou.setOnClickListener(this);
        iv_zuojiantou.setOnClickListener(this);
        et_setname = (EditText) findViewById(R.id.mine_personalinformation_setname_et_setname);
    }

    /**
     * OnClickListener事件监听的总方法
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_personalinformation_setname_iv_zuojiantou:
                setnameIntent.putExtra("setname", et_setname.getText().toString());
                setResult(11, setnameIntent);
                MinePInfoSetNameActivity.this.finish();
                break;
            default:
                break;
        }
    }
}
