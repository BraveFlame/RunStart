package com.runstart.help;

import android.content.Context;

import com.runstart.BmobBean.DaySport;
import com.runstart.BmobBean.User;
import com.runstart.friend.LocalChatLog;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

/**
 * Created by user on 17-10-10.
 */

public class GetSportDataBmob {

    public static void getDaySportData(Context context) {
        BmobQuery<User> userBmobQuery = new BmobQuery<>();
        userBmobQuery.getObject("sd", new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {

            }
        });

    }

    public static void getAllSportData(Context context) {

        BmobQuery<DaySport> daySportBmobQuery = new BmobQuery<>();
        daySportBmobQuery.addWhereEqualTo("userID", "1882121212");
        daySportBmobQuery.findObjects(new FindListener<DaySport>() {
            @Override
            public void done(List<DaySport> list, BmobException e) {
                if (e == null && list.size() > 0) {
                    for (DaySport daySport : list) {

                    }
                }


            }
        });

    }


}
