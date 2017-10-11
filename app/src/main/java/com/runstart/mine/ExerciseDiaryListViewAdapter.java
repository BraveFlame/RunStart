package com.runstart.mine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;


import com.runstart.R;
import com.runstart.bean.ExerciseDiary;

import java.util.List;

/**
 * Created by zhouj on 2017-09-28.
 */

class ViewHolder {
    public TextView exDiaryTitle;
    public TextView exDiaryContent;
    public TextView exDiaryDate;
    public static CheckBox checkBox;
}

public class ExerciseDiaryListViewAdapter extends BaseAdapter {
    LayoutInflater mLayoutInflater;
    List<ExerciseDiary> exerciseDiaryList;

    public void setExerciseDiaryList(List<ExerciseDiary> exerciseDiaryList) {
        this.exerciseDiaryList = exerciseDiaryList;
    }

    public List<ExerciseDiary> getExerciseDiaryList() {
        return exerciseDiaryList;
    }
    //创建构造函数

    public ExerciseDiaryListViewAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return exerciseDiaryList.size();
    }

    @Override
    public Object getItem(int position) {
        return exerciseDiaryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (viewHolder == null) {
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.mine_exercisediary_listitem, null);
            viewHolder.exDiaryTitle = (TextView) convertView.findViewById(R.id.mine_exercisediary_listitem_exdiarytitle);
            viewHolder.exDiaryContent = (TextView) convertView.findViewById(R.id.mine_exercisediary_listitem_exdiarycontent);
            viewHolder.exDiaryDate = (TextView) convertView.findViewById(R.id.mine_exercisediary_listitem_exdiarydate);
            ViewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.mine_exercisediary_listitem_checkbox);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        /**
         * 判断是否显示多选框，长按才显示
         */
        if (!MineExerciseDiaryActivity.isChoose) {
            viewHolder.checkBox.setVisibility(View.GONE);
        } else {
            viewHolder.checkBox.setVisibility(View.VISIBLE);
        }
        viewHolder.exDiaryTitle.setText(exerciseDiaryList.get(position).getExDairyTitle());
        viewHolder.exDiaryContent.setText(exerciseDiaryList.get(position).getExDairyContent());
        viewHolder.exDiaryDate.setText(exerciseDiaryList.get(position).getExDairyDate());
        viewHolder.checkBox.setChecked(exerciseDiaryList.get(position).isCheck());

        return convertView;
    }
}
