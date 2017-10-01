package com.runstart.slidingpage;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.runstart.BmobBean.User;
import com.runstart.R;
import com.runstart.history.MyApplication;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobRole;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;

public class ForgetPasswordActivity extends AppCompatActivity {

    private ImageView phoneImage, passwordImage;
    private ToggleButton showPassword;
    private EditText phoneNumber, password, confirmationCode;
    private Button getConfirmationCode, determine, goBack;

    private int leftTime = 59;
    private Timer mTimer;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x123){
                if (leftTime == -1){
                    getConfirmationCode.setText("Obtain");
                    getConfirmationCode.setClickable(true);
                    leftTime = 59;
                    mTimer.cancel();
                }else {
                    getConfirmationCode.setText((leftTime --) + "S");
                }
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        phoneImage = (ImageView)findViewById(R.id.phoneImage);
        passwordImage = (ImageView)findViewById(R.id.passwordImage);
        showPassword = (ToggleButton) findViewById(R.id.showPassword);
        phoneNumber = (EditText)findViewById(R.id.phoneNumber);
        password = (EditText)findViewById(R.id.password);
        confirmationCode = (EditText)findViewById(R.id.confirmationCode);
        getConfirmationCode = (Button)findViewById(R.id.getConfirmationCode);
        determine = (Button)findViewById(R.id.determine);
        goBack = (Button)findViewById(R.id.goBack);

        phoneNumber.setText(getIntent().getStringExtra("phoneNumber"));

        phoneNumber.requestFocus();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager inputMethodManager =
                        (InputMethodManager)phoneNumber.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(phoneNumber, 0);
            }
        }, 100);

        phoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    phoneImage.setImageResource(R.mipmap.ic_shouji);
                }else {
                    phoneImage.setImageResource(R.mipmap.ic_shouji2);
                }
            }
        });
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    passwordImage.setImageResource(R.mipmap.ic_mima1);
                }else {
                    passwordImage.setImageResource(R.mipmap.ic_mima2);
                }
            }
        });
        getConfirmationCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发送验证码
                getConfirmationCode.setClickable(false);
                mTimer = new Timer();
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mHandler.sendEmptyMessage(0x123);
                    }
                }, 0, 1000);
            }
        });
        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    showPassword.setBackgroundResource(R.mipmap.ic_eye2);
                }else {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    showPassword.setBackgroundResource(R.mipmap.ic_eye1);
                }
            }
        });
        determine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phoneNumberStr = phoneNumber.getText().toString();
                final String passwordStr = password.getText().toString();
                String sql = "select * from User where phoneNumber=?";
                BmobQuery<User> query = new BmobQuery<>();
                query.setSQL(sql);
                query.setPreparedParams(new String[]{phoneNumberStr});
                query.doSQLQuery(new SQLQueryListener<User>() {
                    @Override
                    public void done(BmobQueryResult<User> bmobQueryResult, BmobException e) {
                        if (e == null){
                            List<User> users = bmobQueryResult.getResults();
                            if (users.size() == 0){
                                Toast.makeText(ForgetPasswordActivity.this, "You have not registered this account", Toast.LENGTH_SHORT).show();
                            }else {
                                User user = users.get(0);
                                MyApplication.applicationMap.put("phoneNumber", phoneNumberStr);
                                MyApplication.applicationMap.put("userObjectId", user.getObjectId());
                                user.setPassword(passwordStr);
                                user.update();
                                startActivity(new Intent(ForgetPasswordActivity.this, WelcomeActivity.class));
                                Toast.makeText(ForgetPasswordActivity.this, "password modified successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
