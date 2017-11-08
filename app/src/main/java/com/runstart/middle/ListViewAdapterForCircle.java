package com.runstart.middle;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.runstart.R;
import com.runstart.bean.ActivityTopicForCircle;
import com.squareup.picasso.Picasso;

import java.util.List;
/**
 * Created by zhouj on 2017-09-19.
 */
final class ViewHolderForCircle {
    public ImageView topicImage;
    public TextView topicTitle;
    public ImageView userHeadImage;
    public TextView userName;
}

public class ListViewAdapterForCircle extends BaseAdapter {
    private LayoutInflater mLayoutInflater;
    private List<ActivityTopicForCircle> topicList;
    private Context context;

    public void setTopicList(List<ActivityTopicForCircle> topicList) {
        this.topicList = topicList;
    }

    public List<ActivityTopicForCircle> getTopicList() {
        return topicList;
    }

    public ListViewAdapterForCircle(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        this.context=context;
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
        ViewHolderForCircle viewHolderForCircle =null;
        if (convertView == null) {
            viewHolderForCircle =new ViewHolderForCircle();
            convertView = mLayoutInflater.inflate(R.layout.walk_myactivity_listitem, null);
            viewHolderForCircle.topicImage = (ImageView) convertView.findViewById(R.id.topic_image);
            viewHolderForCircle.topicTitle = (TextView) convertView.findViewById(R.id.topic_title);
            viewHolderForCircle.userHeadImage = (ImageView) convertView.findViewById(R.id.topic_user_headImage);
            viewHolderForCircle.userName = (TextView) convertView.findViewById(R.id.topic_user_name);
            convertView.setTag(viewHolderForCircle);
        }else {
            viewHolderForCircle =(ViewHolderForCircle)convertView.getTag();
        }
        Picasso.with(context).load(topicList.get(position).getUserHeadImage()).fit().noFade().into(viewHolderForCircle.userHeadImage);
        Picasso.with(context).load(topicList.get(position).getTopicImage()).fit().noFade().into(viewHolderForCircle.topicImage);
        viewHolderForCircle.topicTitle.setText(topicList.get(position).getTopicTitle());
        viewHolderForCircle.userName.setText((topicList.get(position).getUserName()));
        return convertView;
    }
}
