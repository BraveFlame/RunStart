package com.runstart.slidingpage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.runstart.BmobBean.User;
import com.runstart.MainActivity;
import com.runstart.R;
import com.runstart.help.GetSharedPreferences;
import com.runstart.help.ToastShow;
import com.runstart.history.MyApplication;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.BmobQuery;

import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;


public class LoginPageActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView phoneImage, passwordImage;
    private ToggleButton showPassword;
    private EditText phoneNumber, password;
    private TextView forgetPassword;
    private Button login, goBack;
    private TextView nullCount;
    private CheckBox checkBox;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private GetSharedPreferences getSharedPreferences;
    private boolean isRemember;
    private String userPhone;
    private String userPassword, userObjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        getSharedPreferences=GetSharedPreferences.getPref(this);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pref.edit();
        isRemember = pref.getBoolean("remember_password", false);
        userPhone = pref.getString("phone", "12345");
        userPassword = pref.getString("password", "12345");
        userObjectId = pref.getString("userObjectId", "12345");
        if (isRemember) {
            phoneNumber.setText(userPhone);
            password.setText(userPassword);
            checkBox.setChecked(true);
        }

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager inputMethodManager =
                        (InputMethodManager) phoneNumber.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(phoneNumber, 0);
            }
        }, 200);

        setIconColar();

        nullCount.setOnClickListener(this);
        forgetPassword.setOnClickListener(this);
        login.setOnClickListener(this);
        goBack.setOnClickListener(this);
    }


    public void initView() {

        nullCount = (TextView) findViewById(R.id.nullCount);
        phoneImage = (ImageView) findViewById(R.id.phoneImage);
        passwordImage = (ImageView) findViewById(R.id.passwordImage);
        showPassword = (ToggleButton) findViewById(R.id.showPassword);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        password = (EditText) findViewById(R.id.password);
        forgetPassword = (TextView) findViewById(R.id.forgetPassword);
        checkBox = (CheckBox) findViewById(R.id.remember_password_chbok);
        login = (Button) findViewById(R.id.login);
        goBack = (Button) findViewById(R.id.goBack);
        phoneNumber.requestFocus();

    }

    @Override
    public void onClick(View v) {
        String phoneNumberStr = phoneNumber.getText().toString();
        MyApplication.applicationMap.put("phoneNumber", phoneNumberStr);
        switch (v.getId()) {
            case R.id.nullCount:
                startActivity(new Intent(LoginPageActivity.this, RegisterActivity.class));
                break;
            case R.id.forgetPassword:
                Intent intent = new Intent(LoginPageActivity.this, ForgetPasswordActivity.class);
                intent.putExtra("phoneNumber", phoneNumberStr);
                startActivity(intent);
                break;
            case R.id.login:
                String passwordStr = password.getText().toString();
                String sql = "select * from User where phoneNumber=? and password=?";
                BmobQuery<User> query = new BmobQuery<>();
                query.setSQL(sql);
                query.setPreparedParams(new String[]{phoneNumberStr, passwordStr});
                query.doSQLQuery(new SQLQueryListener<User>() {
                    @Override
                    public void done(BmobQueryResult<User> bmobQueryResult, BmobException e) {
                        if (e == null) {
                            List<User> users = bmobQueryResult.getResults();
                            if (users.size() == 0) {
                                ToastShow.showToast(LoginPageActivity.this, "The phoneNumber or password is wrong");
                            } else {
                                remenberPassword();
                                getSharedPreferences.saveUser(users.get(0));
                                MyApplication.applicationMap.put("userObjectId", users.get(0).getObjectId());
                                startActivity(new Intent(LoginPageActivity.this, MainActivity.class));
                                ToastShow.showToast(LoginPageActivity.this, "Login successfully");
                                finish();
                            }
                        } else {
                            Log.e("*********", e.getMessage() + "****************exception");
                            ToastShow.showToast(LoginPageActivity.this, "Login failed");
                        }
                    }
                });
                break;
            case R.id.goBack:
                finish();
        }
    }

    public void remenberPassword() {

        if (checkBox.isChecked()) {

            editor.putBoolean("remember_password", true);

        } else {
            editor.putBoolean("remember_password", false);
        }


        editor.commit();
    }

    public void setIconColar() {
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
