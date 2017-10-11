package com.runstart.mine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.runstart.R;
import com.runstart.bean.ExerciseDiary;
import com.runstart.help.ActivityCollector;

import org.w3c.dom.Text;

/**
 * Created by zhouj on 2017-09-30.
 */

public class ExerciseDiaryContentActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_mine_exercisediary_content);
        ActivityCollector.addActivity(this);
        initExDiaryContentView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    /**
     * 初始化fragment_mine_exercisediary_content.xml的布局组件
     */
    public void initExDiaryContentView(){
        ImageView iv_zuojiantou = (ImageView) findViewById(R.id.mine_exercisediary_content_iv_zuojiantou);
        iv_zuojiantou.setOnClickListener(this);
        //获取到Intent
        Intent intent=getIntent();
        //直接通过Intent取出它所携带的Bundle数据包中的数据
        ExerciseDiary exerciseDiary=(ExerciseDiary)intent.getSerializableExtra("exerciseDiary");
        TextView tv_exdiarytitle = (TextView) findViewById(R.id.mine_exercisediary_content_exdiarytitle);
        tv_exdiarytitle.setText(exerciseDiary.getExDairyTitle());
        TextView tv_exdiarycontent=(TextView)findViewById(R.id.mine_exercisediary_content_exdiarycontent);
        tv_exdiarycontent.setText(exerciseDiary.getExDairyContent());
        TextView tv_exdiarydate=(TextView)findViewById(R.id.mine_exercisediary_content_exdiarydate);
        tv_exdiarydate.setText(exerciseDiary.getExDairyDate());
    }

    /**
     * OnClickListener事件监听的总方法
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mine_exercisediary_content_iv_zuojiantou:
                ExerciseDiaryContentActivity.this.finish();
                break;
            default:
                break;
        }
    }
}
