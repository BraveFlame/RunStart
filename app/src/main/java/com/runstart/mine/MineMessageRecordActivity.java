package com.runstart.mine;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;


import com.runstart.R;
import com.runstart.bean.MessageRecord;
import com.runstart.help.ActivityCollector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouj on 2017-09-21.
 */

public class MineMessageRecordActivity extends Activity implements View.OnClickListener{
    //定义fragment_mine_messagerecord_title.xml的布局组件
    private ImageView iv_zuojiantou;
    //定义fragment_mine_messagerecord_content.xml的布局组件
    private ListView meRecordListView;
    //定义ListView的适配器meRecordAdapter
    private MeRecordAdapter meRecordAdapter;
    //定义List列表来保存MessageRecord的相应数据信息
    private List<MessageRecord> messageRecordList=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_mine_messagerecord);
        ActivityCollector.addActivity(this);
        getMessageRecordData();
        initMeRecordView();
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
    public void initMeRecordView(){
        iv_zuojiantou = (ImageView) findViewById(R.id.mine_messagerecord_iv_zuojiantou);
        iv_zuojiantou.setOnClickListener(this);
        meRecordListView = (ListView) findViewById(R.id.mine_messagerecord_listview);
    }
    /**
     * 初始化MessageRecord的相应数据
     */
    public void getMessageRecordData(){
        messageRecordList = new ArrayList<MessageRecord>();
        MessageRecord messageRecord01=new MessageRecord();
        messageRecord01.setUserImage(String.valueOf(R.mipmap.arvin_febry_302935_copy3));
        messageRecord01.setUserName("chen.zhou");
        messageRecord01.setUserMessage("哈哈");
        messageRecord01.setUserTime("08:09:12");
        messageRecordList.add(messageRecord01);
        MessageRecord messageRecord02=new MessageRecord();
        messageRecord02.setUserImage(String.valueOf(R.mipmap.arvin_febry_302935_copy3));
        messageRecord02.setUserName("chen.zhou");
        messageRecord02.setUserMessage("哈哈");
        messageRecord02.setUserTime("08:09:13");
        messageRecordList.add(messageRecord02);
    }
    /**
     * 提供给listView适配器
     */
    public void useListViewAdapter(){
        meRecordAdapter = new MeRecordAdapter(MineMessageRecordActivity.this);
        meRecordAdapter.setMeRecordList(messageRecordList);
        meRecordListView.setAdapter(meRecordAdapter);
    }
    /**
     * onClickListener事件监听的总方法
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mine_messagerecord_iv_zuojiantou:
                MineMessageRecordActivity.this.finish();
                break;
            default:
                break;
        }
    }
}
