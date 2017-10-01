package com.runstart.friend.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.ArrayMap;
import android.view.View;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import com.runstart.BmobBean.Friend;
import com.runstart.BmobBean.User;
import com.runstart.R;

import java.util.Calendar;

/**
 * Created by User on 17-9-1.
 */
public class MyUtils {

    /*private List<Friend> friends = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private Map<String, Bitmap> bitmapMap = new ArrayMap<>();

    private Map<String, Bitmap> forDetailsBitmap = new ArrayMap<>();
    private User[] orderedUserArr;
    private Friend[] orderedFriendArr;

    private final int FINISH_LOADING_FRIEND = 0x111;
    private final int FINISH_LOADING_BITMAP = 0x112;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == FINISH_LOADING_FRIEND){
                init();
                getUser();
            }
            if (msg.what == FINISH_LOADING_BITMAP){
                showResult("walkDistance+runDistance+rideDistance", 0);
            }
        }
    };*/


    public static void like(ImageView likeImage, Friend friend, User user, Context context, TextView likeNumberText){

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if ((friend.getLikeDate() == null) || ((year == Integer.parseInt(friend.getLikeDate().substring(0, 4))) &&
                (month == Integer.parseInt(friend.getLikeDate().substring(4, 6))) &&
                (day == Integer.parseInt(friend.getLikeDate().substring(6, 8))))){
            Toast.makeText(context, "You have liked " + user.getNickName() + " today", Toast.LENGTH_SHORT).show();
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

        if (likeNumber > 99){
            likeNumberText.setText("99+");
        }else {
            likeNumberText.setText(likeNumber + "");
        }
        user.update();
        friend.update();
    }

    /*public static void getUsers(){

    }

    private void beginQuery(final Context context, String whereState, String[] params){
        String sql = "select * from Friend " + whereState;
        BmobQuery<Friend> query = new BmobQuery<>();
        query.setSQL(sql);
        query.setPreparedParams(params);
        query.doSQLQuery(new SQLQueryListener<Friend>() {
            @Override
            public void done(BmobQueryResult<Friend> bmobQueryResult, BmobException e) {
                if (e == null){
                    friends = bmobQueryResult.getResults();
                    handler.sendEmptyMessage(FINISH_LOADING_FRIEND);
                }else {
                    Toast.makeText(context, "load friends failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }*/
}
