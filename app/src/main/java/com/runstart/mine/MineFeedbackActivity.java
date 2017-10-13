package com.runstart.mine;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.runstart.R;
import com.runstart.help.ActivityCollector;


/**
 * Created by zhouj on 2017-10-11.
 */

public class MineFeedbackActivity extends Activity implements View.OnClickListener{

    private EditText et_feedback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_mine_setup_feedback);
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
        ImageView iv_zuojiantou = (ImageView) findViewById(R.id.mine_setup_feedback_iv_zuojiantou);
        iv_zuojiantou.setOnClickListener(this);
        et_feedback = (EditText)findViewById(R.id.mine_setup_feedback_et_feedbackcontent);
        Button btn_submit= (Button) findViewById(R.id.mine_setup_feedback_btn_submit);
        btn_submit.setOnClickListener(this);

    }

    /**
     * onClickListener事件监听的总方法
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_setup_feedback_iv_zuojiantou:
                MineFeedbackActivity.this.finish();
                break;
            case R.id.mine_setup_feedback_btn_submit:
                if (et_feedback.getText().toString().equals("")){
                    Toast.makeText(this, "Dear users, you have not filled in your suggestions, thank you very much for your feedback!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MineFeedbackActivity.this, "Dear users, your suggestions have been uploaded to our mailbox, thank you very much for your feedback and help!", Toast.LENGTH_SHORT).show();
                    et_feedback.setText("");
                }

            default:
                break;
        }
    }
}
