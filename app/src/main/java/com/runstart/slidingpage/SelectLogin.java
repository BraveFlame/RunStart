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


public class SelectLogin extends AppCompatActivity {
//
//    private Button jump, login;
//
//    private TextView nullCount;
    private SharedPreferences pref;

    private  GetSharedPreferences getSharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_login_layout);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        getSharedPreferences=GetSharedPreferences.getPref(this);

        final boolean isRemember = pref.getBoolean("remember_password", false);
        final String userObjectId=pref.getString("userObjectId","0");
        final String userPhone=pref.getString("phone","");
        final String userPassword=pref.getString("password","");

        Handler handler =new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isRemember){
                    BmobQuery<User>userBmobQuery=new BmobQuery<>();
                    userBmobQuery.getObject(userObjectId, new QueryListener<User>() {
                        @Override
                        public void done(User user, BmobException e) {
                            if (e==null){
                                if (user.getPhoneNumber().equals(userPhone)&&user.getPassword().equals(userPassword))
                                {
                                    getSharedPreferences.saveUser(user);
                                    MyApplication.applicationMap.put("userObjectId", user.getObjectId());
                                    startActivity(new Intent(SelectLogin.this, MainActivity.class));
                                    finish();
                                }else {
                                    ToastShow.showToast(SelectLogin.this,"密码错误！");
                                    startActivity(new Intent(SelectLogin.this, LoginPageActivity.class));
                                    finish();
                                }
                            }else {
                                ToastShow.showToast(SelectLogin.this,"连接失败！");
                                startActivity(new Intent(SelectLogin.this, LoginPageActivity.class));
                                finish();
                            }
                        }
                    });


                }else {
                    startActivity(new Intent(SelectLogin.this, LoginPageActivity.class));
                    finish();

                }


            }
        },1000);



//        jump = (Button)findViewById(R.id.jump);
//        login = (Button)findViewById(R.id.login);
//        nullCount = (TextView)findViewById(R.id.nullCount);
//
//        jump.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(SelectLogin.this, LoginPageActivity.class));
//            }
//        });
//        login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(SelectLogin.this, LoginPageActivity.class));
//                finish();
//            }
//        });
//        nullCount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(SelectLogin.this, RegisterActivity.class));
//            }
//        });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
