package com.runstart.circle;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;


import com.runstart.bean.ActivityAndMember;
import com.runstart.bean.ActivityData;
import com.runstart.R;
import com.runstart.MyApplication;
import com.runstart.circle.circle_activity.CircleCreateActivity;
import com.runstart.circle.circle_activity.CirclePushCardActivity;
import com.runstart.mine.mine_activity.MineMessageRecordActivity;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by zhouj on 2017-09-21.
 */

public class NowPupwindow {

    public MyApplication myApplication;
    public CirclePushCardActivity activity;

    //定义PopupMenu菜单对象
    PopupMenu mMenu;
    //判断标志符是否关闭菜单
    boolean flag=false;

    public NowPupwindow(CirclePushCardActivity activity){
        this.activity=activity;
        initPopMenu();
        myApplication=(MyApplication)activity.getApplicationContext();
    }

    /**
     * 初始化popWindow菜单
     */
    private void initPopMenu(){
        //装载mine.layout.popup对应的界面布局
        View view = activity.getLayoutInflater().inflate(R.layout.circle_putcard_popupmenu, null);

        //创建PopupWindow对象
        final PopupWindow popupWindow=new PopupWindow(view,550,400);
        popupWindow.setOutsideTouchable(true);
        Button btn_popupmenu = (Button) activity.findViewById(R.id.circle_pushcard_popupmenu);
        btn_popupmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!flag) {
                    //以下拉的方式来显示
//                popupWindow.showAsDropDown(v,20,30);
                    //将PopupWindow显示在指定的位置
                    popupWindow.showAtLocation(activity.findViewById(R.id.circle_pushcard_popupmenu), Gravity.RELATIVE_LAYOUT_DIRECTION, 220, -530);
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

        //三行分别设置点击的事件
        view.findViewById(R.id.rr_pushcard_popupmenu_new_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                CircleCreateActivity.jump(activity);
                activity.finish();
            }
        });
        view.findViewById(R.id.rr_pushcard_popupmenu_exit_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                if(!activity.activityData.getCreatorId().equals(activity.initMyId)) {
                    new AlertDialog.Builder(activity).setTitle("Make Sure").setMessage("Are you sure to exit?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            delete(activity.activityAndMember.getObjectId());
                            ((MyApplication)activity.getApplicationContext()).showProgressDialog(activity);
                        }
                    }).setNegativeButton("No", null).show();
                } else {
                    new AlertDialog.Builder(activity).setTitle("Make Sure").setMessage("Are you sure to delete this activity?").
                            setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteActivityMembersByActivityId(activity.activityData.getObjectId());
                                    deleteActivityData(activity.activityData.getObjectId());
                                    deletePicture(activity.activityData.getBackgroundURL());
                                    ((MyApplication)activity.getApplicationContext()).showProgressDialog(activity);
                                }
                            }).setNegativeButton("No", null).show();
                }
            }
        });
        view.findViewById(R.id.rr_pushcard_popupmenu_news).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                activity.startActivity(new Intent(activity, MineMessageRecordActivity.class));
                activity.finish();
            }
        });
    }

    //////////////////////////////////////以下是几个删除服务器上数据滴方法//////////////////////////
    Handler handler=new Handler(){

        int k=0;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            k++;
            if(k==3){
                k=0;
                myApplication.isFragmentWalkShouldRefresh=true;
                ((MyApplication)activity.getApplicationContext()).stopProgressDialog();
                activity.finish();
            }
        }
    };

    public void delete(String id){
        final ActivityAndMember activityAndMember=new ActivityAndMember();
        activityAndMember.setObjectId(id);
        activityAndMember.delete(new UpdateListener() {

            @Override
            public void done(BmobException e) {
                ((MyApplication)activity.getApplicationContext()).isFragmentWalkShouldRefresh=true;
                ((MyApplication)activity.getApplicationContext()).stopProgressDialog();
                activity.finish();
                if(e==null){
                    Log.e("database","删除成功:"+activityAndMember.getUpdatedAt());
                }else{
                    Log.e("database","删除失败：" + e.getMessage());
                }
            }

        });
    }

    public void deleteActivityData(String id){
        final ActivityData activityData=new ActivityData();
        activityData.setObjectId(id);
        activityData.delete(new UpdateListener() {

            @Override
            public void done(BmobException e) {
                handler.sendEmptyMessage(1);
                if(e==null){
                    Log.e("database","删除成功:"+activityData.getUpdatedAt());
                }else{
                    Log.e("database","删除失败：" + e.getMessage());
                }
            }
        });
    }

    public void deleteActivityMembersByActivityId(String id){
        BmobQuery<ActivityAndMember> bmobQuery = new BmobQuery<ActivityAndMember>();
        bmobQuery.addWhereEqualTo("activityId",id);
        bmobQuery.findObjects(new FindListener<ActivityAndMember>() {
            @Override
            public void done(List<ActivityAndMember> object, BmobException e) {
                handler.sendEmptyMessage(1);
                if(e==null){
                    for (ActivityAndMember gameScore : object) {
                        gameScore.delete(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                Log.e("database","删除成功");
                            }
                        });
                    }
                }else{
                    Log.e("bmob","活动成员 失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    public void deletePicture(String url){
        BmobFile file = new BmobFile();
        file.setUrl(url);//此url是上传文件成功之后通过bmobFile.getUrl()方法获取的。
        file.delete(new UpdateListener() {

            @Override
            public void done(BmobException e) {
                handler.sendEmptyMessage(1);
                if(e==null){
                    Log.e("database","文件删除成功");
                }else{
                    Log.e("database","文件删除失败："+e.getErrorCode()+","+e.getMessage());
                }
            }
        });
    }

}
