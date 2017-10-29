package com.runstart.circle.circle_listview;

import android.content.Context;
import android.widget.ListView;

import java.util.List;
import java.util.Map;

/**
 * Created by 10605 on 2017/10/9.
 */

public class NowJianjiansListView {
    public static void useAdapter(Context context, ListView listView,List<CircleActivityTopic> topicList,Map map){
        CircleListViewAdapter listViewAdapter=new CircleListViewAdapter(context);
        listViewAdapter.setTopicList(topicList);
        listView.setAdapter(listViewAdapter);
    }
}
