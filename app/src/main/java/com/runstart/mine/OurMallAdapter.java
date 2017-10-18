package com.runstart.mine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.runstart.R;
import com.runstart.bean.OurMall;
import com.runstart.friend.MsgChat;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by zhouj on 2017-10-08.
 */
class OurMallViewHolder{
    public ImageView commodityImage;
    public TextView commodityname;
    public TextView commodityprice;

}
public class OurMallAdapter extends ArrayAdapter<OurMall> {

    private int resourceId;

    public OurMallAdapter(Context context, int textViewResourceId, ArrayList<OurMall>
            objects) {
        super(context, textViewResourceId, objects);
        this.resourceId = textViewResourceId;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OurMall ourMall= getItem(position);
        OurMallViewHolder ourMallViewHolder;
        View  view;
        if(convertView==null){
            ourMallViewHolder=new OurMallViewHolder();
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            ourMallViewHolder.commodityImage = (ImageView) view.findViewById(R.id.mine_ourmall_listitem_iv_commodityImage);
            ourMallViewHolder.commodityname = (TextView) view.findViewById(R.id.mine_ourmall_listitem_commodityname);
            ourMallViewHolder.commodityprice = (TextView) view.findViewById(R.id.mine_ourmall_listitem_tv_commodityprice);
            view.setTag(ourMallViewHolder);
        }else {
            ourMallViewHolder=(OurMallViewHolder)convertView.getTag();
            view=convertView;
        }
        ourMallViewHolder.commodityImage.setImageResource(Integer.parseInt(ourMall.commodityImage));
        ourMallViewHolder.commodityname.setText(ourMall.getCommodityName());
        ourMallViewHolder.commodityprice.setText(ourMall.getCommodityPrice());
        return view;
    }
}
