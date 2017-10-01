package com.runstart.bottom;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.drawable.DrawableWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;


import com.runstart.R;
import com.runstart.friend.friendactivity.CreateGroupActivity;
import com.runstart.friend.friendfragment.GroupFragment;
import com.runstart.friend.friendfragment.MyFriendsFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 17-9-1.
 */
public class FriendsFragment extends Fragment{

    private Fragment[] mFragments;
    private MyFriendsFragment myFriendsFragment;
    private GroupFragment groupFragment;
    private RadioButton[] radioButtons, radioButtons1;
    private RadioGroup bottomRadioGroup, bottomRadioGroup1;
    private View view;

    private Button menuButton;
    private PopupWindow menuPopupWindow;
    private boolean flag = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int[] menuIons = new int[]{R.mipmap.ic_jiahaoyou, R.mipmap.group_10, R.mipmap.ic_xiaoxi, R.mipmap.ic_fenxian};
        String[] menuNames = new String[]{"add friend", "create a group", "news", "invite friends"};
        final String[] from = new String[]{"menuIcon", "menuName"};
        int[] to = new int[]{R.id.item_image, R.id.item_text};
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < menuNames.length; i++){
            Map<String, Object> map = new HashMap<>();
            map.put(from[0], menuIons[i]);
            map.put(from[1], menuNames[i]);
            list.add(map);
        }
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.menu_view, null);
        ListView listView = (ListView) linearLayout.findViewById(R.id.menuListView);
        SimpleAdapter adapter = new SimpleAdapter(getContext(), list, R.layout.item_menu, from, to);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String menuName = ((TextView) view.findViewById(R.id.item_text)).getText().toString();
                switch (menuName){
                    case "add friend":

                        menuPopupWindow.dismiss();
                        break;
                    case "create a group":
                        startActivity(new Intent(getContext(), CreateGroupActivity.class));
                        menuPopupWindow.dismiss();
                        break;
                    case "news":

                        menuPopupWindow.dismiss();
                        break;
                    case "invite friends":

                        menuPopupWindow.dismiss();
                        break;
                }
            }
        });
        menuPopupWindow = new PopupWindow(linearLayout,
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //menuPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.mipmap.rectangle_26copy_2, null));
        menuPopupWindow.setOutsideTouchable(true);
        menuPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(200);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        flag = true;
                    }
                }).start();
            }
        });

        myFriendsFragment = new MyFriendsFragment();
        groupFragment = new GroupFragment();
        mFragments = new Fragment[]{myFriendsFragment, groupFragment};
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friends, container, false);

        bottomRadioGroup = (RadioGroup) view.findViewById(R.id.fbuttonRadioGroup);
        bottomRadioGroup1 = (RadioGroup) view.findViewById(R.id.fbuttonRadioGroup1);
        menuButton = (Button)view.findViewById(R.id.menuButton);
        radioButtons = new RadioButton[]{(RadioButton)view.findViewById(R.id.friends), (RadioButton)view.findViewById(R.id.group)};
        radioButtons1 = new RadioButton[]{(RadioButton)view.findViewById(R.id.friends1), (RadioButton)view.findViewById(R.id.group1)};
        radioButtons[0].setChecked(true);
        radioButtons1[0].setChecked(true);
        radioButtons[0].setTextColor(0xff21187f);
        radioButtons[1].setTextColor(0xff878787);
        final ViewPager viewPager = (ViewPager)view.findViewById(R.id.viewPager);
        FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(getActivity().getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }
            @Override
            public int getCount() {
                return mFragments.length;
            }
        };

        bottomRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.friends:
                        viewPager.setCurrentItem(0);
                        radioButtons1[0].setChecked(true);
                        break;
                    case R.id.group:
                        viewPager.setCurrentItem(1);
                        radioButtons1[1].setChecked(true);
                        break;
                }
            }
        });
        bottomRadioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.friends1:
                        viewPager.setCurrentItem(0);
                        radioButtons[0].setChecked(true);
                        break;
                    case R.id.group1:
                        viewPager.setCurrentItem(1);
                        radioButtons[1].setChecked(true);
                        break;
                }
            }
        });
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                radioButtons[position].setTextColor(0xff21187f);
                radioButtons1[position].setChecked(true);
                radioButtons[1 - position].setTextColor(0xff878787);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag){
                    menuPopupWindow.showAsDropDown(menuButton, 0, 30);
                    flag = false;
                }else {
                    menuPopupWindow.dismiss();
                }
            }
        });
    }

}
