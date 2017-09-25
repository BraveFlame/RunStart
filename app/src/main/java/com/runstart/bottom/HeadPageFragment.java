package com.runstart.bottom;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;


import com.runstart.R;
import com.runstart.sport_fragment.MFragmentPagerAdapter;
import com.runstart.sport_fragment.RideFragment;
import com.runstart.sport_fragment.RunFragment;
import com.runstart.sport_fragment.WalkFragment;

import java.util.ArrayList;

/**
 * 主界面显示，包括步行、跑步和骑行三个选项
 * @author weizhi
 * @version 1.0
 */

public class HeadPageFragment extends BaseFragment {

    //步行
    private TextView walkTextView;
    //跑步
    private TextView runTextView;
    //骑行
    private TextView rideTextView;

    //实现Tab滑动效果
    private ViewPager mViewPager;

    //动画图片
    private ImageView cursor;

    //动画图片偏移量
    private int offset = 0;
    private int position_one;
    private int position_two;

    //动画图片宽度
    private int bmpW;

    //当前页卡编号
    private int currIndex = 0;

    //存放Fragment
    private ArrayList<Fragment> fragmentArrayList;

    //管理Fragment
    private FragmentManager fragmentManager;

    public Context context;

    //定义View
    private  View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_headpage, container, false);
        //初始化TextView
        InitTextView();
        //初始化ImageView
        InitImageView();
        //初始化Fragment
        InitFragment();
        //初始化ViewPager
        InitViewPager();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
//        //初始化TextView
//        InitTextView();
//        //初始化ImageView
//        InitImageView();
//        //初始化Fragment
//        InitFragment();
//        //初始化ViewPager
//        InitViewPager();
    }

    /**
     * 初始化头标
     */
    private void InitTextView(){

        //步行头标
        walkTextView = (TextView)view.findViewById(R.id.walk_text);
        //跑步头标
        runTextView = (TextView) view.findViewById(R.id.run_text);
        //骑行头标
        rideTextView = (TextView)view.findViewById(R.id.ride_text);

        //添加点击事件
        walkTextView.setOnClickListener(new MyOnClickListener(0));
        runTextView.setOnClickListener(new MyOnClickListener(1));
        rideTextView.setOnClickListener(new MyOnClickListener(2));
    }

    /**
     * 初始化页卡内容区
     */
    private void InitViewPager() {

        mViewPager = (ViewPager) view.findViewById(R.id.vPager);
        mViewPager.setAdapter(new MFragmentPagerAdapter(fragmentManager, fragmentArrayList));

        //让ViewPager缓存2个页面
        mViewPager.setOffscreenPageLimit(2);

        //设置默认打开第一页
       // mViewPager.setCurrentItem(0);

        //将顶部文字恢复默认值
        resetTextViewTextColor();
        walkTextView.setTextColor(getResources().getColor(R.color.main_top_tab_color_2));

        //设置viewpager页面滑动监听事件
        mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());

    }

    /**
     * 初始化动画
     */
    private void InitImageView() {
        cursor = (ImageView) view.findViewById(R.id.cursor);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        // 获取分辨率宽度
        int screenW = dm.widthPixels;

        bmpW = (screenW/3);

        //设置动画图片宽度
        setBmpW(cursor, bmpW);
        offset = 0;

        //动画图片偏移量赋值
        position_one = (int) (screenW / 3.0);
        position_two = position_one * 2;

    }

    /**
     * 初始化Fragment，并添加到ArrayList中
     */
    private void InitFragment(){
        fragmentArrayList = new ArrayList<Fragment>();
        fragmentArrayList.add(new WalkFragment());
        fragmentArrayList.add(new RunFragment());
        fragmentArrayList.add(new RideFragment());
        fragmentManager = getActivity().getSupportFragmentManager();

    }

    /**
     * 头标点击监听
     * @author weizhi
     * @version 1.0
     */
    public class MyOnClickListener implements View.OnClickListener{
        private int index = 0 ;
        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            mViewPager.setCurrentItem(index);
        }
    }

    /**
     * 页卡切换监听
     * @author weizhi
     * @version 1.0
     */
    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageSelected(int position) {
            Animation animation = null ;
            switch (position){

                //当前为页卡1
                case 0:
                    //从页卡2跳转转到页卡1
                    if(currIndex == 1){
                        animation = new TranslateAnimation(position_one, 0, 0, 0);
                        resetTextViewTextColor();
                        walkTextView.setTextColor(getContext().getColor(R.color.main_top_tab_color_2));

                    }else if(currIndex == 2){//从页卡3跳转转到页卡1
                        animation = new TranslateAnimation(position_two, 0, 0, 0);
                        resetTextViewTextColor();
                        walkTextView.setTextColor(getContext().getColor(R.color.main_top_tab_color_2));
                    }
                    break;

                //当前为页卡2
                case 1:

                    //从页卡1跳转转到页卡2
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(offset, position_one, 0, 0);
                        resetTextViewTextColor();
                        runTextView.setTextColor(getContext().getColor(R.color.main_top_tab_color_2));

                    } else if (currIndex == 2) { //从页卡3跳转转到页卡2
                        animation = new TranslateAnimation(position_two, position_one, 0, 0);
                        resetTextViewTextColor();
                        runTextView.setTextColor(getContext().getColor(R.color.main_top_tab_color_2));
                    }
//
                    break;

                //当前为页卡3
                case 2:
                    //从页卡1跳转转到页卡3
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(offset, position_two, 0, 0);
                        resetTextViewTextColor();
                        rideTextView.setTextColor(getContext().getColor(R.color.main_top_tab_color_2));
                    } else if (currIndex == 1) {
                        //从页卡2跳转转到页卡3
                        animation = new TranslateAnimation(position_one, position_two, 0, 0);
                        resetTextViewTextColor();
                        rideTextView.setTextColor(getContext().getColor(R.color.main_top_tab_color_2));
                    }
                    break;
            }
            currIndex = position;

            animation.setFillAfter(true);// true:图片停在动画结束位置
            animation.setDuration(300);
            cursor.startAnimation(animation);

        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    /**
     * 设置动画图片宽度
     * @param mWidth
     */
    private void setBmpW(ImageView imageView,int mWidth){
        ViewGroup.LayoutParams para;
        para = imageView.getLayoutParams();
        para.width = mWidth;
        imageView.setLayoutParams(para);
    }

    /**
     * 将顶部文字恢复默认值
     */
    private void resetTextViewTextColor(){

        walkTextView.setTextColor(getResources().getColor(R.color.main_top_tab_color));
        runTextView.setTextColor(getResources().getColor(R.color.main_top_tab_color));
        rideTextView.setTextColor(getResources().getColor(R.color.main_top_tab_color));
    }
}
