package com.runstart.mine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.runstart.R;
import com.runstart.bean.MessageRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouj on 2017-10-07.
 */

final class ErViewHolder{
    public ImageView userImage;
    public TextView username;
    public TextView usermessage;
    public TextView usertime;
}
public class MeRecordAdapter extends BaseAdapter {

    private LayoutInflater mLayoutInflater;
    private List<MessageRecord> meRecordList = new ArrayList<MessageRecord>();

    public List<MessageRecord> getMeRecordList() {
        return meRecordList;
    }

    public void setMeRecordList(List<MessageRecord> meRecordList) {
        this.meRecordList = meRecordList;
    }

    public MeRecordAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return meRecordList.size();
    }

    @Override
    public Object getItem(int position) {
        return meRecordList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ErViewHolder erViewHolder=null;
        if (convertView==null){
            convertView = mLayoutInflater.inflate(R.layout.mine_messagerecord_listitem, null);
            erViewHolder=new ErViewHolder();
            erViewHolder.userImage=(ImageView)convertView.findViewById(R.id.mine_messagerecord_listitem_iv_userimage);
            erViewHolder.username = (TextView) convertView.findViewById(R.id.mine_messagerecord_listitem_tv_username);
            erViewHolder.usermessage = (TextView) convertView.findViewById(R.id.mine_messagerecord_listitem_tv_usermessage);
            erViewHolder.usertime = (TextView) convertView.findViewById(R.id.mine_messagerecord_listitem_tv_usertime);
            convertView.setTag(erViewHolder);
        }else {
            erViewHolder=(ErViewHolder)convertView.getTag();
        }
        erViewHolder.userImage.setImageResource(Integer.parseInt(meRecordList.get(position).getUserImage()));
        erViewHolder.username.setText(meRecordList.get(position).getUserName());
        erViewHolder.usermessage.setText(meRecordList.get(position).getUserMessage());
        erViewHolder.usertime.setText(meRecordList.get(position).getUserTime());
        return convertView;
    }
}

