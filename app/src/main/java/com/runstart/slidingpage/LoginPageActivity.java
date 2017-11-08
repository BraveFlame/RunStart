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
import android.widget.ToggleButton;

import com.runstart.bean.DaySport;
import com.runstart.bean.User;
import com.runstart.MainActivity;
import com.runstart.R;
import com.runstart.help.GetSharedPreferences;
import com.runstart.help.ToastShow;
import com.runstart.MyApplication;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.BmobQuery;

import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SQLQueryListener;

/**
 * Created by yiman.mei on 17-9-10.
 */
public class LoginPageActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView phoneImage, passwordImage;
    private ToggleButton showPassword;
    private EditText phoneNumber, password;
    private TextView forgetPassword, nullCount;
    private Button loginBtn, goBackBtn;
    private CheckBox checkBox;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private GetSharedPreferences getSharedPreferences;

    private boolean isRemember;
    private String userPhone;
    private String userPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        /**
         * changed by zhonghao.song on 17-9-24
         * add "remember password"
         */
        getSharedPreferences = GetSharedPreferences.getPref(this);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pref.edit();
        isRemember = pref.getBoolean("remember_password", false);
        userPhone = pref.getString("phone", "");
        userPassword = pref.getString("password", "");
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
        loginBtn.setOnClickListener(this);
        goBackBtn.setOnClickListener(this);
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
        loginBtn = (Button) findViewById(R.id.login);
        goBackBtn = (Button) findViewById(R.id.goBack);
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
            /**
             * changed by zhonghao.song
             *增加
             *获取bmob运动数据
             */
            case R.id.login:
                String passwordStr = password.getText().toString();
                String sql = "select * from User where phoneNumber=? and password=?";
                BmobQuery<User> query = new BmobQuery<>();
                query.setSQL(sql);
                query.setPreparedParams(new String[]{phoneNumberStr, passwordStr});
                final MyApplication myApplication = (MyApplication) getApplication();
                myApplication.showProgressDialog(LoginPageActivity.this);
                query.doSQLQuery(new SQLQueryListener<User>() {
                    @Override
                    public void done(BmobQueryResult<User> bmobQueryResult, BmobException e) {
                        if (e == null) {
                            List<User> users = bmobQueryResult.getResults();
                            if (users.size() == 0) {
                                myApplication.stopProgressDialog();
                                ToastShow.showToast(LoginPageActivity.this, "The phoneNumber or password is wrong");
                            } else {

                                judgeUserData(users.get(0));
                                // getData(users.get(0));
                                // saveData();
                                MyApplication.applicationMap.put("userObjectId", users.get(0).getObjectId());
                                remenberPassword();
                                getSharedPreferences.saveUser(users.get(0));

                            }
                        } else {
                            Log.e("*********", e.getMessage() + "****************exception");
                            ToastShow.showToast(LoginPageActivity.this, "Login failed");
                            myApplication.stopProgressDialog();
                        }
                    }
                });
                break;
            case R.id.goBack:
                finish();
        }
    }

    /**
     * changed by zhonghao.song
     * 记住密码
     */
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


    /**
     * changed by zhonghao.song
     * 判断是不是切换帐号，是才获取bmob数据
     */
    public void judgeUserData(User user) {
        final MyApplication myApplication = (MyApplication) getApplication();
        if (user.getObjectId().equals(pref.getString("userObjectId", "12345"))) {
            myApplication.stopProgressDialog();
            startActivity(new Intent(LoginPageActivity.this, MainActivity.class));
            ToastShow.showToast(LoginPageActivity.this, "Login successfully");
            finish();
            return;
        }
        //保存到本地
        myApplication.nowDB.dropSport();
        BmobQuery<DaySport> daySportBmobQuery = new BmobQuery<>();
        daySportBmobQuery.addWhereEqualTo("userID", user.getPhoneNumber());
        final String phoneNumber = user.getPhoneNumber();
        daySportBmobQuery.findObjects(new FindListener<DaySport>() {
            @Override
            public void done(List<DaySport> list, BmobException e) {

                if (e == null) {
                    if (list.size() > 0)
                        myApplication.nowDB.insertDaySport(list, phoneNumber);
                }
                myApplication.stopProgressDialog();
                startActivity(new Intent(LoginPageActivity.this, MainActivity.class));
                ToastShow.showToast(LoginPageActivity.this, "Login successfully");
                finish();
            }
        });
    }
// /**
//    * changed by zhonghao.song
//   * 转移数据表之取test
//   */
//
//
//    public void getData(User user){
//
//        if(user.getObjectId().equals(pref.getString("userObjectId","12345"))) {
//            saveData();
//            return;
//        }
//        final MyApplication myApplication=(MyApplication)getApplication();
//        BmobQuery<tempData>bmobQuery=new BmobQuery<>();
//        bmobQuery.addWhereEqualTo("userID",1820272768);
//        bmobQuery.findObjects(new FindListener<tempData>() {
//            @Override
//            public void done(List<tempData> list, BmobException e) {
//                if(e==null){
//                    if (list.size()==0)
//                        return;
//                    String []phone=new String[]{"18814126594"};
//                    for (int i=0;i<list.size();i++){
//                        tempData daySport=list.get(i);
//                        myApplication.nowDB.insert(phone,new double[]{daySport.getMonth(),daySport.getWeek() ,
//                                daySport.getDay(),daySport.getDistance(),daySport.getTime(),
//                                daySport.getCal(),daySport.getType()});
//                    }
//                }
//            }
//        });
//    }
// /**
    //  * changed by zhonghao.song
    //  * 转移数据表之存test
    // */
//
//    public void saveData(){
//        List<BmobObject>daySportList=new ArrayList<>();
//        final MyApplication myApplication=(MyApplication)getApplication();
//        myApplication.nowDB.query(daySportList);
//        new BmobBatch().insertBatch(daySportList).doBatch(new QueryListListener<BatchResult>() {
//            @Override
//            public void done(List<BatchResult> list, BmobException e) {
//                if(e==null){
//                    Log.e("bmob","SAVE"+list.size());
//                    ToastShow.showToast(LoginPageActivity.this,"CHG");
//                }
//            }
//        });
//    }

}
