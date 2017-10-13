package com.runstart.friend;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.runstart.R;
import com.runstart.friend.friendfragment.ImageWidgetInfoObj;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by user on 17-9-28.
 */

public class MsgAdapter extends ArrayAdapter<MsgChat> implements View.OnClickListener{
    private int resourceId;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Context context;
    private ImageWidgetInfoObj imageWidgetInfoObj;
    private String imgUrl;
    ImageInfoObj imageInfoObj;
    private boolean isScreen = false;
    private ViewGroup.LayoutParams imgLayParaLeft,imgLayParaRight;
    public MsgAdapter(Context context, int textViewResourceId, List<MsgChat>
            objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        this.context=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MsgChat msg = getItem(position);
        View view;
        final ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.leftLayout = (RelativeLayout) view.findViewById
                    (R.id.chat_left_layout);
            viewHolder.rightLayout = (RelativeLayout) view.findViewById
                    (R.id.chat_right_layout);
            viewHolder.timeShow = (TextView) view.findViewById(R.id.chat_time_show);
            viewHolder.leftMsg = (TextView) view.findViewById(R.id.chat_left_msg);
            viewHolder.rightMsg = (TextView) view.findViewById(R.id.chat_right_msg);
            viewHolder.leftImg = (ImageView) view.findViewById(R.id.chat_left_img);
            viewHolder.rightImg = (ImageView) view.findViewById(R.id.chat_right_img);
            view.setTag(viewHolder);
            viewHolder.leftImg.setOnClickListener(this);
            viewHolder.rightImg.setOnClickListener(this);
            imgLayParaLeft=viewHolder.leftImg.getLayoutParams();
            imgLayParaRight=viewHolder.rightImg.getLayoutParams();
             init(viewHolder.leftImg);
            init(viewHolder.rightImg);

        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        if (position == 0) {
            viewHolder.timeShow.setVisibility(View.VISIBLE);
            viewHolder.timeShow.setText(msg.getTime());
        }
        if (position > 0) {
            try {
                Date date1 = sdf.parse(getItem(position - 1).getTime());
                Date date2 = sdf.parse(msg.getTime());
                long distance = date2.getTime() - date1.getTime();
                if (distance > 120000) {
                    viewHolder.timeShow.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.timeShow.setVisibility(View.GONE);
                }
                viewHolder.timeShow.setText(msg.getTime());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        if (msg.getType() == 0) {
// 如果是收到的消息,则显示左边的消息布局,将右边的消息布局隐藏

            viewHolder.leftLayout.setVisibility(View.VISIBLE);
            viewHolder.rightLayout.setVisibility(View.GONE);

            if (msg.getContent().contains("/myimages/")) {
                viewHolder.leftMsg.setVisibility(View.GONE);
                viewHolder.leftImg.setVisibility(View.VISIBLE);
                imgUrl=msg.getContent();
                if("0".equals(PhotoUtilsCircle.showImage(viewHolder.leftImg, imgUrl))){
                    viewHolder.leftLayout.setVisibility(View.GONE);
                }

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
                imgUrl=msg.getContent();
                if("0".equals(PhotoUtilsCircle.showImage(viewHolder.rightImg, imgUrl))){
                    viewHolder.rightLayout.setVisibility(View.GONE);
                }

            } else {
                viewHolder.rightImg.setVisibility(View.GONE);
                viewHolder.rightMsg.setVisibility(View.VISIBLE);
                viewHolder.rightMsg.setText(msg.getContent());
            }
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.chat_left_img:
                Intent intent = new Intent(context,ScaleChatImgActivity.class);
                intent.putExtra("imageInfoObj", imageInfoObj);
                intent.putExtra("imageWidgetInfoObj", imageWidgetInfoObj);
                context.startActivity(intent);
                break;
            case R.id.chat_right_img:
                Intent intent0r= new Intent(context,ScaleChatImgActivity.class);
                intent0r.putExtra("imageInfoObj", imageInfoObj);
                intent0r.putExtra("imageWidgetInfoObj", imageWidgetInfoObj);
                context.startActivity(intent0r);
                break;
            default:
                break;
        }
    }

    private void init(ImageView imageView) {
     //   Glide.with(context).load(Bitmap.Config.IMAGE_URL).placeholder(R.mipmap.maimai).into(imageView);

        imageInfoObj = new ImageInfoObj();
       // imageInfoObj.imageUrl = Bitmap.Config.IMAGE_URL;
        imageInfoObj.imageUrl =imgUrl ;
        imageInfoObj.imageWidth = 1280;
        imageInfoObj.imageHeight = 720;

        imageWidgetInfoObj = new ImageWidgetInfoObj();
        imageWidgetInfoObj.x = imageView.getLeft();
        imageWidgetInfoObj.y = imageView.getTop();
        imageWidgetInfoObj.width = imageView.getLayoutParams().width;
        imageWidgetInfoObj.height = imageView.getLayoutParams().height;

    }

    public void setLeftAllScreen(ViewHolder viewHolder) {
        if (!isScreen) {
            imgLayParaLeft.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            imgLayParaLeft.width= RelativeLayout.LayoutParams.MATCH_PARENT;


        } else {
            imgLayParaLeft.height =480;
            imgLayParaLeft.width=360;
        }
        viewHolder.leftImg.setLayoutParams(imgLayParaLeft);

        isScreen = !isScreen;
    }
    public void setRightAllScreen(ViewHolder viewHolder) {
        if (!isScreen) {
            imgLayParaRight.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            imgLayParaRight.width= RelativeLayout.LayoutParams.MATCH_PARENT;


        } else {
            imgLayParaRight.height =480;
            imgLayParaRight.width=360;
        }
        viewHolder.rightImg.setLayoutParams(imgLayParaRight);

        isScreen = !isScreen;
    }
    class ViewHolder {
        RelativeLayout leftLayout, rightLayout;
        TextView leftMsg;
        TextView rightMsg, timeShow;
        ImageView leftImg, rightImg;
    }

}
