package com.runstart.friend;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.runstart.R;

import java.util.List;

/**
 * Created by user on 17-9-28.
 */

public class MsgAdapter extends ArrayAdapter<MsgChat> {
    private int resourceId;

    public MsgAdapter(Context context, int textViewResourceId, List<MsgChat>
            objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MsgChat msg = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.leftLayout = (RelativeLayout) view.findViewById
                    (R.id.chat_left_layout);
            viewHolder.rightLayout = (RelativeLayout) view.findViewById
                    (R.id.chat_right_layout);
            viewHolder.leftMsg = (TextView) view.findViewById(R.id.chat_left_msg);
            viewHolder.rightMsg = (TextView) view.findViewById(R.id.chat_right_msg);
            viewHolder.leftImg = (ImageView) view.findViewById(R.id.chat_left_img);
            viewHolder.rightImg = (ImageView) view.findViewById(R.id.chat_right_img);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        if (msg.getType() == 0) {

// 如果是收到的消息,则显示左边的消息布局,将右边的消息布局隐藏

            viewHolder.leftLayout.setVisibility(View.VISIBLE);
            viewHolder.rightLayout.setVisibility(View.GONE);

            if (msg.getContent().contains("/myimages/")) {
                viewHolder.leftMsg.setVisibility(View.GONE);
                viewHolder.leftImg.setVisibility(View.VISIBLE);
                PhotoUtilsCircle.showImage(viewHolder.leftImg, msg.getContent());

            } else {
                viewHolder.leftImg.setVisibility(View.GONE);
                viewHolder.leftMsg.setVisibility(View.VISIBLE);
                viewHolder.leftMsg.setText(msg.getContent());
            }
        } else if (msg.getType() == 1) {
// 如果是发出的消息,则显示右边的消息布局,将左边的消息布局隐藏
            viewHolder.rightLayout.setVisibility(View.VISIBLE);
            viewHolder.leftLayout.setVisibility(View.GONE);


            if (msg.getContent().contains("/myimages/")) {
                viewHolder.rightMsg.setVisibility(View.GONE);
                viewHolder.rightImg.setVisibility(View.VISIBLE);
                PhotoUtilsCircle.showImage(viewHolder.rightImg, msg.getContent());

            } else {
                viewHolder.rightImg.setVisibility(View.GONE);
                viewHolder.rightMsg.setVisibility(View.VISIBLE);
                viewHolder.rightMsg.setText(msg.getContent());
            }
        }
        return view;
    }

    class ViewHolder {
        RelativeLayout leftLayout, rightLayout;
        TextView leftMsg;
        TextView rightMsg;
        ImageView leftImg, rightImg;
    }
}
