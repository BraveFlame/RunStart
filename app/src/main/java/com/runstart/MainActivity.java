package com.runstart;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import com.runstart.bottom.CircleFragment;
import com.runstart.bottom.FriendsFragment;
import com.runstart.bottom.HeadPageFragment;
import com.runstart.bottom.MineFragment;


/**
 * 主界面的总体布局的实现
 * @author weizhi
 * @version 1.0
 */
public class MainActivity extends FragmentActivity {

    //定义FragmentLayout布局
    private FrameLayout frameLayout;
    //定义存放fragment的数组
    private Fragment[] mFragments;
    //定义RadioGroup组件
    private RadioGroup radioGroup;
    //定义选项index
    int mIndex;
    public static String activityKeep;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化底部状态栏的fragment
        initFragment();
        //底部状态栏切换fragment
        setRadioGroup();


    }


    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {

        return super.onCreateView(name, context, attrs);
    }

    @Override
    protected void onResume() {
        /**
         * 设置为竖屏
         */
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        super.onResume();
    }

    /**
     * 初始化Fragment框架
     */
    public void initFragment(){
        frameLayout = (FrameLayout) findViewById(R.id.fl_content);
        radioGroup = (RadioGroup) findViewById(R.id.rg_radioGroup);
        HeadPageFragment headPageFragment = new HeadPageFragment();
        FriendsFragment friendsFragment = new FriendsFragment();
        CircleFragment circleFragment=new CircleFragment();
        MineFragment mineFragment=new MineFragment();

        //初始化mFragments数组
        mFragments = new Fragment[]{headPageFragment, friendsFragment, circleFragment, mineFragment};
        //定义事务
        FragmentTransaction fg=getSupportFragmentManager().beginTransaction();
        //设置默认首页
        fg.add(R.id.fl_content,headPageFragment).commit();
        //默认设置为0
        setIndexSelected(0);

    }

    /**
     * 底层状态栏的切换选择的Item
     * @param index
     */
    private void setIndexSelected(int index) {
        if(mIndex==index){
            return;
        }
        //开始事务
        FragmentTransaction fg=getSupportFragmentManager().beginTransaction();
        //隐藏frgment
        fg.hide(mFragments[mIndex]);
        //判断是否添加fragment
        if (!mFragments[index].isAdded()) {
            fg.add(R.id.fl_content, mFragments[index]).show(mFragments[index]);
        }else {
            fg.show(mFragments[index]);
        }
        //提交事务
        fg.commit();
        //再次赋值
        mIndex=index;

    }

    /**
     * 底层状态栏的切换选择
     */
    private void setRadioGroup() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkId) {
                switch (checkId){
                    case R.id.rb_headPage:
                        setIndexSelected(0);
                        break;
                    case R.id.rb_friends:
                        setIndexSelected(1);
                        break;
                    case R.id.rb_circle:
                        setIndexSelected(2);
                        break;
                    case R.id.rb_mine:
                        setIndexSelected(3);
                        break;
                    default:
                        break;
                }
            }
        });
    }


}