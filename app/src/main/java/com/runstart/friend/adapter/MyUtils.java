package com.runstart.friend.adapter;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.runstart.BmobBean.Friend;
import com.runstart.BmobBean.User;
import com.runstart.R;
import com.runstart.MyApplication;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by User on 17-9-1.
 */
public class MyUtils {
    //点赞
    public static void like(int source, ImageView likeImage, Friend friend, User user, Context context, TextView likeNumberText){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if ((friend.getLikeDate().length() != 0) && ((year == Integer.parseInt(friend.getLikeDate().substring(0, 4))) &&
                (month == Integer.parseInt(friend.getLikeDate().substring(4, 6))) &&
                (day == Integer.parseInt(friend.getLikeDate().substring(6, 8))))){
            Toast.makeText(context, "You have liked " + user.getNickName() + " today", Toast.LENGTH_SHORT).show();
            return;
        }
        if (user.getObjectId().equals(MyApplication.applicationMap.get("userObjectId"))){
            Toast.makeText(context, "You can not liked yourself", Toast.LENGTH_SHORT).show();
            return;
        }

        likeImage.setImageResource(R.mipmap.ic_zan);
        String monthStr = month >9 ? month + "" : "0" + month;
        String dayStr = day >9 ? day + "" : "0" + day;
        friend.setLikeDate(year + monthStr + dayStr);

        int likeNumber = user.getLikeNumberForHistory();
        likeNumber++;
        likeNumberText.setTextColor(0xffc562ff);
        user.setLikeNumberForHistory(likeNumber);

        //source指点赞所在页面标志
        if (source == 0 && likeNumber > 99){
            likeNumberText.setText("99+");
        }else {
            likeNumberText.setText(likeNumber + "");
        }

        user.update();
        friend.update();
    }

    //获取日期和时间的格式化形式
    public static String getStringToday() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    //显示等待数据加载对话框
//    public static void showProgressDialog(ProgressDialog progressDialog){
//        progressDialog.setTitle("Loading data");
//        progressDialog.setMessage("Data is loading, please waite for a moment.");
//        progressDialog.setCancelable(true);
//        progressDialog.setIndeterminate(true);
//        progressDialog.show();
//    }
    public static void showProgressDialog(Activity activity){
        MyApplication myApplication=(MyApplication)activity.getApplicationContext();
        myApplication.showProgressDialog(activity);
    }
    public static void dismissProgressDialog(Activity activity){
        MyApplication myApplication=(MyApplication)activity.getApplicationContext();
        myApplication.stopProgressDialog();
    }
//    public static void dismissProgressDialog(ProgressDialog progressDialog){
//        if (progressDialog.isShowing()){
//            progressDialog.dismiss();
//        }
//    }
}
