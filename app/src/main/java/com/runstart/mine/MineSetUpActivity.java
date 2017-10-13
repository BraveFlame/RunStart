package com.runstart.mine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.runstart.R;
import com.runstart.help.ActivityCollector;


/**
 * Created by zhouj on 2017-09-21.
 */

public class MineSetUpActivity extends Activity implements View.OnClickListener{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_mine_setup);
        ActivityCollector.addActivity(this);
        initSetUpView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    /**
     *初始化fragment_mine_setup_content.xml的布局组件
     */
    private void initSetUpView() {
        //初始化fragment_mine_setup_title.xml的布局组件
        ImageView iv_zuojiantou = (ImageView) findViewById(R.id.mine_setup_iv_zuojiantou);
        iv_zuojiantou.setOnClickListener(this);
        RelativeLayout rl_userguide = (RelativeLayout) findViewById(R.id.mine_setup_content_rl_first);
        rl_userguide.setOnClickListener(this);
        RelativeLayout rl_feedback = (RelativeLayout) findViewById(R.id.mine_setup_content_rl_second);
        rl_feedback.setOnClickListener(this);
        RelativeLayout rl_givepraise = (RelativeLayout) findViewById(R.id.mine_setup_content_rl_three);
        rl_givepraise.setOnClickListener(this);
        RelativeLayout rl_aboutus = (RelativeLayout) findViewById(R.id.mine_setup_content_rl_four);
        rl_aboutus.setOnClickListener(this);
        Button btn_logout = (Button) findViewById(R.id.mine_setup_content_btn_logout);
        btn_logout.setOnClickListener(this);
    }

    /**
     * 使用OnClickListener事件监听的总方法
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_setup_iv_zuojiantou:
                MineSetUpActivity.this.finish();
                break;
            case R.id.mine_setup_content_rl_first:
                Intent relatedIntent = new Intent(MineSetUpActivity.this,MineRelatedUsActivity.class);
                startActivity(relatedIntent);
                break;
            case R.id.mine_setup_content_rl_second:
                Intent feedbackIntent = new Intent(MineSetUpActivity.this, MineFeedbackActivity.class);
                startActivity(feedbackIntent);
                break;
            case R.id.mine_setup_content_rl_three:
                Toast.makeText(this, "The product is not on the line, please wait!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.mine_setup_content_rl_four:
                Intent aboutusIntent = new Intent(MineSetUpActivity.this, MineAboutUsActivity.class);
                startActivity(aboutusIntent);
                break;
            case R.id.mine_setup_content_btn_logout:
                ActivityCollector.finishAll();
            default:
                break;
        }
    }
}
