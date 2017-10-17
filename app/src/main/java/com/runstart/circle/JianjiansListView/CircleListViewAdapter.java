package com.runstart.circle.JianjiansListView;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.runstart.BmobBean.Friend;
import com.runstart.BmobBean.User;
import com.runstart.R;
import com.runstart.circle.GetFromBmob;
import com.runstart.history.MyApplication;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

final class CircleViewHolder {
    public TextView number,name,distance,like_times;
    public ImageView header,like;
    public LinearLayout likeLL;
}

public class CircleListViewAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;
    private List<CircleActivityTopic> topicList;
    private Context context;
    String userId;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Gson gson=new Gson();
            Bundle bundle=msg.getData();
            String str;
            str=bundle.getString("User");
            User user = gson.fromJson(str, User.class);
            user.setLikeNumberForHistory(user.getLikeNumberForHistory()+1);
            user.update();
        }
    };

    public void setTopicList(List<CircleActivityTopic> topicList) {
        this.topicList = topicList;
    }

    public List<CircleActivityTopic> getTopicList() {
        return topicList;
    }

    public CircleListViewAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        this.context=context;
        this.userId=((MyApplication)context.getApplicationContext()).applicationMap.get("userObjectId");
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        CircleViewHolder viewHolder=null;
        if (convertView == null) {
            viewHolder=new CircleViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.activity_circle_friend_list_listview_content, null);
            viewHolder.number=(TextView)convertView.findViewById(R.id.friend_list_number);
            viewHolder.header=(ImageView)convertView.findViewById(R.id.friend_list_header);
            viewHolder.name=(TextView)convertView.findViewById(R.id.friend_list_name);
            viewHolder.distance=(TextView)convertView.findViewById(R.id.friend_list_distance);
            viewHolder.like=(ImageView)convertView.findViewById(R.id.friend_list_like);
            viewHolder.like_times=(TextView)convertView.findViewById(R.id.friend_list_like_times);
            viewHolder.likeLL=(LinearLayout) convertView.findViewById(R.id.friend_list_likeLL);
            convertView.setTag(viewHolder);
        }else {
            viewHolder=(CircleViewHolder)convertView.getTag();
        }
        viewHolder.likeLL.setOnClickListener(new btnListener(position,viewHolder));
        viewHolder.number.setText(""+topicList.get(position).getNumber());
        Picasso.with(context).load(topicList.get(position).getUserHeadImage()).noFade().into(viewHolder.header);
        viewHolder.name.setText(topicList.get(position).getUserName());
        viewHolder.distance.setText(topicList.get(position).getUserDistance());
        if(topicList.get(position).isLiked()) {
            viewHolder.like.setBackgroundResource(R.mipmap.ic_zan);
        }
        else {
            viewHolder.like.setBackgroundResource(R.mipmap.ic_zan2);
        }
        viewHolder.like_times.setText(""+topicList.get(position).getLikeTimes());
        return convertView;
    }

    private void updateLiked(int position){
        Calendar calendar = Calendar.getInstance();
        String date;
        date = calendar.get(Calendar.YEAR) + "";
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (month < 10)
            date += "0" + month;
        else
            date += month;
        if (day < 10)
            date += "0" + day;
        else
            date += day;
        Log.e("database", "date:" + date);

        Friend friend=topicList.get(position).getFriend();
        if(friend!=null) {
            friend.setLikeDate(date);
            friend.update();
        } else {
            friend=new Friend();
            friend.setUserObjectId(userId);
            friend.setLikeDate(date);
            friend.setFriendObjectId(topicList.get(position).getMemberId());
            friend.setFriend(0);
            friend.save();
        }
        GetFromBmob.getUserById(topicList.get(position).getMemberId(),handler);
    }

    class btnListener implements View.OnClickListener
    {
        private int position;
        private CircleViewHolder currentHolder;

        public btnListener(int position, CircleViewHolder currentHolder)
        {
            this.position = position;
            this.currentHolder=currentHolder;
        }

        @Override
        public void onClick(View v){
            if(!topicList.get(position).isLiked()) {
                currentHolder.like.setBackgroundResource(R.mipmap.ic_zan);
                currentHolder.like_times.setText("" + (topicList.get(position).getLikeTimes() + 1));
                topicList.get(position).setLiked(true);
                updateLiked(position);
            } else {
                Toast.makeText(context,"can't click twice today...",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
