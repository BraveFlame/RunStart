package com.runstart.circle;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.runstart.R;


/**
 * Created by zhouj on 2017-09-23.
 */

public class CircleCreateActivity extends Activity implements View.OnClickListener {
    /**
     * 定义fragment_circle_createactivity_content.xml组件
     */
    //定义RelativeLayout组件(or select the frequency)
    RelativeLayout rl_selectedFrequency;
    //定义TextView组件(or select the frequency)
    TextView tv_selectedFrequency;
    //定义RelativeLayout组件(the amount of completion)
    RelativeLayout rl_amount;
    //定义TextView组件.(the amount of completion)
    TextView tv_style;
    TextView tv_mileage;

    /**
     * 定义dialog_circle_createactivity_amount.xml布局中的组件
     */
    RadioGroup rg_amount;
    EditText ed_amount;
    /**
     * 定义fragment_circle_createactivity_title.xml布局中的组件
     */
    ImageView iv_backjiantou;

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
        /**
         *  初始化fragment_circle_createactivity_title.xml布局中的组件
         */
        iv_backjiantou = (ImageView) findViewById(R.id.circle_createactivity_iv_zuojiantou);
        iv_backjiantou.setOnClickListener(this);
        /**
         *  初始化fragment_circle_createactivity_content.xml布局中的组件
         */
        //Or select the frequency的组件
        rl_selectedFrequency = (RelativeLayout) findViewById(R.id.circle_createactivity_five_rl_first);
        rl_selectedFrequency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAlertDialog();

            }
        });
        tv_selectedFrequency = (TextView) findViewById(R.id.circle_createactivity_five_first_tv_selectedfrequency);
        //the amount of completion的组件
        rl_amount = (RelativeLayout) findViewById(R.id.circle_createactivity_four_rl_first);
        rl_amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMileageDialog();
            }
        });
        tv_style = (TextView) findViewById(R.id.circle_createactivity_four_first_tv_style);
        tv_mileage = (TextView) findViewById(R.id.circle_createactivity_four_first_tv_mileage);
        //悬浮按钮组件




    }
    /**
     * 设置运动方式和里程的对话框
     */
    private void getMileageDialog() {
        LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.dialog_circle_createactivity_amount, (ViewGroup) findViewById(R.id.dialog_circle_createactivity_ll_layout));
        //初始化dialog_circle_createactivity_amount.xml的布局组件
        ed_amount = (EditText) layout.findViewById(R.id.dialog_circle_createactivity_tv_mileage);
        rg_amount = (RadioGroup) layout.findViewById(R.id.dialog_circle_createactivity_radiogroup);
        //声明一个alerdialog对话框
        final AlertDialog.Builder builder = new AlertDialog.Builder(CircleCreateActivity.this, R.style.Dialog_FS)
                .setView(layout);
        setRadioGroup();
        //为对话框设置一个"确定"按钮
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                tv_mileage.setText(ed_amount.getText()+"m");
            }
        });
        //为对话框设置一个"取消"按钮
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.create().show();
    }

    /**
     * selected Frequency的选择对话框
     */
    private void getAlertDialog() {
        final Dialog dialog = new AlertDialog.Builder(CircleCreateActivity.this, R.style.Dialog_FS).create();
        dialog.show();

                /*添加对话框的自定义的布局*/
        dialog.setContentView(R.layout.dialog_createactivity_selectedfrequency_listview);
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
        ArrayAdapter<String> frequencyadapter = new ArrayAdapter<String>(this, R.layout.dialog_createactivity_selectedfrequency_listitem, freqencyArray);
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

    /**
     * 实现radiogroup的的事件监听
     */
    public void setRadioGroup() {
        rg_amount.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                switch (checkedId) {
                    case R.id.dialog_circle_createactivity_rb_all:
                        RadioButton all_btn = (RadioButton)group.findViewById(checkedId);
                        tv_style.setText(all_btn.getText());
                        break;
                    case R.id.dialog_circle_createactivity_rb_walk:
                        RadioButton walk_btn = (RadioButton)group.findViewById(checkedId);
                        tv_style.setText(walk_btn.getText());
                        break;
                    case R.id.dialog_circle_createactivity_rb_run:
                        RadioButton run_btn = (RadioButton)group.findViewById(checkedId);
                        tv_style.setText(run_btn.getText());
                        break;
                    case R.id.dialog_circle_createactivity_rb_ride:
                        RadioButton ride_btn = (RadioButton)group.findViewById(checkedId);
                        tv_style.setText(ride_btn.getText());
                        break;
                    default:
                        Toast.makeText(CircleCreateActivity.this,"尊敬的用户，您还没选择您的运动方式，请重新选择！",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

    }

    /**
     * 实现OnClickListener事件监听的总方法
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.circle_createactivity_iv_zuojiantou:
                finish();
                break;
            default:
                break;
        }

    }
}
