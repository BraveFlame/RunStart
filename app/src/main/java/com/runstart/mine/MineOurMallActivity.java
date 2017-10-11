package com.runstart.mine;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;


import com.runstart.R;
import com.runstart.bean.OurMall;
import com.runstart.help.ActivityCollector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouj on 2017-09-21.
 */

public class MineOurMallActivity extends Activity implements View.OnClickListener{
    //定义fragment_mine_ourmall_title.xml布局的组件
    private ImageView iv_zuojiantou;
    //定义fragment_mine_ourmall_content.xml布局的组件
    private ListView ourMallListView;
    //定义ListView的适配器OurMallAdapter
    private OurMallAdapter ourMallAdapter;
    //定义List列表来保存OurMall的数据信息
    private List<OurMall> ourMallList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_mine_ourmall);
        ActivityCollector.addActivity(this);
        getOurMallData();
        initOurMallView();
        useListViewAdapter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    /**
     * 初始化组件
     */
    public void initOurMallView(){
        iv_zuojiantou = (ImageView) findViewById(R.id.mine_ourmall_iv_zuojiantou);
        iv_zuojiantou.setOnClickListener(this);
        ourMallListView = (ListView) findViewById(R.id.mine_ourmall_content_listview);
    }
    /**
     * 初始化OurMall的数据
     */
    public void getOurMallData(){
        ourMallList = new ArrayList<OurMall>();
        OurMall ourMall01=new OurMall();
        ourMall01.setCommodityImage(String.valueOf(R.mipmap.ourmall_shoes));
        ourMall01.setCommodityName("Nike Air Max");
        ourMall01.setCommodityPrice("$155.00");
        ourMallList.add(ourMall01);
        OurMall ourMall02=new OurMall();
        ourMall02.setCommodityImage(String.valueOf(R.mipmap.img_3625));
        ourMall02.setCommodityName("Nike Air Max");
        ourMall02.setCommodityPrice("$156.00");
        ourMallList.add(ourMall02);
        OurMall ourMall03=new OurMall();
        ourMall03.setCommodityImage(String.valueOf(R.mipmap.img_3625_copy));
        ourMall03.setCommodityName("Nike Air Max");
        ourMall03.setCommodityPrice("$157.00");
        ourMallList.add(ourMall03);

    }
    /**
     * 使用ListView的适配器
     */
    public void useListViewAdapter(){
        ourMallAdapter=new OurMallAdapter(MineOurMallActivity.this);
        ourMallAdapter.setOurMallList(ourMallList);
        ourMallListView.setAdapter(ourMallAdapter);
    }
    /**
     * OnClickListener事件监听的总方法
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mine_ourmall_iv_zuojiantou:
                MineOurMallActivity.this.finish();
                break;
            default:
                break;
        }
    }
}
