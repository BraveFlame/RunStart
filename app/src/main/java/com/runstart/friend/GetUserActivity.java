package com.runstart.friend;

import android.app.Activity;
import android.os.Bundle;

import com.runstart.BmobBean.User;

import cn.bmob.v3.BmobQuery;

/**
 * Created by user on 17-9-30.
 */

public class GetUserActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BmobQuery<User>query=new BmobQuery<>();

    }
}
