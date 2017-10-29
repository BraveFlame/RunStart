package com.runstart.mine.mine_activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.runstart.R;
import com.runstart.bean.ExerciseDiary;
import com.runstart.help.ActivityCollector;
import com.runstart.help.ToastShow;
import com.runstart.mine.mine_adapter.ExerciseDiaryListViewAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;


/**
 * Created by zhouj on 2017-09-21.
 */

public class MineExerciseDiaryActivity extends Activity implements View.OnClickListener {

    //用来判断单选框是否被选中
    public static boolean isChoose = false;
    //定义fragment_mine_exercisedairy.xml的布局组件
    private ListView lv_exercisedairy;
    private ImageView iv_zuojiantou;
    private Button btn_popupmenu;
    //判断标志符是否关闭菜单
    boolean flag = false;
    //定义list列表保存exercisediary的相关信息
    private List<ExerciseDiary> exerciseDiaryList = new ArrayList<>();
    //定义mine_exercisediary_popupmenu.xml的组件
    private RelativeLayout rl_mine_exdiarymenu_first;
    private RelativeLayout rl_mine_exdiarymenu_second;
    private RelativeLayout rl_mine_exdiarymenu_three;
    //定义ListView的适配器exDairyListViewAdapter
    private ExerciseDiaryListViewAdapter exDairyListViewAdapter;
    //定义HashSet来保存所选中的item位置
    private List<Integer> positionList;

