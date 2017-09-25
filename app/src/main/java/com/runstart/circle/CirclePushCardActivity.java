package com.runstart.circle;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.runstart.R;

/**
 * Created by zhouj on 2017-09-21.
 */

public class CirclePushCardActivity extends Activity implements PopupMenu.OnMenuItemClickListener{
    //定义PopupMenu菜单对象
    PopupMenu mMenu;
    //判断标志符是否关闭菜单
    boolean flag=false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_circle_pushcard);

        initPopMenu();

    }

    /**
     * 初始化popWindow菜单
     */
    public void initPopMenu(){

        //装载mine.layout.popup对应的界面布局
        View view = this.getLayoutInflater().inflate(R.layout.mine_putcard_popup, null);
        //创建PopupWindow对象
        final PopupWindow popupWindow=new PopupWindow(view,550,550);
        popupWindow.setOutsideTouchable(true);
        Button btn_popupmenu = (Button) findViewById(R.id.circle_pushcard_popupmenu);

        btn_popupmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!flag) {
                    //以下拉的方式来显示
//                popupWindow.showAsDropDown(v,20,30);
                    //将PopupWindow显示在指定的位置
                    popupWindow.showAtLocation(findViewById(R.id.circle_pushcard_popupmenu), Gravity.RELATIVE_LAYOUT_DIRECTION, 220, -435);
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



    }
    /**
     * 初始化popMenu菜单
     */
    public void initMenu(){
        View view = findViewById(R.id.circle_pushcard_popupmenu);
        if (view != null) {
            mMenu = new PopupMenu(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMenu.show();

                }
            });
            mMenu.getMenuInflater().inflate(R.menu.mine_pushcard_title_menu, mMenu.getMenu());
            mMenu.setOnMenuItemClickListener(this);
        }
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.mine_pushcard_menu_newactivity:
                Toast.makeText(this, "您点击了“未知“按钮", Toast.LENGTH_SHORT).show();
                break;
            case R.id.mine_pushcard_menu_exitactivity:
                Toast.makeText(this, "您点击了“kfd“按钮", Toast.LENGTH_SHORT).show();
                break;
            case R.id.mine_pushcard_menu_news:
                Toast.makeText(this, "您点击了“sdfg“按钮", Toast.LENGTH_SHORT).show();
                break;
            case R.id.mine_pushcard_menu_invitefriends:
                Toast.makeText(this, "您点击了“未ssdf“按钮", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return false;
    }

}
