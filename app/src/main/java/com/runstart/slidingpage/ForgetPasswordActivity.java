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
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.runstart.BmobBean.User;
import com.runstart.R;
import com.runstart.help.JudgePhoneMail;
import com.runstart.help.ToastShow;
import com.runstart.history.MyApplication;

import org.json.JSONArray;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobRole;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView phoneImage, passwordImage;
    private ToggleButton showPassword;
    private EditText phoneNumber, password, confirmationCode;
    private Button getConfirmationCode, determine, goBack;

    private int leftTime = 59;
    private Timer mTimer;
    private User user;
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
        setContentView(R.layout.activity_forget_password);

        initView();
        setIconColorShow();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager inputMethodManager =
                        (InputMethodManager) phoneNumber.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(phoneNumber, 0);
            }
        }, 100);

        /**
         * 验证码
         */
        getConfirmationCode.setOnClickListener(this);
        /**
         * 提交修改密码
         */
        determine.setOnClickListener(this);
        goBack.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /**
             * 获取验证码
             */
            case R.id.getConfirmationCode:
                if (!JudgePhoneMail.isMobileNO(phoneNumber.getText().toString())) {
                    ToastShow.showToast(ForgetPasswordActivity.this, "手机号码格式错误！");
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
                            Toast.makeText(ForgetPasswordActivity.this, "get user failed", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        final List<User> users = bmobQueryResult.getResults();
                        if (users.size() == 0) {
                            Toast.makeText(ForgetPasswordActivity.this, "You have not registered this account", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        user = users.get(0);
                        getCode(phoneNumber.getText().toString());

                    }
                });
                break;

            /**
             * 确认提交
             */
            case R.id.determine:
                final String phoneNumberStr = phoneNumber.getText().toString();
                final String passwordStr = password.getText().toString();
                final String getCode = confirmationCode.getText().toString();
                if (!isAllInfo()) {
                    ToastShow.showToast(ForgetPasswordActivity.this, "输入不能为空！");
                    return;
                }

                if (!JudgePhoneMail.isMobileNO(phoneNumberStr)) {
                    ToastShow.showToast(ForgetPasswordActivity.this, "手机号码格式错误！");
                    return;
                }

                BmobSMS.verifySmsCode(phoneNumberStr, getCode, new UpdateListener() {
                    @Override
                    public void done(BmobException ee) {
                        if (ee != null) {
                            ToastShow.showToast(ForgetPasswordActivity.this, "验证码错误！");
                            return;
                        }
                        MyApplication.applicationMap.put("phoneNumber", phoneNumberStr);
                        MyApplication.applicationMap.put("userObjectId", user.getObjectId());
                        user.setPassword(passwordStr);
                        user.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    finish();
                                    Toast.makeText(ForgetPasswordActivity.this, "password modified successfully", Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(ForgetPasswordActivity.this, "password modified failed", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }
                });

                break;

            //返回
            case R.id.goBack:

                finish();

                break;

            default:
                break;
        }
    }
    //发送验证码
    public void getCode(String phone){

        BmobSMS.requestSMSCode(phone, "注册模板", new QueryListener<Integer>() {
            @Override
            public void done(Integer integer, BmobException e) {
                if (e == null) {
                    ToastShow.showToast(ForgetPasswordActivity.this, "验证码发送成功！");

                } else {
                    ToastShow.showToast(ForgetPasswordActivity.this, "验证码发送失败！");
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

    public void initView() {
        phoneImage = (ImageView) findViewById(R.id.phoneImage);
        passwordImage = (ImageView) findViewById(R.id.passwordImage);
        showPassword = (ToggleButton) findViewById(R.id.showPassword);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        password = (EditText) findViewById(R.id.password);
        confirmationCode = (EditText) findViewById(R.id.confirmationCode);
        getConfirmationCode = (Button) findViewById(R.id.getConfirmationCode);
        determine = (Button) findViewById(R.id.determine);
        goBack = (Button) findViewById(R.id.goBack);

        phoneNumber.setText(getIntent().getStringExtra("phoneNumber"));
        phoneNumber.requestFocus();
    }

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
