package com.runstart.mine;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.runstart.R;
import com.runstart.help.ActivityCollector;


/**
 * Created by zhouj on 2017-10-11.
 */

public class MineRelatedUsActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_mine_setup_relatedus);
        ActivityCollector.addActivity(this);
        feedbackInitView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
    /**
     * 初始化组件
     */
    public void feedbackInitView(){
        ImageView iv_zuojiantou = (ImageView) findViewById(R.id.mine_setup_aboutus_iv_zuojiantou);
        iv_zuojiantou.setOnClickListener(this);

    }

    /**
     * onClickListener事件监听的总方法
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_setup_aboutus_iv_zuojiantou:
                MineRelatedUsActivity.this.finish();
                break;
            default:
                break;
        }
    }
}
