package com.runstart.mine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.runstart.R;
import com.runstart.bean.AboutSport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouj on 2017-10-09.
 */

public class AboutSportAdapter extends BaseAdapter {

    class AboutSportViewHolder{
        public ImageView newsImage;
        public TextView newsTitle;
        public TextView newsContent;
    }
    private LayoutInflater mLayoutInflater;
    private List<AboutSport> aboutSportList = new ArrayList<AboutSport>();

    public List<AboutSport> getAboutSportList() {
        return aboutSportList;
    }

    public void setAboutSportList(List<AboutSport> aboutSportList) {
        this.aboutSportList = aboutSportList;
    }

    public AboutSportAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return aboutSportList.size();
    }

    @Override
    public Object getItem(int position) {
        return aboutSportList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AboutSportViewHolder aboutSportViewHolder;
        if(convertView==null){
            convertView=mLayoutInflater.inflate(R.layout.mine_aboutsport_listitem,null);
            aboutSportViewHolder=new AboutSportViewHolder();
            aboutSportViewHolder.newsImage = (ImageView)convertView.findViewById(R.id.mine_aboutsport_listitem_iv_newsImage);
            aboutSportViewHolder.newsTitle = (TextView) convertView.findViewById(R.id.mine_aboutsport_listitem_tv_newstitle);
            aboutSportViewHolder.newsContent = (TextView) convertView.findViewById(R.id.mine_aboutsport_listitem_tv_newscontent);
            convertView.setTag(aboutSportViewHolder);
        }else {
            aboutSportViewHolder=(AboutSportViewHolder)convertView.getTag();
        }
        aboutSportViewHolder.newsImage.setImageResource(Integer.parseInt(aboutSportList.get(position).getNewsImage()));
        aboutSportViewHolder.newsImage.setBackgroundResource(Integer.parseInt(aboutSportList.get(position).getNewsBgImage()));
        aboutSportViewHolder.newsTitle.setText(aboutSportList.get(position).getNewsTitle());
        aboutSportViewHolder.newsContent.setText(aboutSportList.get(position).getNewsContent());
        return convertView;
    }
}
