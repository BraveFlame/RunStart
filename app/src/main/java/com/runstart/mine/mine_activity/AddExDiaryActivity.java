package com.runstart.mine.mine_activity;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.runstart.R;
import com.runstart.bean.ExerciseDiary;
import com.runstart.help.ActivityCollector;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by zhouj on 2017-09-30.
 */

public class AddExDiaryActivity extends Activity implements View.OnClickListener{
    //定义fragment_mine_exercisediary_addexdiary.xml的布局组件
    private ImageView iv_zuojiantou;
    private EditText et_exdiarytitle;
    private EditText et_exdiarycontent;
    private TextView tv_exDiaryDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_mine_exercisediary_addexdiary);
        ActivityCollector.addActivity(this);
        initAddExDiaryView();
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
     * 初始化fragment_mine_exercisediary_addexdiary.xml的布局组件
     */
    public void initAddExDiaryView(){
        iv_zuojiantou = (ImageView) findViewById(R.id.mine_exercisediary_addexdiary_iv_zuojiantou);
        iv_zuojiantou.setOnClickListener(this);
        et_exdiarytitle = (EditText) findViewById(R.id.mine_exercisediary_addexdiary_exdiarytitle);
        et_exdiarycontent = (EditText) findViewById(R.id.mine_exercisediary_addexdiary_exdiarycontent);
        tv_exDiaryDate = (TextView) findViewById(R.id.mine_exercisediary_addexdiary_exdiarydate);
        Button btn_create = (Button) findViewById(R.id.mine_exercisediary_addexdiary_btn_create);
        btn_create.setOnClickListener(this);

    }

    /**
     * 设置OnClickListener事件监听的总方法
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //标题栏左箭头退回
            case R.id.mine_exercisediary_addexdiary_iv_zuojiantou:
                AddExDiaryActivity.this.finish();


                break;
            //点击button按钮将数据提交到数据库
            case R.id.mine_exercisediary_addexdiary_btn_create:
                addExDiaryData();
                break;
            default:
                break;
        }
    }

    /**
     * 将ExerciseDiary的数据提交到数据库中
     */
    private void addExDiaryData() {
        ExerciseDiary exerciseDiary = new ExerciseDiary();
        exerciseDiary.setUserObjectId(PreferenceManager.getDefaultSharedPreferences(this).getString("userObjectId","0101"));
        exerciseDiary.setExDairyTitle(et_exdiarytitle.getText().toString());
        exerciseDiary.setExDairyContent(et_exdiarycontent.getText().toString());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        exerciseDiary.setExDairyDate(dateFormat.format(new Date()));
        exerciseDiary.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if(e==null){
                    Toast.makeText(AddExDiaryActivity.this,"创建数据成功：" + objectId,Toast.LENGTH_SHORT).show();
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
        et_exdiarytitle.setText(null);
        et_exdiarycontent.setText(null);
        et_exdiarytitle.requestFocus();


    }
}
