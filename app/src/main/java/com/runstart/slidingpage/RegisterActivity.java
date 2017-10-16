package com.runstart.slidingpage;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
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
import com.runstart.help.JudgePhoneMail;
import com.runstart.help.ToastShow;
import com.runstart.history.MyApplication;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView phoneImage, passwordImage;
    private ToggleButton showPassword;

    private EditText phoneNumber, password, confirmationCode;

    private Button getConfirmationCode, register, goBack;


    private User user;

    private int leftTime = 59;
    private Timer mTimer;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                if (leftTime == -1) {
                    getConfirmationCode.setText("Obtain");
                    getConfirmationCode.setClickable(true);
                    leftTime = 59;
                    mTimer.cancel();
                } else {
                    getConfirmationCode.setText((leftTime--) + "S");
                }
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        initView();
        phoneNumber.requestFocus();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) phoneNumber.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(phoneNumber, 0);
            }
        }, 100);

        setIconColorShow();
        getConfirmationCode.setOnClickListener(this);

        register.setOnClickListener(this);
        goBack.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.getConfirmationCode:
                if (!JudgePhoneMail.isMobileNO(phoneNumber.getText().toString())) {
                    ToastShow.showToast(RegisterActivity.this, "手机号码格式错误！");
                    return;
                }
                String sql = "select * from User where phoneNumber=?";
                BmobQuery<User> query = new BmobQuery<>();
                query.setSQL(sql);
                query.setPreparedParams(new String[]{phoneNumber.getText().toString()});
                query.doSQLQuery(new SQLQueryListener<User>() {
                    @Override
                    public void done(BmobQueryResult<User> bmobQueryResult, BmobException e) {
                        //手机
                        if (e != null) {
                            Toast.makeText(RegisterActivity.this, "connecting failed", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        final List<User> users = bmobQueryResult.getResults();
                        if (users.size() > 0) {
                            Toast.makeText(RegisterActivity.this, "You have already registered this account", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        getCode(phoneNumber.getText().toString());
                    }
                });

                break;


            case R.id.register:
                final String phoneNumberStr = phoneNumber.getText().toString();
                final String passwordStr = password.getText().toString();
                final String getCode = confirmationCode.getText().toString();
                if (!isAllInfo()) {
                    ToastShow.showToast(RegisterActivity.this, "输入不能为空！");
                    return;
                }

                if (!JudgePhoneMail.isMobileNO(phoneNumberStr)) {
                    ToastShow.showToast(RegisterActivity.this, "手机号码格式错误！");
                    return;
                }

                BmobSMS.verifySmsCode(phoneNumberStr, getCode, new UpdateListener() {
                    @Override
                    public void done(BmobException ee) {
                        if (ee != null) {
                            ToastShow.showToast(RegisterActivity.this, "验证码错误！");
                            return;
                        }
                        user = new User();
                        user.setPhoneNumber(phoneNumberStr);
                        user.setPassword(passwordStr);
                        MyApplication.applicationMap.put("phoneNumber", phoneNumberStr);
                        MyApplication.applicationMap.put("userObjectId", user.getObjectId());
                        user.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    finish();
                                    Toast.makeText(RegisterActivity.this, "Register successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Register failed", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });


                    }
                });
                break;


            case R.id.goBack:
                finish();
                break;

            default:
                break;

        }
    }

    //发送验证码
    public void getCode(String phone) {

        BmobSMS.requestSMSCode(phone, "注册模板", new QueryListener<Integer>() {
            @Override
            public void done(Integer integer, BmobException e) {
                if (e == null) {
                    ToastShow.showToast(RegisterActivity.this, "验证码发送成功！");

                } else {
                    ToastShow.showToast(RegisterActivity.this, "验证码发送失败！");
                    getConfirmationCode.setText("Obtain");
                    getConfirmationCode.setClickable(true);
                    leftTime = 59;
                    mTimer.cancel();
                }
            }
        });

        getConfirmationCode.setClickable(false);
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(0x123);
            }
        }, 0, 1000);
    }

    public boolean isAllInfo() {
        if (!"".equals(phoneNumber.getText().toString()) && !"".equals(password.getText().toString())
                && !"".equals(confirmationCode.getText().toString()))
            return true;
        else return false;
    }

    /**
     * 初始化界面
     */
    public void initView() {
        phoneImage = (ImageView) findViewById(R.id.phoneImage);
        passwordImage = (ImageView) findViewById(R.id.passwordImage);
        showPassword = (ToggleButton) findViewById(R.id.showPassword);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        password = (EditText) findViewById(R.id.password);
        confirmationCode = (EditText) findViewById(R.id.confirmationCode);
        getConfirmationCode = (Button) findViewById(R.id.getConfirmationCode);
        register = (Button) findViewById(R.id.register);
        goBack = (Button) findViewById(R.id.goBack);
    }


    /**
     * 根据是否输入，改变图案深浅
     */
    public void setIconColorShow() {
        phoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    phoneImage.setImageResource(R.mipmap.ic_shouji);
                } else {
                    phoneImage.setImageResource(R.mipmap.ic_shouji2);
                }
            }
        });
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    passwordImage.setImageResource(R.mipmap.ic_mima1);
                } else {
                    passwordImage.setImageResource(R.mipmap.ic_mima2);
                }
            }
        });

        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    showPassword.setBackgroundResource(R.mipmap.ic_eye2);
                } else {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    showPassword.setBackgroundResource(R.mipmap.ic_eye1);
                }
            }
        });
    }
}
