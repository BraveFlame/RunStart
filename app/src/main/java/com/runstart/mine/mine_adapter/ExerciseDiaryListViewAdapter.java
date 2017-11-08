package com.runstart.mine.mine_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;


import com.runstart.R;
import com.runstart.bean.ExerciseDiary;
import com.runstart.mine.mine_activity.MineExerciseDiaryActivity;

import java.util.List;

/**
 * Created by zhouj on 2017-09-28.
 */


public class ExerciseDiaryListViewAdapter extends ArrayAdapter<ExerciseDiary> {
    private int resourceId;

    //创建构造函数

    public ExerciseDiaryListViewAdapter(Context context, int textViewResourceId, List<ExerciseDiary>
            objects) {
        super(context, textViewResourceId, objects);
        this.resourceId = textViewResourceId;
    }


    /**
     * changed by zhonghao.song
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        ExerciseDiary exerciseDiary = getItem(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder.exDiaryTitle = (TextView) convertView.findViewById(R.id.mine_exercisediary_listitem_exdiarytitle);
            viewHolder.exDiaryContent = (TextView) convertView.findViewById(R.id.mine_exercisediary_listitem_exdiarycontent);
            viewHolder.exDiaryDate = (TextView) convertView.findViewById(R.id.mine_exercisediary_listitem_exdiarydate);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.mine_exercisediary_listitem_checkbox);
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
        viewHolder.exDiaryTitle.setText(exerciseDiary.getExDairyTitle());
        viewHolder.exDiaryContent.setText(exerciseDiary.getExDairyContent());
        viewHolder.exDiaryDate.setText(exerciseDiary.getExDairyDate());
        viewHolder.checkBox.setChecked(exerciseDiary.isCheck());

        return convertView;
    }

    class ViewHolder {
        public TextView exDiaryTitle;
        public TextView exDiaryContent;
        public TextView exDiaryDate;
        public CheckBox checkBox;
    }
}
