package com.runstart.circle.circle_activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.runstart.bean.ActivityAndMember;
import com.runstart.bean.ActivityData;
import com.runstart.bean.User;
import com.runstart.R;
import com.runstart.MyApplication;
import com.runstart.circle.CommonUtils;
import com.runstart.circle.GetFromBmob;
import com.runstart.circle.NowPupwindow;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CirclePushCardActivity extends AppCompatActivity {

    TextView activityName,activityIntroduction,activityCreatingDate,frequency,exerciseAmount;
    ImageView activityBackground;
    TextView beenInDays,signInTimes,completionRate;
    TextView averageSpeed,exerciseTime,cal;
    ImageView memberHeaderUrl1,memberHeaderUrl2,memberHeaderUrl3,memberHeaderUrl4,memberHeaderUrl5,memberHeaderUrl6;
    Button punchCard;

    public static String initActivityId,initMyId;

    public ActivityData activityData;
    public ActivityAndMember activityAndMember;
    User user;

    Handler handler=new Handler(){
        int k=0;
        int m=0;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Gson gson=new Gson();
            Bundle bundle=msg.getData();
            String str;
            switch (msg.what){
                //获取activity信息，并设置到布局
                case 1:
                    str=bundle.getString("ActivityData");
                    ActivityData activityData = gson.fromJson(str, ActivityData.class);
                    setADview(activityData);
                    k++;
                    //取出，用于判断是否完成任务，与上方无关
                    CirclePushCardActivity.this.activityData=activityData;
                    m++;
                    if(m>=2){
                        handler.sendEmptyMessage(8);
                    }
                    break;
                //获取成员信息，并设置到布局
                case 2:
                    str=bundle.getString("ActivityAndMember");
                    ActivityAndMember activityAndMember =gson.fromJson(str,ActivityAndMember.class);
                    CirclePushCardActivity.this.activityAndMember = activityAndMember;
                    setMTAview(activityAndMember);
                    k++;
                    m++;
                    if(m>=2){
                        handler.sendEmptyMessage(8);
                    }
                    break;
                //获取成员的用户信息，并设置到布局
                case 3:
                    str=bundle.getString("User");
                    User user=gson.fromJson(str,User.class);
                    setUserView(user);
                    //取出，用于判断是否完成任务，与上方无关
                    CirclePushCardActivity.this.user=user;
                    k++;
                    break;
                //成员信息更新后调用，重新获取成员信息并设置，同时有个抖的动作
                case 4:
                    GetFromBmob.getAAM(initActivityId,initMyId,handler);
                    YoYo.with(Techniques.Wobble)
                            .duration(700)
                            .repeat(2)
                            .playOn(signInTimes);
                    YoYo.with(Techniques.Wobble)
                            .duration(700)
                            .repeat(2)
                            .playOn(completionRate);
                    break;
                //加载成员头像
                case 5:
                    str=bundle.getString("MTAlist");
                    List<ActivityAndMember> ps = gson.fromJson(str, new TypeToken<List<ActivityAndMember>>(){}.getType());
                    setMemberHeaderImage(ps);
                    break;
                //有图片加载完了才进行，这个时候算比较晚了，故三种数据都取好了，下面交叉调用的才不会出错
                case 6:
                    ((MyApplication)getApplicationContext()).stopProgressDialog();
                    break;
                //按钮抖一下然后消失的动画
                case 7:
                    YoYo.with(Techniques.Pulse)
                            .duration(700)
                            .repeat(3)
                            .playOn(punchCard);
                    YoYo.with(Techniques.FlipOutY).delay(2100)
                            .duration(700)
                            .repeat(1)
                            .playOn(punchCard);
                    break;
                //没办法，试了很多次，更新只能延时——handler来设置完成率
                case 8:
                    completionRate.setText(getCompletionRate()+"%");
                    break;
                default:break;
            }
            if(k==3){
                k=0;
                setButton();
            }
        }
    };

    public static void jump(String initActivityId,Activity activity){
        Intent intent=new Intent(activity,CirclePushCardActivity.class);
        intent.putExtra("initAcitivityId",initActivityId);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_data();
        init_view();
        GetFromBmob.getAD(initActivityId,handler);
        GetFromBmob.getUserById(initMyId,handler);
        GetFromBmob.getAAM(initActivityId,initMyId,handler);
        GetFromBmob.getActivityMember(initActivityId,handler);
    }

    private void init_data(){
        Intent intent=getIntent();
        this.initActivityId=intent.getStringExtra("initAcitivityId");
        this.initMyId=((MyApplication)getApplicationContext()).applicationMap.get("userObjectId");
    }

    private void init_view(){
        setContentView(R.layout.fragment_circle_pushcard);

        activityName=(TextView)findViewById(R.id.activityName);
        activityIntroduction=(TextView)findViewById(R.id.activityIntroduction);
        activityCreatingDate=(TextView)findViewById(R.id.activityCreatingDate);
        frequency=(TextView)findViewById(R.id.frequency);
        exerciseAmount=(TextView)findViewById(R.id.exerciseAmount);
        activityBackground=(ImageView)findViewById(R.id.activityBackground);

        beenInDays=(TextView)findViewById(R.id.beenInDays);
        signInTimes=(TextView)findViewById(R.id.signInTimes);
        completionRate=(TextView)findViewById(R.id.completionRate);

        averageSpeed=(TextView)findViewById(R.id.averageSpeed);
        exerciseTime=(TextView)findViewById(R.id.exerciseTime);
        cal=(TextView)findViewById(R.id.Kcal);

        memberHeaderUrl1=(ImageView)findViewById(R.id.circle_pushcard_iv_userimage_first);
        memberHeaderUrl2=(ImageView)findViewById(R.id.circle_pushcard_iv_userimage_second);
        memberHeaderUrl3=(ImageView)findViewById(R.id.circle_pushcard_iv_userimage_three);
        memberHeaderUrl4=(ImageView)findViewById(R.id.circle_pushcard_iv_userimage_four);
        memberHeaderUrl5=(ImageView)findViewById(R.id.circle_pushcard_iv_userimage_five);
        memberHeaderUrl6=(ImageView)findViewById(R.id.circle_pushcard_iv_userimage_six);

        findViewById(R.id.circle_pushcard_iv_zuojiantou).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.circle_pushcard_ll_friend_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CircleFriendListActivity.jump(activityData.getObjectId(),CirclePushCardActivity.this);
            }
        });

        new NowPupwindow(this);

        ((MyApplication)getApplicationContext()).showProgressDialog(this);
    }

    private void setADview(ActivityData activityData){
        activityName.setText(activityData.getActivityName());
        activityIntroduction.setText(activityData.getActivityIntroduction());
        String creatingTime=activityData.getCreatedAt();
        activityCreatingDate.setText(creatingTime.substring(0,10));
        frequency.setText(CommonUtils.frequencyString[activityData.getFrequency()]);
        exerciseAmount.setText(""+activityData.getExerciseAmount()+"m");
        Picasso.with(this).load(activityData.getBackgroundURL()).fit().noFade().into(activityBackground);
//        Target target = new Target() {
//            @Override
//            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
//                //替换背景
////                activityBackground.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
//                activityBackground.setBackground(new BitmapDrawable(getResources(), bitmap));
//            }
//
//            @Override
//            public void onBitmapFailed(Drawable drawable) {
//
//            }
//
//            @Override
//            public void onPrepareLoad(Drawable drawable) {
//
//            }
//        };
//
//        Picasso.with(this).load(activityData.getBackgroundURL()).noFade().into(target);
    }

    private long getCompletionRate(){
        int type=activityData.getFrequency();
        String join_time=activityAndMember.getCreatedAt();
        long supposed_punching_times=0;
        switch (type){
            case 0:supposed_punching_times=CommonUtils.getTwoDay(join_time)+1;break;
            case 1:supposed_punching_times=(CommonUtils.getTwoDay(join_time)+1)*2;break;
            case 2:supposed_punching_times=CommonUtils.getTwoWeek(join_time)+1;break;
            case 3:supposed_punching_times=(CommonUtils.getTwoWeek(join_time)+1)*2;break;
            case 4:supposed_punching_times=(CommonUtils.getTwoWeek(join_time)+1)*3;break;
            case 5:supposed_punching_times=CommonUtils.getTwoMonth(join_time)+1;break;
            default:break;
        }
        Log.e("database","type:"+type+",jointime:"+join_time+",week:"+CommonUtils.getTwoWeek(join_time));
        int punching_times=activityAndMember.getSignInTimes();
        if(punching_times==0)return 0;
        else {
            Log.e("database","supposed:"+supposed_punching_times+",punchingtimes:"+punching_times);
            long completion_rate =  punching_times * 100 / supposed_punching_times;
            return completion_rate;
        }
    }
    private void setMTAview(ActivityAndMember activityAndMember){
        String join_time= activityAndMember.getCreatedAt();
        int sign_in_times= activityAndMember.getSignInTimes();
        long join_days=CommonUtils.getTwoDay(join_time);

        beenInDays.setText(""+(join_days+1)+" days");
        signInTimes.setText(""+sign_in_times+" days");
    }

    private void setUserView(User user){
        int distance=user.getWalkDistance()+user.getRunDistance()+user.getWalkDistance();
        int kcal=user.getWalkKcal()+user.getRunKcal()+user.getRideKcal();
        long time=user.getRideTime()+user.getRunTime()+user.getWalkTime();
        long speed;
        if(time!=0)
         speed=distance/time;
        else speed=0;
        exerciseTime.setText(CommonUtils.secondsToHHmmss((int)time));
        cal.setText(""+kcal);
        averageSpeed.setText(""+speed+"km/h");
    }

    private void setMemberHeaderImage(List<ActivityAndMember> list){
        ArrayList<String> userIds=new ArrayList<>();
        for(int i=0;i<list.size();i++){
            ActivityAndMember activityAndMember =list.get(i);
            userIds.add(activityAndMember.getMemberId());
        }
        ArrayList<ImageView> aliv=new ArrayList<>();
        aliv.add(memberHeaderUrl1);
        aliv.add(memberHeaderUrl2);
        aliv.add(memberHeaderUrl3);
        aliv.add(memberHeaderUrl4);
        aliv.add(memberHeaderUrl5);
        aliv.add(memberHeaderUrl6);
        GetFromBmob.getUserImage(userIds,aliv,handler);
    }

    private boolean ifInCompletedState(){
        int type=activityData.getExerciseType();
        int amount=activityData.getExerciseAmount();
        int distance=0;
        switch (type){
            case 0:distance=user.getWalkDistance();break;
            case 1:distance=user.getRunDistance();break;
            case 2:distance=user.getRideDistance();break;
            default:break;
        }
        int result=distance/amount;
        int signInFlag=activityAndMember.getSignInFlag();
        if(result>signInFlag)return true;
        else return false;
    }

    private boolean ifCanPush(){
        int sign_in_flag=activityAndMember.getSignInFlag();
        int frequency=activityData.getFrequency();
        Log.e("database","frequency:"+frequency+",signInFlag:"+sign_in_flag);
        switch (frequency){
            case 0:if(sign_in_flag<1)return true;break;
            case 1:if(sign_in_flag<2)return true;break;
            case 2:if(sign_in_flag<1)return true;break;
            case 3:if(sign_in_flag<2)return true;break;
            case 4:if(sign_in_flag<3)return true;break;
            case 5:if(sign_in_flag<1)return true;break;
            default:break;
        }
        return false;
    }

    private void clearDayFlag(){
        long days=CommonUtils.getTwoDay(activityAndMember.getUpdatedAt());
        if(days>0) {
            activityAndMember.setSignInFlag(0);
            Log.e("database","clearDay");
        }
    }
    private void clearWeekFlag(){
        int weeks=CommonUtils.getTwoWeek(activityAndMember.getUpdatedAt());
        if (weeks>0) {
            activityAndMember.setSignInFlag(0);
            Log.e("database","clearWeek"+",id:"+activityAndMember.getObjectId());
        }
    }
    private void clearMonthFlag(){
        int months=CommonUtils.getTwoMonth(activityAndMember.getUpdatedAt());
        if(months>0) {
            activityAndMember.setSignInFlag(0);
            Log.e("database","clearMonth");
        }
    }
    private void setButton(){
        punchCard=(Button)findViewById(R.id.punchCard);
        //可以点，（如果上次是上周，次数先为0）次数+1，按键变化
        punchCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityAndMember.setSignInTimes(activityAndMember.getSignInTimes()+1);
                activityAndMember.setSignInFlag(activityAndMember.getSignInFlag()+1);
                GetFromBmob.updateAAM(activityAndMember,handler);

                punchCard.setText("√");
                punchCard.setEnabled(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1700);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        handler.sendEmptyMessage(7);
                    }
                }).start();
            }
        });
        //完成后
        int frequency=activityData.getFrequency();
        Log.e("database","frequency1:"+frequency);
        switch (frequency){
            case 0:clearDayFlag();break;
            case 1:clearDayFlag();break;
            case 2:clearWeekFlag();break;
            case 3:clearWeekFlag();break;
            case 4:clearWeekFlag();break;
            case 5:clearMonthFlag();break;
            default:break;
        }
        if(ifInCompletedState()&ifCanPush()){
            punchCard.setEnabled(true);
            punchCard.setBackgroundResource(R.mipmap.b2);
        } else {
            punchCard.setEnabled(false);
            punchCard.setBackgroundResource(R.mipmap.b3);
        }
    }
}
