package com.runstart.circle.circle_activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.runstart.bean.ActivityData;
import com.runstart.bean.Friend;
import com.runstart.bean.User;
import com.runstart.R;
import com.runstart.circle.AdapterForAddFriends;
import com.runstart.circle.CirclePhotoUtils;
import com.runstart.circle.CommonUtils;
import com.runstart.circle.GetFromBmob;
import com.runstart.friend.adapter.ListViewForScrollView;
import com.runstart.MyApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.SQLQueryListener;

public class CircleCreateActivity extends AppCompatActivity implements View.OnClickListener, AdapterForAddFriends.Callback {

    final String[] freqencyArray = new String[]{"Once a day", "Twice a day", "once a week",
            "Twice a week", "three times a week", "Once a month"};
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

    RelativeLayout themePhoto;
    ImageView foreViewThemePhoto,circleForeViewThemePhoto;
    EditText ETactivityName,ETactivityIntroduction;
    Button create_activity_button;

    /**
     * 定义dialog_circle_createactivity_amount.xml布局中的组件
     */
    RadioGroup rg_amount;
    EditText ed_amount;
    /**
     * 定义fragment_circle_createactivity_title.xml布局中的组件
     */
    ImageView iv_backjiantou;

    String url;
    ActivityData activityDataToUpload;
    ProgressDialog progressDialog;

