package com.runstart.slidingpage;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.runstart.bean.User;
import com.runstart.MainActivity;
import com.runstart.R;
import com.runstart.help.GetSharedPreferences;
import com.runstart.help.ToastShow;
import com.runstart.MyApplication;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

/**
 * Created by zhonghao.song on 17-9-26.
 */
public class LeadPageActivity extends AppCompatActivity {

    private SharedPreferences pref;
    private GetSharedPreferences getSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_login_layout);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        getSharedPreferences = GetSharedPreferences.getPref(this);

        final boolean isRemember = pref.getBoolean("remember_password", false);
        final String userObjectId = pref.getString("userObjectId", "0");
        final String userPhone = pref.getString("phone", "");
        final String userPassword = pref.getString("password", "");

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isRemember) {
                    BmobQuery<User> userBmobQuery = new BmobQuery<>();
                    userBmobQuery.getObject(userObjectId, new QueryListener<User>() {
                        @Override
                        public void done(User user, BmobException e) {
                            if (e == null) {
                                if (user.getPhoneNumber().equals(userPhone) && user.getPassword().equals(userPassword)) {
                                    getSharedPreferences.saveUser(user);
                                    MyApplication.applicationMap.put("userObjectId", user.getObjectId());
                                    startActivity(new Intent(LeadPageActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    ToastShow.showToast(LeadPageActivity.this, "密码错误！");
                                    startActivity(new Intent(LeadPageActivity.this, LoginPageActivity.class));
                                    finish();
                                }
                            } else {
                                ToastShow.showToast(LeadPageActivity.this, "连接失败！");
                                startActivity(new Intent(LeadPageActivity.this, LoginPageActivity.class));
                                finish();
                            }
                        }
                    });


                } else {
                    startActivity(new Intent(LeadPageActivity.this, LoginPageActivity.class));
                    finish();

                }


            }
        }, 1000);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
