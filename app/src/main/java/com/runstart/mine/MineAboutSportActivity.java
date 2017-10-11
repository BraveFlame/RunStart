package com.runstart.mine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;


import com.runstart.R;
import com.runstart.bean.AboutSport;
import com.runstart.help.ActivityCollector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouj on 2017-09-21.
 */

public class MineAboutSportActivity extends Activity implements View.OnClickListener{
    //定义fragment_mine_aboutsport_title.xml的布局组件
    private ImageView iv_zuojiantou;
    //定义fragment_mine_aboutsport_content.xml的布局组件
    private ListView abSportListView;
    //定义ListView的适配器AboutSportAdapter
    AboutSportAdapter aboutSportAdapter;
    //定义List列表来保存AboutSport的相关数据信息
    List<AboutSport> aboutSportList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_mine_aboutsport);
        ActivityCollector.addActivity(this);
        getAboutSportData();
        initAboutSportView();
        useListViewApdater();
        useListViewMethod();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    /**
     * 初始化组件
     */
    public void initAboutSportView(){
        iv_zuojiantou = (ImageView) findViewById(R.id.mine_aboutsport_iv_zuojiantou);
        iv_zuojiantou.setOnClickListener(this);
        abSportListView = (ListView) findViewById(R.id.mine_aboutsport_content_listview);
    }

    /**
     * 定义AboutSport的相关数据
     */
    public void getAboutSportData(){
        aboutSportList = new ArrayList<AboutSport>();
        AboutSport aboutSport01=new AboutSport();
        aboutSport01.setNewsImage(String.valueOf(R.mipmap.annie_spratt_1338721));
        aboutSport01.setNewsTitle("Want to lose weight is the first step…");
        aboutSport01.setNewsContent("① increase cardiopulmonary function, long-term perspective, can reduce the risk of cardiovascular disease.\n" +
                "② is the most effective way to control weight, and can control the amount of calorie intake.\n" +
                "③ participation of sports courses for social opportunities.\n" +
                "④ help to improve the body, because the movement can adjust the relaxation of the skin, and reduce the fat content, so you have a healthy feeling.\n" +
                "⑤ help to eliminate the spirit of tension and stress.\n" +
                "⑥ help to reduce the phenomenon of aging, such as high blood pressure which is an important factor leading to heart disease, diabetes and osteoporosis\n" +
                "In fact, you are talking about physical exercise, in fact, you have this issue I have aroused great attention, in particular, to start from small sports or physical exercise it.");
        aboutSportList.add(aboutSport01);
        AboutSport aboutSport02=new AboutSport();
        aboutSport02.setNewsImage(String.valueOf(R.mipmap.annie_spratt_133872_2));
        aboutSport02.setNewsTitle("Want to lose weight is the first step…");
        aboutSport02.setNewsContent("① increase cardiopulmonary function, long-term perspective, can reduce the risk of cardiovascular disease.\n" +
                "② is the most effective way to control weight, and can control the amount of calorie intake.\n" +
                "③ participation of sports courses for social opportunities.\n" +
                "④ help to improve the body, because the movement can adjust the relaxation of the skin, and reduce the fat content, so you have a healthy feeling.\n" +
                "⑤ help to eliminate the spirit of tension and stress.\n" +
                "⑥ help to reduce the phenomenon of aging, such as high blood pressure which is an important factor leading to heart disease, diabetes and osteoporosis\n" +
                "In fact, you are talking about physical exercise, in fact, you have this issue I have aroused great attention, in particular, to start from small sports or physical exercise it.");
        aboutSportList.add(aboutSport02);
        AboutSport aboutSport03=new AboutSport();
        aboutSport03.setNewsImage(String.valueOf(R.mipmap.annie_spratt_133872_3));
        aboutSport03.setNewsTitle("Want to lose weight is the first step…");
        aboutSport03.setNewsContent("Want to lose weight is the first...");
        aboutSportList.add(aboutSport03);
    }
    /**
     * 使用ListView的适配器
     */
    public  void useListViewApdater(){
        aboutSportAdapter = new AboutSportAdapter(MineAboutSportActivity.this);
        aboutSportAdapter.setAboutSportList(aboutSportList);
        abSportListView.setAdapter(aboutSportAdapter);
    }
    /**
     * 使用ListView的事件监听
     */
    public void useListViewMethod(){
        abSportListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //创建一个Buddle对象
                Bundle bundle = new Bundle();
                AboutSport aboutSport = aboutSportList.get(position);
                bundle.putSerializable("aboutSport",aboutSport);
                Intent abSportNewIntent = new Intent(MineAboutSportActivity.this, AboutSportNewsActivity.class);
                abSportNewIntent.putExtras(bundle);
                startActivity(abSportNewIntent);
            }
        });
    }
    /**
     * 定义OnClickListener的事件监听总方法
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mine_aboutsport_iv_zuojiantou:
                MineAboutSportActivity.this.finish();
                break;
            default:
                break;
        }
    }
}
