package com.runstart.mine.mine_activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;


import com.runstart.R;
import com.runstart.bean.OurMall;
import com.runstart.help.ActivityCollector;
import com.runstart.mine.mine_adapter.OurMallAdapter;

import java.util.ArrayList;

/**
 * Created by zhouj on 2017-09-21.
 */

public class MineOurMallActivity extends Activity implements View.OnClickListener {
    //定义fragment_mine_ourmall_title.xml布局的组件
    private ImageView iv_zuojiantou;
    //定义fragment_mine_ourmall_content.xml布局的组件
    private ListView ourMallListView;
    //定义ListView的适配器OurMallAdapter
    private OurMallAdapter ourMallAdapter;
    //定义List列表来保存OurMall的数据信息
    private ArrayList<OurMall> ourMallList;
    private Button btn_jumpbuy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_mine_ourmall);
        ActivityCollector.addActivity(this);
        getOurMallData();
        initOurMallView();
        useListViewAdapter();
        listViewItemListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    /**
     * 初始化组件
     */
    public void initOurMallView() {
        iv_zuojiantou = (ImageView) findViewById(R.id.mine_ourmall_iv_zuojiantou);
        iv_zuojiantou.setOnClickListener(this);
        ourMallListView = (ListView) findViewById(R.id.mine_ourmall_content_listview);
        btn_jumpbuy = (Button) findViewById(R.id.mine_ourmall_listitem_btn_jumpbuy);

    }

    /**
     * 初始化OurMall的数据
     */
    public void getOurMallData() {
        ourMallList = new ArrayList<OurMall>();
        OurMall ourMall01 = new OurMall();
        ourMall01.setCommodityImage(R.mipmap.ourmall_shoes01);
        ourMall01.setCommodityName("Nike Air Max");
        ourMall01.setCommodityPrice("$155.00");
        ourMallList.add(ourMall01);
        OurMall ourMall02 = new OurMall();
        ourMall02.setCommodityImage(R.mipmap.ourmall_shoes02);
        ourMall02.setCommodityName("361 Air Max");
        ourMall02.setCommodityPrice("$60.00");
        ourMallList.add(ourMall02);
        OurMall ourMall03 = new OurMall();
        ourMall03.setCommodityImage(R.mipmap.ourmall_shoes03);
        ourMall03.setCommodityName("Dan Air Max");
        ourMall03.setCommodityPrice("$135.00");
        ourMallList.add(ourMall03);
        OurMall ourMall04 = new OurMall();
        ourMall04.setCommodityImage(R.mipmap.ourmall_shoes04);
        ourMall04.setCommodityName("Adidas Air Max");
        ourMall04.setCommodityPrice("$187.00");
        ourMallList.add(ourMall04);
        OurMall ourMall05 = new OurMall();
        ourMall05.setCommodityImage(R.mipmap.ourmall_shoes05);
        ourMall05.setCommodityName("Ning Air Max");
        ourMall05.setCommodityPrice("$97.00");
        ourMallList.add(ourMall05);

    }

    /**
     * 使用ListView的适配器
     */
    public void useListViewAdapter() {
        ourMallAdapter = new OurMallAdapter(MineOurMallActivity.this,R.layout.mine_ourmall_listitem,ourMallList);
        ourMallListView.setAdapter(ourMallAdapter);

    }

    private void listViewItemListener() {
        ourMallListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Uri uri = Uri.parse("https://nike.tmall.com/?spm=a220o.1000855.1997427721.d4918089.6df2abf1MNw6G7");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                        break;
                    case 1:
                        Uri uri01 = Uri.parse("https://detail.tmall.com/item.htm?spm=a220m.1000858.1000725.1.327f1978ZN5pyO&id=542919014291&skuId=3429048119069&user_id=363607599&cat_id=2&is_b=1&rn=2de67e470ec69f470abe4d260e64e264");
                        Intent intent01 = new Intent(Intent.ACTION_VIEW, uri01);
                        startActivity(intent01);
                        break;
                    case 2:
                        Uri uri02 = Uri.parse("https://qiaodan.tmall.com/?spm=a220o.1000855.1997427721.d4918089.e84a184C6RReA");
                        Intent intent02 = new Intent(Intent.ACTION_VIEW, uri02);
                        startActivity(intent02);
                        break;
                    case 3:
                        Uri uri03 = Uri.parse("https://qiaodan.tmall.com/?spm=a220o.1000855.1997427721.d4918089.e84a184C6RReA");
                        Intent intent03 = new Intent(Intent.ACTION_VIEW, uri03);
                        startActivity(intent03);
                        break;
                    case 4:
                        Uri uri04 = Uri.parse("https://detail.tmall.com/item.htm?spm=a1z10.1-b-s.w5003-15786156679.2.3a430639VUZxnW&id=556844982601&scene=taobao_shop");
                        Intent intent04 = new Intent(Intent.ACTION_VIEW, uri04);
                        startActivity(intent04);
                        break;
                    default:
                        break;

                }
            }
        });
    }

    /**
     * OnClickListener事件监听的总方法
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_ourmall_iv_zuojiantou:
                MineOurMallActivity.this.finish();
                break;
            default:
                break;
        }
    }
}
