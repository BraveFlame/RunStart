package com.runstart.friend.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


import com.runstart.R;

import java.util.List;
import java.util.Map;

/**
 * Created by User on 17-9-11.
 */
public class AdapterForAddFriends extends SimpleAdapter {
    private Callback mCallback;
    private LayoutInflater mInflater;
    private List<Map<String, Object>> mList;
    private String[] mFrom;
    private int[] mTo;
    public AdapterForAddFriends(Context context, List<Map<String, Object>> data, int resource, String[] from, int[] to, Callback callback) {
        super(context, data, resource, from, to);
        mList = data;
        mInflater = LayoutInflater.from(context);
        mCallback = callback;
        mFrom = from;
        mTo = to;
    }


    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    private void bindView(int position, View view) {
        final Map dataSet = mList.get(position);
        if (dataSet == null) {
            return;
        }

        final ViewBinder binder = getViewBinder();
        final String[] from = mFrom;
        final int[] to = mTo;
        final int count = to.length;

        for (int i = 0; i < count; i++) {
            final View v = view.findViewById(to[i]);
            if (v != null) {
                final Object data = dataSet.get(from[i]);
                String text = data == null ? "" : data.toString();
                if (text == null) {
                    text = "";
                }

                boolean bound = false;
                if (binder != null) {
                    bound = binder.setViewValue(v, data, text);
                }

                if (!bound) {
                    if (v instanceof Checkable) {
                        if (data instanceof Boolean) {
                            ((Checkable) v).setChecked((Boolean) data);
                        } else if (v instanceof TextView) {
                            setViewText((TextView) v, text);
                        } else {
                            throw new IllegalStateException(v.getClass().getName() +
                                    " should be bound to a Boolean, not a " +
                                    (data == null ? "<unknown type>" : data.getClass()));
                        }
                    } else if (v instanceof TextView) {
                        setViewText((TextView) v, text);
                    } else if (v instanceof ImageView) {
                        if (data instanceof Bitmap){
                            ((ImageView) v).setImageBitmap((Bitmap)data);
                        }else if (data instanceof Integer) {
                            setViewImage((ImageView) v, (Integer) data);
                        }else if (data == null){
                            ((ImageView) v).setImageResource(R.mipmap.ic_shangchuangtupiang);
                        }else {
                            setViewImage((ImageView) v, text);
                        }
                    } else {
                        throw new IllegalStateException(v.getClass().getName() + " is not a " +
                                " view that can be bounds by this SimpleAdapter");
                    }
                }
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;
        if (convertView == null){
            view = mInflater.inflate(R.layout.item_adding_friend, parent, false);
        }else {
            view = convertView;
        }

        bindView(position, view);

        ViewHolder holder;
        holder = new ViewHolder();
        holder.addFriendToMyGroup = (ImageView) view.findViewById(R.id.addFriendToMyGroup);
        holder.addFriendToMyGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click(v);
            }
        });
        return view;
    }

    private  void click(View v){
        mCallback.click(v);
    }

    public interface Callback{
        void click(View view);
    }

    public class ViewHolder{
        public ImageView addFriendToMyGroup;
    }
}
