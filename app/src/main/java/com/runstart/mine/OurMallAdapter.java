package com.runstart.mine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.runstart.R;
import com.runstart.bean.OurMall;

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
public class OurMallAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;
    private List<OurMall> ourMallList = new ArrayList<OurMall>();

    public List<OurMall> getOurMallList() {
        return ourMallList;
    }

    public void setOurMallList(List<OurMall> ourMallList) {
        this.ourMallList = ourMallList;
    }

    public OurMallAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return ourMallList.size();
    }

    @Override
    public Object getItem(int position) {
        return ourMallList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OurMallViewHolder ourMallViewHolder;
        if(convertView==null){
            ourMallViewHolder=new OurMallViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.mine_ourmall_listitem, null);
            ourMallViewHolder.commodityImage = (ImageView) convertView.findViewById(R.id.mine_ourmall_listitem_iv_commodityImage);
            ourMallViewHolder.commodityname = (TextView) convertView.findViewById(R.id.mine_ourmall_listitem_commodityname);
            ourMallViewHolder.commodityprice = (TextView) convertView.findViewById(R.id.mine_ourmall_listitem_tv_commodityprice);
            convertView.setTag(ourMallViewHolder);
        }else {
            ourMallViewHolder=(OurMallViewHolder)convertView.getTag();
        }
        ourMallViewHolder.commodityImage.setImageResource(Integer.parseInt(ourMallList.get(position).commodityImage));
        ourMallViewHolder.commodityname.setText(ourMallList.get(position).getCommodityName());
        ourMallViewHolder.commodityprice.setText(ourMallList.get(position).getCommodityPrice());
        return convertView;
    }
}
