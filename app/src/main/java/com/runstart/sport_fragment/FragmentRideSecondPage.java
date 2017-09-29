package com.runstart.sport_fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.runstart.R;
import com.runstart.bean.ActivityTopic;
import com.runstart.middle.ListViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 17-9-26.
 */

public class FragmentRideSecondPage extends Fragment {
    ListView myListView;
    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_walk_secondpage, container, false);
        initView();
        useAdapter();
        return view;
    }
    /**
     * 初始化view
     */
    public void initView(){
        myListView = (ListView) view.findViewById(R.id.lv_walk_myactivity);
    }
    /**
     * 初始化数据
     */
    public List<ActivityTopic> getActivityTopicData(){
        List<ActivityTopic> topicList = new ArrayList<>();

        ActivityTopic activityTopic=new ActivityTopic();
        activityTopic.setTopicImage(String.valueOf(R.mipmap.bitmap_walk));
        activityTopic.setTopicTitle("every day");
        activityTopic.setUserHeadImage(String.valueOf(R.mipmap.arvin_febry_302935_copy3));
        activityTopic.setUserName("alien");
        activityTopic.setTopicProgressbar(String.valueOf(R.mipmap.progressbar));
        topicList.add(activityTopic);
        ActivityTopic activityTopic2=new ActivityTopic();
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
    public  void useAdapter(){
        List<ActivityTopic> topicList=getActivityTopicData();
        ListViewAdapter listViewAdapter=new ListViewAdapter(getContext());
        listViewAdapter.setTopicList(topicList);
        myListView.setAdapter(listViewAdapter);

    }
}
