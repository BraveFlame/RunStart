package com.runstart.mine;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.runstart.R;
import com.runstart.help.ActivityCollector;
import com.runstart.help.ToastShow;
import com.runstart.slidingpage.PrefManager;

import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.PushListener;


/**
 * Created by zhouj on 2017-10-11.
 */

public class MineFeedbackActivity extends Activity implements View.OnClickListener {

    private EditText et_feedback;
    BmobPushManager bmobPushManager = new BmobPushManager();
    SharedPreferences preferences;
    private String[] phone = new String[]{"18814126594"};
    private String inputContent;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_mine_setup_feedback);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
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
    public void feedbackInitView() {
        ImageView iv_zuojiantou = (ImageView) findViewById(R.id.mine_setup_feedback_iv_zuojiantou);
        iv_zuojiantou.setOnClickListener(this);
        et_feedback = (EditText) findViewById(R.id.mine_setup_feedback_et_feedbackcontent);
        Button btn_submit = (Button) findViewById(R.id.mine_setup_feedback_btn_submit);
        btn_submit.setOnClickListener(this);

    }

    /**
     * onClickListener事件监听的总方法
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_setup_feedback_iv_zuojiantou:
                MineFeedbackActivity.this.finish();
                break;
            case R.id.mine_setup_feedback_btn_submit:
                inputContent = et_feedback.getText().toString();
                if (phone[0].equals(preferences.getString("phone", "12345")))
                    if (inputContent.length() > 22 && inputContent.contains("rs.v5_")) {
                        pushtoBmob();
                        return;
                    }
                if (inputContent.equals("")) {
                    ToastShow.showToast(MineFeedbackActivity.this, "Dear users, you have not filled in your suggestions, thank you very much for your feedback!");
                } else {
                    ToastShow.showToast(MineFeedbackActivity.this, "Dear users, your suggestions have been uploaded to our mailbox, thank you very much for your feedback and help!");
                    et_feedback.setText("");
                }


            default:
                break;
        }
    }

    private void pushtoBmob() {
        bmobPushManager.pushMessageAll(inputContent, new PushListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    et_feedback.setText("");
                    ToastShow.showToast(MineFeedbackActivity.this, "push successfully");
                } else {
                    ToastShow.showToast(MineFeedbackActivity.this, "push failed");
                }
            }
        });
    }
}
