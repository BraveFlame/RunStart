package com.runstart.history;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.runstart.R;
import com.runstart.help.CountDown;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 17-8-24.
 */
public class NowDialog {

    EditText text1,text2,text4,text5,text6,text7,text8;
    MyApplication myApplication;
    Button button2;
    HistoryChartActivity activity;
    String[] items=new String[]{"pacing","running","riding"};

    public NowDialog(HistoryChartActivity activity){
        this.activity=activity;
        myApplication=(MyApplication)activity.getApplication();
    }

    public void my_dialog(){
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.history1_inner);
        text1 = (EditText) dialog.findViewById(R.id.text1);
        text2 = (EditText) dialog.findViewById(R.id.text2);
        text4 = (EditText) dialog.findViewById(R.id.text4);
        text5 = (EditText) dialog.findViewById(R.id.text5);
        text6 = (EditText) dialog.findViewById(R.id.text6);
        text7 = (EditText) dialog.findViewById(R.id.text7);
        text8 = (EditText) dialog.findViewById(R.id.text8);
        button2 = (Button) dialog.findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                int month=Integer.parseInt(text2.getText().toString());
                int day=Integer.parseInt(text4.getText().toString());
                Calendar c=Calendar.getInstance();
                month--;
                c.set(Calendar.MONTH,month);
                c.set(Calendar.DAY_OF_MONTH,day);
                int week=c.get(Calendar.WEEK_OF_YEAR);
                Log.e("database","week in dialog:"+week);
                myApplication.nowDB.insert(new String[]{},new double[]{Double.parseDouble(text1.getText().toString()),
                        (double)month,(double)week,(double)day,
                        Double.parseDouble(text5.getText().toString()),Double.parseDouble(text6.getText().toString()),
                        Double.parseDouble(text7.getText().toString()),Double.parseDouble(text8.getText().toString())});
                Toast.makeText(activity,"新增了" + text1.getText(), Toast.LENGTH_SHORT).show();
                if (dialog != null && dialog.isShowing()){
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    public void single_dialog(){
        List<HashMap<String,Integer>>list=new ArrayList<>();
        HashMap<String,Integer>map=new HashMap<>();
        map.put("runnig",R.mipmap.bitmap_run);
        map.put("riding",R.mipmap.bitmap_ride);
        map.put("pacing",R.mipmap.bitmap_walk);
        list.add(map);
        // 创建对话框构建器
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        // 设置参数
        builder.setIcon(R.mipmap.sport).setTitle("Please choose the sport")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(activity, CountDown.class);
                        intent.putExtra("activity",items[which]);
                       activity.startActivity(intent);
                    }
                });
        builder.create().show();
    }
}
