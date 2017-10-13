package com.runstart.circle;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.zhouj.viewpagerdemo.BmobBean.ActivityAndMember;
import com.example.zhouj.viewpagerdemo.BmobBean.ActivityData;
import com.example.zhouj.viewpagerdemo.MyApplication;
import com.example.zhouj.viewpagerdemo.R;
import com.example.zhouj.viewpagerdemo.middle.FragmentWalkSecondPage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouj on 2017-09-26.
 */

public class CircleJoinActivity extends Activity implements View.OnClickListener{

    //定义PopupMenu菜单对象
    PopupMenu mMenu;
    //判断标志符是否关闭菜单
    boolean flag=false;
    //定义circle_join_popupmenu.xml布局的组件
    RelativeLayout rl_menu_first;
    RelativeLayout rl_menu_second;
    //定义fragment_circle_join_title.xml布局的组件
    ImageView join_iv_zuojiantou;
    TextView activityName,activityIntroduction,activityCreatingDate,frequency,exerciseAmount;
    ImageView activityBackground;
    ImageView memberHeaderUrl1,memberHeaderUrl2,memberHeaderUrl3,memberHeaderUrl4,memberHeaderUrl5,memberHeaderUrl6;
    Button joinButton;

    private static String activityId,userId;
    private ActivityData activityData;

    Handler handler=new Handler(){
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
                    //取出，用于判断是否完成任务，与上方无关
                    CircleJoinActivity.this.activityData=activityData;
                    break;
                case 2:
                    ((MyApplication)getApplicationContext()).stopProgressDialog();
                    setContentView(R.layout.fragment_circle_join_when_activity_deleted);
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
                //加入activity成功后，触发，进入该activity
                case 7:
                    CirclePushCardActivity.jump(activityId,CircleJoinActivity.this);
                    ((MyApplication)getApplicationContext()).isFragmentWalkShouldRefresh=true;
                    finish();
                    break;
                case 8:
                    joinButton.setEnabled(false);
                    break;
                case 9:
                    joinButton.setEnabled(true);
                    break;
                default:break;
            }
        }
    };

    public static void jump(String activityId, Activity activity){
        Intent intent=new Intent(activity,CircleJoinActivity.class);
        intent.putExtra("activityId",activityId);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_circle_join);
        init_data();
        initView();
        initPopMenu();
    }

    private void init_data(){
        this.activityId=getIntent().getStringExtra("activityId");
        this.userId=((MyApplication)getApplicationContext()).applicationMap.get("userObjectId");
        GetFromBmob.getActivityMember(activityId,handler);
        GetFromBmob.getAD(activityId,handler);
        GetFromBmob.getOneAAM(activityId,userId,handler);
    }

    /**
     * 初始化组件
     */
    private void initView(){
        //初始化fragment_circle_join_title.xml布局的组件
        join_iv_zuojiantou = (ImageView) findViewById(R.id.circle_join_iv_zuojiantou);
//        TextView activityName,activityIntroduction,activityCreatingDate,frequency,exerciseAmount;
        activityName=(TextView)findViewById(R.id.activityName);
        activityIntroduction=(TextView)findViewById(R.id.activityIntroduction);
        activityCreatingDate=(TextView)findViewById(R.id.activityCreatingDate);
        frequency=(TextView)findViewById(R.id.frequency);
        exerciseAmount=(TextView)findViewById(R.id.exerciseAmount);
        activityBackground=(ImageView)findViewById(R.id.activityBackground);

        memberHeaderUrl1=(ImageView)findViewById(R.id.circle_pushcard_iv_userimage_first);
        memberHeaderUrl2=(ImageView)findViewById(R.id.circle_pushcard_iv_userimage_second);
        memberHeaderUrl3=(ImageView)findViewById(R.id.circle_pushcard_iv_userimage_three);
        memberHeaderUrl4=(ImageView)findViewById(R.id.circle_pushcard_iv_userimage_four);
        memberHeaderUrl5=(ImageView)findViewById(R.id.circle_pushcard_iv_userimage_five);
        memberHeaderUrl6=(ImageView)findViewById(R.id.circle_pushcard_iv_userimage_six);

        findViewById(R.id.ll_friend_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CircleFriendListActivity.jump(activityId,CircleJoinActivity.this);
            }
        });
        joinButton=(Button) findViewById(R.id.circle_join_button);
        joinButton.setOnClickListener(this);
        join_iv_zuojiantou.setOnClickListener(this);

        ((MyApplication)getApplicationContext()).showProgressDialog(this);
    }
    /**
     * 初始化popWindow菜单
     */
    private void initPopMenu(){

        //装载mine.layout.popup对应的界面布局
        View view = this.getLayoutInflater().inflate(R.layout.circle_join_popupmenu, null);

        //创建PopupWindow对象
        final PopupWindow popupWindow=new PopupWindow(view,550,250);
        popupWindow.setOutsideTouchable(true);
        Button btn_popupmenu = (Button) findViewById(R.id.circle_join_popupmenu);

        btn_popupmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!flag) {
                    //以下拉的方式来显示
//                popupWindow.showAsDropDown(v,20,30);
                    //将PopupWindow显示在指定的位置
                    popupWindow.showAtLocation(findViewById(R.id.circle_join_popupmenu), Gravity.RELATIVE_LAYOUT_DIRECTION, 210, -590);
                    flag = true;
                }else {
                    popupWindow.dismiss();
                    flag=false;
                }
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        flag=false;
                    }
                }).start();
            }
        });
        //初始化circle_join_popupmenu.xml布局的组件
        rl_menu_first = (RelativeLayout) view.findViewById(R.id.circle_join_popupmenu_rl_first);
        rl_menu_first.setOnClickListener(this);
        rl_menu_second = (RelativeLayout) view.findViewById(R.id.circle_join_popupmenu_rl_second);
        rl_menu_second.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //点击后改动数据库，加入activity
            case R.id.circle_join_button:
                GetFromBmob.addAAM(activityId,userId,handler);
                break;
            case R.id.circle_join_iv_zuojiantou:        //点击回退到前一个activity
                CircleJoinActivity.this.finish();
                break;
            //实现菜单circle_join_popupmenu.xml布局中的事件监听
            case R.id.circle_join_popupmenu_rl_first:
