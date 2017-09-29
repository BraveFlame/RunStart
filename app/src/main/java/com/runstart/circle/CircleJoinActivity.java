package com.runstart.circle;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.runstart.R;

import java.io.PushbackInputStream;


/**
 * Created by zhouj on 2017-09-26.
 */

public class CircleJoinActivity extends Activity implements View.OnClickListener {

    //定义PopupMenu菜单对象
    PopupMenu mMenu;
    //判断标志符是否关闭菜单
    boolean flag = false;
    //定义circle_join_popupmenu.xml布局的组件
    RelativeLayout rl_menu_first;
    RelativeLayout rl_menu_second;
    //定义fragment_circle_join_title.xml布局的组件
    ImageView join_iv_zuojiantou;
    private Button joinCardBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_circle_join);
        initView();
        initPopMenu();
    }

    /**
     * 初始化组件
     */
    public void initView() {
        //初始化fragment_circle_join_title.xml布局的组件
        join_iv_zuojiantou = (ImageView) findViewById(R.id.circle_join_iv_zuojiantou);
        join_iv_zuojiantou.setOnClickListener(this);
        joinCardBtn = (Button) findViewById(R.id.circle_join_card);
        joinCardBtn.setOnClickListener(this);
    }

    /**
     * 初始化popWindow菜单
     */
    public void initPopMenu() {

        //装载mine.layout.popup对应的界面布局
        View view = this.getLayoutInflater().inflate(R.layout.circle_join_popupmenu, null);

        //创建PopupWindow对象
        final PopupWindow popupWindow = new PopupWindow(view, 550, 250);
        popupWindow.setOutsideTouchable(true);
        Button btn_popupmenu = (Button) findViewById(R.id.circle_join_popupmenu);

        btn_popupmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!flag) {
                    //以下拉的方式来显示
//                popupWindow.showAsDropDown(v,20,30);
                    //将PopupWindow显示在指定的位置
                    popupWindow.showAtLocation(findViewById(R.id.circle_join_popupmenu), Gravity.RELATIVE_LAYOUT_DIRECTION, 210, -590);
                    flag = true;
                } else {
                    popupWindow.dismiss();
                    flag = false;
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
                        flag = false;
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
        switch (v.getId()) {

            case R.id.circle_join_iv_zuojiantou:        //点击回退到前一个activity
                CircleJoinActivity.this.finish();
                break;
            //实现菜单circle_join_popupmenu.xml布局中的事件监听
            case R.id.circle_join_popupmenu_rl_first:
                Intent createActivityIntent = new Intent(CircleJoinActivity.this, CircleCreateActivity.class);
                startActivity(createActivityIntent);
                break;
            case R.id.circle_join_popupmenu_rl_second:
                final Dialog dialog = new AlertDialog.Builder(CircleJoinActivity.this, R.style.Dialog_FS).create();
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

            //模拟已经加入活动后打卡
            case R.id.circle_join_card:
                CirclePushCardActivity.jump("jmKi777W","033b152e41",CircleJoinActivity.this);
                break;
            default:
                break;
        }

    }
}
