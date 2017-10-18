package com.runstart.mine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.runstart.R;
import com.runstart.bean.AboutSport;
import com.runstart.help.ActivityCollector;

import org.w3c.dom.Text;

/**
 * Created by zhouj on 2017-10-09.
 */

public class AboutSportNewsActivity extends Activity implements View.OnClickListener{
    //定义fragment_mine_aboutsport_content_news.xml的布局组件
    ImageView iv_zuojiantou;
    TextView tv_title;
    TextView tv_newstitle;
    ImageView iv_newsImage;
    TextView tv_newscontent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_mine_aboutsport_content_news);
        ActivityCollector.addActivity(this);
        asportNewsInitView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    /**
     * 初始化组件
     */
    public void asportNewsInitView() {
        iv_zuojiantou = (ImageView) findViewById(R.id.mine_aboutsport_content_news_iv_zuojiantou);
        iv_zuojiantou.setOnClickListener(this);
        //获取到Intent
        Intent intent = getIntent();
        //直接通过Intent取出它所携带的Bundle数据包中的数据
        AboutSport aboutSport = (AboutSport) intent.getSerializableExtra("aboutSport");
        tv_title = (TextView) findViewById(R.id.mine_aboutsport_content_news_tv_title);
        tv_title.setText(aboutSport.getNewsTitle());
        tv_newstitle = (TextView) findViewById(R.id.mine_aboutsport_content_news_tv_newstitle);
        tv_newstitle.setText(aboutSport.getNewsTitle());
        iv_newsImage = (ImageView) findViewById(R.id.mine_aboutsport_content_news_iv_newsImage);
        iv_newsImage.setImageResource(Integer.parseInt(aboutSport.getNewsBgImage()));
        tv_newscontent = (TextView) findViewById(R.id.mine_aboutsport_content_news_tv_newscontent);
        tv_newscontent.setText(aboutSport.getNewsContent());
    }

    /**
     * 设置OnClickListener事件监听的总方法
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_aboutsport_content_news_iv_zuojiantou:
                AboutSportNewsActivity.this.finish();
                break;
            default:
                break;
        }
    }
}