    private String MyId;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    url = msg.getData().getString("url");
                    activityDataToUpload=getActivityDataToAdd(url);
                    GetFromBmob.addAD(activityDataToUpload, handler);
                    break;
                case 2:
                    String objectId=msg.getData().getString("objectId");
                    for (Map.Entry<String, String> key : selectedUserObjectIdMap.entrySet()) {
                        GetFromBmob.addAAM(objectId,key.getKey(),handler);
                    }
                    GetFromBmob.addAAM(activityDataToUpload.getObjectId(),MyId,handler);
                    break;
                case 7:
                    CirclePushCardActivity.jump(activityDataToUpload.getObjectId(),CircleCreateActivity.this);
                    ((MyApplication)getApplicationContext()).isFragmentWalkShouldRefresh=true;
                    handler.sendEmptyMessage(11);
                    finish();
                    break;
                case 11:
                    progressDialog.cancel();
                    break;
                default:break;
            }
        }
    };

    public static void jump(Activity activity){
        Intent intent=new Intent(activity,CircleCreateActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.MyId=((MyApplication)getApplicationContext()).applicationMap.get("userObjectId");
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        init_view();

        queryFriend();
    }

    public void init_view() {
        setContentView(R.layout.fragment_circle_createactivity);
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

        themePhoto=(RelativeLayout)findViewById(R.id.themePhoto);
        themePhoto.setOnClickListener(this);
        foreViewThemePhoto=(ImageView)findViewById(R.id.foreViewThemePhoto);
        circleForeViewThemePhoto=(ImageView)findViewById(R.id.circleForeViewThemePhoto);
        create_activity_button=(Button)findViewById(R.id.create_activity_button);
        create_activity_button.setOnClickListener(this);

        ETactivityName=(EditText)findViewById(R.id.ETactivityName);
        ETactivityIntroduction=(EditText)findViewById(R.id.ETactivityIntroduction);

        friendsListView = (ListViewForScrollView)findViewById(R.id.friendsListView);
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

    private int typeStringToInt(String str){
        int i=4;
        if(str.equals("All"))i=3;
        else if(str.equals("Walk"))i=0;
        else if(str.equals("Run"))i=1;
        else if(str.equals("Ride"))i=2;
        return i;
    }
    private int frequencyStringToInt(String str){
        int i=6;
        for(int j=0;j<6;j++)
            if(str.equals(freqencyArray[j]))i=j;
        return i;
    }
    private boolean isCanAdd(){
        boolean flag=false;
        String type=tv_style.getText().toString();
        String amount=tv_mileage.getText().toString();
        String frequency=tv_selectedFrequency.getText().toString();
        String activityName=ETactivityName.getText().toString();
        String activityIntroduction=ETactivityIntroduction.getText().toString();
        if((!type.equals(""))&(!amount.equals("m"))&(!frequency.equals(""))&(!bxfPath.equals(""))&
                (!activityIntroduction.equals(""))&(!activityName.equals("")))
            flag=true;
        return flag;
    }
    private ActivityData getActivityDataToAdd(String backgroundUrl){
        String type=tv_style.getText().toString();
        String amount=tv_mileage.getText().toString();
        amount=amount.substring(0,amount.length()-1);
        String frequency=tv_selectedFrequency.getText().toString();
        String activityName=ETactivityName.getText().toString();
        String activityIntroduction=ETactivityIntroduction.getText().toString();
        ActivityData activityData=new ActivityData();
        activityData.setActivityName(activityName);
        activityData.setActivityIntroduction(activityIntroduction);
        activityData.setExerciseType(typeStringToInt(type));
        activityData.setCreatorId(MyId);
        activityData.setExerciseAmount(Integer.parseInt(amount));
        activityData.setFrequency(frequencyStringToInt(frequency));
        activityData.setBackgroundURL(backgroundUrl);
        return activityData;
    }

    private String bxfPath="";
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String str= CirclePhotoUtils.myPictureOnResultOperate(requestCode,resultCode,data,this);
        if (str.length()>3) {
            if (str.substring(0, 3).equals("bxf"))
                bxfPath = str.substring(3);
        } else if(str.equals("3")) CirclePhotoUtils.showImage(foreViewThemePhoto,circleForeViewThemePhoto,bxfPath);
        Log.e("database","str:"+str);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.themePhoto:
                CirclePhotoUtils.options(this);
                break;
            case R.id.circle_createactivity_iv_zuojiantou:
                finish();
                break;
            case R.id.create_activity_button:
                if(isCanAdd()) {
                    GetFromBmob.upload_picture(bxfPath, handler);
                    progressDialog=new ProgressDialog(this);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Uploading the data...");
                    progressDialog.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            handler.sendEmptyMessage(11);
                        }
                    }).start();
                }
                else
                    Toast.makeText(this,"Please complete the data",Toast.LENGTH_LONG).show();
                break;
            default:break;
        }
    }


    //添加活动好友
    private ListViewForScrollView friendsListView;
    private List<User> userList = new ArrayList<>();
    private List<Friend> friendList;
    private Map<String, Bitmap> bitmapMap = new ArrayMap<>();
    private List<User> orderedUserList = new ArrayList<>();
    private Map<String, String> selectedUserObjectIdMap = new HashMap();
    private void queryFriend(){
        new BmobQuery<Friend>().setSQL("select * from Friend where userObjectId=? and isFriend=1")
                .setPreparedParams(new String[]{MyApplication.applicationMap.get("userObjectId")})
                .doSQLQuery(new SQLQueryListener<Friend>() {
                    @Override
                    public void done(BmobQueryResult<Friend> bmobQueryResult, BmobException e) {
                        if (e == null){
                            friendList = bmobQueryResult.getResults();
                            if (friendList.size() != 0){
                                queryUser();
                            }
                        }else {
                            Toast.makeText(CircleCreateActivity.this, "load friends failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void queryUser(){
        String sql = "select * from User where objectId=?";
        for (int i = 0; i < friendList.size(); i++){
            new BmobQuery<User>().setSQL(sql).setPreparedParams(new String[]{friendList.get(i).getFriendObjectId()})
                    .doSQLQuery(new SQLQueryListener<User>() {
                        @Override
                        public void done(BmobQueryResult<User> bmobQueryResult, BmobException e) {
                            if (e == null){
                                synchronized (CircleCreateActivity.class) {
                                    User user = bmobQueryResult.getResults().get(0);
                                    userList.add(user);
                                    queryBitmap(user);
                                }
                            }else {
                                Toast.makeText(CircleCreateActivity.this, "load friends failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    private void queryBitmap(User user){
        final int objectIdLength = user.getObjectId().length();
        String saveName = CommonUtils.getStringToday() + user.getObjectId() + ".png";
        String headerImageUri = user.getHeaderImageUri();
        File saveFile = new File(Environment.getExternalStorageDirectory() + File.separator + "lovesportimage", saveName);
        if (headerImageUri == null || headerImageUri.length() == 0){
            synchronized (Object.class){
                bitmapMap.put(saveFile.toString().substring(saveFile.toString().length() - objectIdLength - 4, saveFile.toString().length() - 4), null);
            }
            return;
        }
        new BmobFile(saveName, "", headerImageUri).download(saveFile, new DownloadFileListener() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null){
                    synchronized (Object.class){
                        bitmapMap.put(s.substring(s.length() - objectIdLength - 4, s.length() - 4), BitmapFactory.decodeFile(s));
                        if (bitmapMap.size() == friendList.size()){
                            showResult();
                        }
                    }
                }else {
                    Toast.makeText(CircleCreateActivity.this, "load friends' images failed", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onProgress(Integer integer, long l) {}
        });
    }
    private void showResult(){
        String[] mItemCols = new String[]{"rankings", "headerImage", "nickName", "sportDistance", "addFriendToMyGroup"};
        int[] mItemIds = new int[]{R.id.rankings, R.id.headerImage, R.id.nickName, R.id.sportDistance, R.id.addFriendToMyGroup};

        AdapterForAddFriends adapter = new AdapterForAddFriends(this, orderedByDistance(),
                R.layout.item_adding_friend, mItemCols, mItemIds, this);
        friendsListView.setAdapter(adapter);
    }
    private ArrayList<Map<String, Object>> orderedByDistance(){
        User[] orderedUsers = getOrderedUsers();
        orderedUserList = Arrays.asList(orderedUsers);

        ArrayList<Map<String, Object>> mapList = new ArrayList<>();
        for (int i = 0; i < userList.size(); i++){
            User user = orderedUsers[i];
            Map<String, Object> map = new HashMap<>();
            map.put("rankings", i + 1);
            map.put("headerImage", bitmapMap.get(user.getObjectId()));
            map.put("nickName", user.getNickName());
            map.put("sportDistance", getSportDistance(user) + " m");
            map.put("addFriendToMyGroup", R.mipmap.add);
            mapList.add(map);
        }
        return mapList;
    }
    private User[] getOrderedUsers(){
        User[] orderedUsers = new User[userList.size()];
        userList.toArray(orderedUsers);
        for (int i = 0; i < userList.size() - 1; i++){
            for (int j = i + 1; j < userList.size(); j++){
                if (getSportDistance(orderedUsers[i]) < getSportDistance(orderedUsers[j])){
                    User tempUser = orderedUsers[i];
                    orderedUsers[i] = orderedUsers[j];
                    orderedUsers[j] = tempUser;
                }
            }
        }
        return orderedUsers;
    }
    private int getSportDistance(User user){
        return user.getWalkDistance() + user.getRunDistance() + user.getRideDistance();
    }
    @Override
    public void click(View view) {
        LinearLayout parent = (LinearLayout)view.getParent();
        int index = Integer.parseInt(((TextView)parent.findViewById(R.id.rankings)).getText().toString()) - 1;

        int tag = Integer.parseInt((String)view.getTag());
        if (tag == 0){
            ((ImageView)view).setImageResource(R.mipmap.ic_gou);
            view.setTag(1 + "");
            selectedUserObjectIdMap.put(orderedUserList.get(index).getObjectId(), orderedUserList.get(index).getObjectId());
        }else {
            ((ImageView)view).setImageResource(R.mipmap.add);
            view.setTag(0 + "");
            selectedUserObjectIdMap.remove(orderedUserList.get(index).getObjectId());
        }
    }
}