//                Intent createActivityIntent = new Intent(CircleJoinActivity.this, CircleCreateActivity.class);
//                startActivity(createActivityIntent);
                break;
            case R.id.circle_join_popupmenu_rl_second:
                final Dialog dialog = new AlertDialog.Builder(CircleJoinActivity.this,R.style.Dialog_FS).create();
                dialog.show();

                /*添加对话框的自定义的布局*/
                dialog.setContentView(R.layout.dialog_circle_join_menu_invitefriends);
                /*获取对话框的窗口*/
                Window dialogWindow = dialog.getWindow();
                /*设置显示从对话框窗口的宽高*/
                dialogWindow.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                /*设置对话框的显示位置*/
                dialogWindow.setGravity(Gravity.BOTTOM);
                /*通过dialogWindow找寻布局中的控件*/

                break;
            default:
                break;
        }
    }

    ///////////////////////////////////////////
    //各种获取数据并布局到activity
    ///////////////////////////////////////////
    private void setADview(ActivityData activityData){
        activityName.setText(activityData.getActivityName());
        activityIntroduction.setText(activityData.getActivityIntroduction());
        String creatingTime=activityData.getCreatedAt();
        activityCreatingDate.setText(creatingTime.substring(0,10));
        frequency.setText(CommonUtils.frequencyString[activityData.getFrequency()]);
        exerciseAmount.setText(""+activityData.getExerciseAmount()+"m");
        Picasso.with(this).load(activityData.getBackgroundURL()).fit().noFade().into(activityBackground);
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
}
