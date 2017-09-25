package com.runstart.middle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.runstart.R;
import com.runstart.bean.ActivityTopic;

import java.util.List;


/**
 * Created by zhouj on 2017-09-19.
 */
final class ViewHolder{
    public ImageView topicImage;
    public TextView topicTitle;
    public ImageView userHeadImage;
    public TextView userName;
    public ImageView topicProgressbar;
}

public class ListViewAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;
    private List<ActivityTopic> topicList;

    public void setTopicList(List<ActivityTopic> topicList) {
        this.topicList = topicList;
    }

    public List<ActivityTopic> getTopicList() {
        return topicList;
    }

    public ListViewAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return topicList.size();
    }

    @Override
    public Object getItem(int position) {
        return topicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if (convertView == null) {
            viewHolder=new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.walk_secondpage_listitem, null);
            viewHolder.topicImage = (ImageView) convertView.findViewById(R.id.topic_image);
            viewHolder.topicTitle = (TextView) convertView.findViewById(R.id.topic_title);
            viewHolder.userHeadImage = (ImageView) convertView.findViewById(R.id.topic_user_headImage);
            viewHolder.userName = (TextView) convertView.findViewById(R.id.topic_user_name);
            viewHolder.topicProgressbar = (ImageView) convertView.findViewById(R.id.topic_progressbar);
            convertView.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolder)convertView.getTag();
        }
        viewHolder.topicImage.setBackgroundResource(Integer.parseInt(topicList.get(position).getTopicImage()));
        viewHolder.topicTitle.setText(topicList.get(position).getTopicTitle());
        viewHolder.userHeadImage.setImageResource(Integer.parseInt(topicList.get(position).getUserHeadImage()));
        viewHolder.userName.setText((topicList.get(position).getUserName()));
        viewHolder.topicProgressbar.setBackgroundResource(Integer.parseInt(topicList.get(position).getTopicProgressbar()));
        return convertView;
    }
}