    private SharedPreferences preferences;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_mine_exercisediary);
        ActivityCollector.addActivity(this);
        preferences= PreferenceManager.getDefaultSharedPreferences(this);
        positionList = new ArrayList<>();
        initView();
        initPopMenu();
        useAdapter();
        useListViewMethod();

    }

    @Override
    protected void onStart() {
        super.onStart();
        getExerciseDiaryData();
    }

    /**
     * 初始化布局组件
     */
    public void initView() {
        //初始化内容栏的ListView组件
        lv_exercisedairy = (ListView) findViewById(R.id.mine_exercisediary_content_lv_exercisedairy);
        //初始化标题栏的ImageView左箭头组件
        iv_zuojiantou = (ImageView) findViewById(R.id.mine_exercisediary_iv_zuojiantou);
        iv_zuojiantou.setOnClickListener(this);
        //初始化悬浮按钮的组件
        FloatingActionButton btn_floatingAction = (FloatingActionButton) findViewById(R.id.mine_exercisediary_btnFloatingAction);
        btn_floatingAction.setOnClickListener(this);
    }

    /**
     * 初始化popWindow菜单
     */
    public void initPopMenu() {

        //装载mine.layout.popup.xml对应的界面布局
        View view = this.getLayoutInflater().inflate(R.layout.mine_exercisediary_popupmenu, null);

        //创建PopupWindow对象
        final PopupWindow popupWindow = new PopupWindow(view, 550, 400);
        popupWindow.setOutsideTouchable(true);
        btn_popupmenu = (Button) findViewById(R.id.mine_exercisediary_popupmenu);

        btn_popupmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!flag) {
                    //以下拉的方式来显示
//                popupWindow.showAsDropDown(v,20,30);
                    //将PopupWindow显示在指定的位置
                    popupWindow.showAtLocation(findViewById(R.id.mine_exercisediary_popupmenu), Gravity.RELATIVE_LAYOUT_DIRECTION, 220, -535);
                    flag = true;
                } else {
                    popupWindow.dismiss();
                    flag = false;
                }
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        flag = false;
                    }
                }).start();
            }
        });
        //初始化mine_exercise_popupmenu.xml布局的组件
        rl_mine_exdiarymenu_first = (RelativeLayout) view.findViewById(R.id.mine_exercisediary_popupmenu_rl_first);
        rl_mine_exdiarymenu_first.setOnClickListener(this);
        rl_mine_exdiarymenu_second = (RelativeLayout) view.findViewById(R.id.mine_exercisediary_popupmenu_rl_second);
        rl_mine_exdiarymenu_second.setOnClickListener(this);
        rl_mine_exdiarymenu_three = (RelativeLayout) view.findViewById(R.id.mine_exercisediary_popupmenu_rl_three);
        rl_mine_exdiarymenu_three.setOnClickListener(this);
    }


    /**
     * 初始化数据
     */
    public void getExerciseDiaryData() {
        exerciseDiaryList.clear();
        exDairyListViewAdapter.notifyDataSetChanged();

        BmobQuery<ExerciseDiary> query = new BmobQuery<ExerciseDiary>();
        query.addWhereEqualTo("userObjectId",preferences.getString("userObjectId","0101"));
        //执行查询方法
        query.findObjects(new FindListener<ExerciseDiary>() {
            @Override
            public void done(List<ExerciseDiary> object, BmobException e) {
                if (e == null) {
                    if (object.size()==0)
                    {
                        ToastShow.showToast(MineExerciseDiaryActivity.this,"暂无日记");
                        return;
                    }

                    for (ExerciseDiary exerciseDiary : object) {
                        Log.i("bmob", "获取数据成功！" + object.size());
                        exerciseDiaryList.add(exerciseDiary);
                        exDairyListViewAdapter.notifyDataSetChanged();

                    }
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    /**
     * 使用ListViewAdapter
     */
    public void useAdapter() {
        exDairyListViewAdapter = new ExerciseDiaryListViewAdapter(MineExerciseDiaryActivity.this
        ,R.layout.mine_exercisediary_listitem,exerciseDiaryList);
        lv_exercisedairy.setAdapter(exDairyListViewAdapter);


    }

    /**
     * 使用listView的事件监听方法
     */
    private void useListViewMethod() {
        lv_exercisedairy.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //判断是否可以反选checkbox
            int currentNum = -1;
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //用于判断长按是否生效
                if (isChoose) {
                    if (currentNum == -1) { //选中
                        if (exerciseDiaryList.get(position).isCheck()) {
                            exerciseDiaryList.get(position).setCheck(false);
                        }else {
                            exerciseDiaryList.get(position).setCheck(true);
                        }
                        currentNum = position;
                        if (!positionList.contains(position)) {
                            positionList.add(position);
                        }
                    } else if (currentNum == position) {//同一个item选中变未选中
                        if (exerciseDiaryList.get(position).isCheck()) {
                            exerciseDiaryList.get(position).setCheck(false);
                        }else {
                            exerciseDiaryList.get(position).setCheck(true);
                        }
                        currentNum = -1;
                        if (!positionList.contains(position)) {
                            positionList.add(position);
                        } else {
                            for (int i = 0; i < positionList.size(); i++) {
                                if (positionList.get(i).equals(position)) {
                                    positionList.remove(i);
                                }
                            }
                        }

                    } else if (currentNum != position) {//不是同一个item选中
                        if (exerciseDiaryList.get(position).isCheck()) {
                            exerciseDiaryList.get(position).setCheck(false);
                        }else {
                            exerciseDiaryList.get(position).setCheck(true);
                        }
                        currentNum = position;
                        if (!positionList.contains(position)) {
                            positionList.add(position);
                        }
                    }

                    exDairyListViewAdapter.notifyDataSetChanged();
                }
                if (!isChoose) {
                    ExerciseDiary exerciseDiary = exerciseDiaryList.get(position);
                    //创建一个Buddle对象
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("exerciseDiary", exerciseDiary);
                    Intent exDiaryContentIntent = new Intent(MineExerciseDiaryActivity.this, ExerciseDiaryContentActivity.class);
                    exDiaryContentIntent.putExtras(bundle);
                    startActivity(exDiaryContentIntent);
                }
            }
        });



        lv_exercisedairy.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (!isChoose) {
                    btn_popupmenu.setVisibility(View.VISIBLE);
                    for (int i=0;i<exerciseDiaryList.size();i++){
                        exerciseDiaryList.get(i).setCheck(false);
                    }
                    exDairyListViewAdapter.notifyDataSetChanged();
                    isChoose = true;
                    return true;
                }
                return false;
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
            case R.id.mine_exercisediary_btnFloatingAction:     //悬浮按钮的操作(进入addExDiary进行添加操作)
                Intent addExDiaryIntent = new Intent(MineExerciseDiaryActivity.this, AddExDiaryActivity.class);
                startActivity(addExDiaryIntent);
                break;
            case R.id.mine_exercisediary_iv_zuojiantou:         //标题栏的左箭头回退按钮
                MineExerciseDiaryActivity.this.finish();
                break;
            case R.id.mine_exercisediary_popupmenu_rl_first:     //菜单的全选按钮
                chooseAllMenu();
                break;
            case R.id.mine_exercisediary_popupmenu_rl_second:   //菜单的反选按钮
                insertSelectionMenu();
                break;
            case R.id.mine_exercisediary_popupmenu_rl_three:    //菜单的删除按钮
                deleteDialogMenu();
            default:
                break;
        }
    }

    /**
     * 菜单栏的反选操作
     */
    private void insertSelectionMenu() {
        for (int i = 0; i < exerciseDiaryList.size(); i++) {
            exerciseDiaryList.get(i).setCheck(false);
        }
        positionList.clear();
        exDairyListViewAdapter.notifyDataSetChanged();
    }

    /**
     * 菜单栏的全选操作
     */
    private void chooseAllMenu() {
        positionList.clear();
        for (int i = 0; i < exerciseDiaryList.size(); i++) {
            exerciseDiaryList.get(i).setCheck(true);
            if (!positionList.contains(i)) {
                positionList.add(i);
            } else {
                for (int j = 0; j < positionList.size(); j++) {
                    if (positionList.get(j).equals(i)) {
                        positionList.remove(i);
                    }
                }
            }
        }
        exDairyListViewAdapter.notifyDataSetChanged();
    }

    /**
     * 菜单栏的删除操作
     */
    private void deleteDialogMenu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MineExerciseDiaryActivity.this);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = positionList.size() - 1; i >= 0; i--) {
                    if (isNetworkAvailable(MineExerciseDiaryActivity.this) && (is3rd(MineExerciseDiaryActivity.this) | isWifi(MineExerciseDiaryActivity.this))) {
                        if (exerciseDiaryList.get(positionList.get(i)).isCheck()) {
                            deleteExDiaryData(exerciseDiaryList.get(positionList.get(i)), i);
                        }
                    } else {
                        Toast.makeText(MineExerciseDiaryActivity.this, "Dear users, please open your internet connection, otherwise you can not delete successfully!", Toast.LENGTH_SHORT).show();
                    }

                }
                isChoose = false;
                positionList.clear();
                btn_popupmenu.setVisibility(View.INVISIBLE);
                exDairyListViewAdapter.notifyDataSetChanged();
            }

        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.setTitle("Cancel the ExerciseDiary dialog!");
        dialog.setMessage("Dear users, do you confirm deleting the selected content? Yes Ok, No Cancel!");
        dialog.show();
    }

    /**
     * 从数据库中删除所选中的数据
     *
     * @param exerciseDiary
     */
    private void deleteExDiaryData(ExerciseDiary exerciseDiary, final int i) {
        exerciseDiary.setObjectId(exerciseDiary.getObjectId());
        exerciseDiary.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.i("bmob", "成功");

                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());

                }
            }
        });
        exerciseDiaryList.remove(exerciseDiaryList.get(positionList.get(i)));
        exDairyListViewAdapter.notifyDataSetChanged();
    }

    //判断网络是否连接
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
        } else {
/*
                如果仅仅是用来判断网络连接 ,
                则可以使用 cm.getActiveNetworkInfo().isAvailable();
                  */
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //    三、判断WIFI是否打开
    public static boolean isWifiEnabled(Context context) {
        ConnectivityManager mgrConn = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager mgrTel = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return ((mgrConn.getActiveNetworkInfo() != null && mgrConn
                .getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel
                .getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
    }

    //    四、判断是否是3G网络
    public static boolean is3rd(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkINfo = cm.getActiveNetworkInfo();
        if (networkINfo != null
                && networkINfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        return false;
    }

    //    五、判断是wifi还是3g网络,用户的体现性在这里了，wifi就可以建议下载或者在线播放。
    public static boolean isWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkINfo = cm.getActiveNetworkInfo();
        if (networkINfo != null
                && networkINfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isChoose = false;
        ActivityCollector.removeActivity(this);
    }
}
