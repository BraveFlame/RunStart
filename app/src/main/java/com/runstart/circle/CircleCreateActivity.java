package com.runstart.circle;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.runstart.R;


/**
 * Created by zhouj on 2017-09-23.
 */

public class CircleCreateActivity extends Activity {

    RelativeLayout rl_selectedFrequency;
    //定义TextView组件
    TextView tv_selectedFrequency;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_circle_createactivity);
        initView();
    }

    /**
     * 初始化组件
     */
    public void initView() {
        rl_selectedFrequency = (RelativeLayout) findViewById(R.id.circle_createactivity_five_rl_first);
        rl_selectedFrequency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAlertDialog();

            }
        });
        tv_selectedFrequency = (TextView) findViewById(R.id.circle_createactivity_five_first_tv_selectedfrequency);
    }

    /**
     * selected Frequency的选择对话框
     */
    private void getAlertDialog() {
        final Dialog dialog = new AlertDialog.Builder(CircleCreateActivity.this,R.style.Dialog_FS).create();
        dialog.show();

        dialog.setTitle("Or select the frequency");
                /*添加对话框的自定义的布局*/
        dialog.setContentView(R.layout.createactivity_selectedfrequency_listview);
                /*获取对话框的窗口*/
        Window dialogWindow = dialog.getWindow();
                /*设置显示从对话框窗口的宽高*/
        dialogWindow.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                /*设置对话框的显示位置*/
        dialogWindow.setGravity(Gravity.BOTTOM);
                /*通过dialogWindow找寻布局中的控件*/
        ListView frequencyListView = (ListView) dialogWindow.findViewById(R.id.createactivity_selectedfrequency_listview);
        //定义一个数组
        final String[] freqencyArray = new String[]{"Once a day", "Twice a day", "once a week",
                "Twice a week", "three times a week", "Once a month"};
        //将数组包装为ArrayAdapter
        ArrayAdapter<String> frequencyadapter = new ArrayAdapter<String>(this, R.layout.createactivity_selectedfrequency_listitem, freqencyArray);
        frequencyListView.setAdapter(frequencyadapter);
                /*设置监听事件*/
        frequencyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tv_selectedFrequency.setText(freqencyArray[position]);
                dialog.dismiss();
            }
        });
    }
}
