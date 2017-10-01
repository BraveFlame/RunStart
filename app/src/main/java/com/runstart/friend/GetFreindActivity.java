package com.runstart.friend;

import android.app.Activity;
import android.os.Bundle;

import com.runstart.BmobBean.User;
import com.runstart.help.ToastShow;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

/**
 * Created by user on 17-9-30.
 */

public class GetFreindActivity extends Activity{
    BmobQuery<User>friendQuery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        friendQuery.getObject("033d152e41", new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {

                if(e==null){


                }else {
                    ToastShow.showToast(GetFreindActivity.this,"连接error");
                }
            }
        });

    }
}
