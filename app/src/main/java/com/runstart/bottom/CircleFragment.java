package com.runstart.bottom;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


import com.runstart.R;
import com.runstart.bean.ActivityTopic;
import com.runstart.circle.CircleCreateActivity;
import com.runstart.circle.CircleJoinActivity;
import com.runstart.middle.ListViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouj on 2017-09-08.
 */

public class CircleFragment extends BaseFragment{
    ListView firstListView;
    ListView secondListView;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_circle, container, false);
        initView();
        useAdapter();
        setOnItemListener();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * 初始化view
     */
    public void initView() {
        firstListView = (ListView) view.findViewById(R.id.circle_recommendactivity_first_listview);
        secondListView = (ListView) view.findViewById(R.id.circle_recommendactivity_second_listview);
        FloatingActionButton btn_floataction = (FloatingActionButton) view.findViewById(R.id.circle_btnFloatingAction);
        btn_floataction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createActivityIntent = new Intent(getActivity(), CircleCreateActivity.class);
                startActivity(createActivityIntent);

            }
        });
    }

    /**
     * 初始化数据
     */
    public List<ActivityTopic> getActivityTopicData() {
        List<ActivityTopic> topicList = new ArrayList<>();

        ActivityTopic activityTopic = new ActivityTopic();
        activityTopic.setTopicImage(String.valueOf(R.mipmap.bitmap_walk));
        activityTopic.setTopicTitle("every day");
        activityTopic.setUserHeadImage(String.valueOf(R.mipmap.arvin_febry_302935_copy3));
        activityTopic.setUserName("alien");
        activityTopic.setTopicProgressbar(String.valueOf(R.mipmap.progressbar));
        topicList.add(activityTopic);
        ActivityTopic activityTopic2 = new ActivityTopic();
        activityTopic2.setTopicImage(String.valueOf(R.mipmap.bitmap_walk));
        activityTopic2.setTopicTitle("every day");
        activityTopic2.setUserHeadImage(String.valueOf(R.mipmap.arvin_febry_302935_copy3));
        activityTopic2.setUserName("alien");
        activityTopic2.setTopicProgressbar(String.valueOf(R.mipmap.progressbar));
        topicList.add(activityTopic2);

        return topicList;
    }

    /**
     * 使用ListViewAdapter
     */
    public void useAdapter() {
        List<ActivityTopic> topicList = getActivityTopicData();
        ListViewAdapter listViewAdapter = new ListViewAdapter(getContext());
        listViewAdapter.setTopicList(topicList);
       firstListView.setAdapter(listViewAdapter);

        secondListView.setAdapter(listViewAdapter);

    }
    /**
     * 创建ListView的OnItemListener事件监听
     */
    public void setOnItemListener(){
        firstListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent joinIntent = new Intent(getActivity(), CircleJoinActivity.class);
                startActivity(joinIntent);
//                Toast.makeText(getActivity(),"点击了 "+position,Toast.LENGTH_LONG).show();
            }
        });
    }


}
