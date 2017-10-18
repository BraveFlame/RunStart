package com.runstart.mine;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.runstart.BmobBean.User;
import com.runstart.R;
import com.runstart.friend.PhotoUtilsCircle;
import com.runstart.help.ActivityCollector;
import com.runstart.help.ToastShow;
import com.runstart.mine.location.City;
import com.runstart.mine.location.PInfoLocationActivity;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;


/**
 * Created by zhouj on 2017-10-09.
 */

public class MinePersonalInformationActivity extends Activity implements View.OnClickListener {

    private ImageView myHeadImg;
    private Button mineSetBtn;
    private TextView myNameTv, myLocationTv, myMailBoxTv;
    private User user;
    private String USER_IMG_PATH;
    private File userImgFile;
    private final int GET_IMG_BMOB = 1;
    private String myName, myHead, myLocation, myMailBox;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private ProgressDialog dialog;

    private boolean isChangeImg;


    private City city;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_mine_personalinformation);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        ActivityCollector.addActivity(this);
        user = (User) getIntent().getSerializableExtra("userInfo");
        USER_IMG_PATH = Environment.getExternalStorageDirectory() + File.separator
                + getPackageName() + File.separator + "myimages/" + user.getObjectId() + "userHeadImg.png";
        city=new City();
        pInformationInitView();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    /**
     * 初始化组件
     */
    public void pInformationInitView() {
        //初始化fragment_mine_personalinformation.xml的布局组件
        ImageView iv_zuojiantou = (ImageView) findViewById(R.id.mine_personalinformation_iv_zuojiantou);
        iv_zuojiantou.setOnClickListener(this);
        RelativeLayout rl_addportrait = (RelativeLayout) findViewById(R.id.mine_personalinformation_content_rl_second);
        rl_addportrait.setOnClickListener(this);
        RelativeLayout rl_setname = (RelativeLayout) findViewById(R.id.mine_personalinformation_content_rl_three);
        rl_setname.setOnClickListener(this);
        RelativeLayout rl_location = (RelativeLayout) findViewById(R.id.mine_personalinformation_content_rl_four);
        rl_location.setOnClickListener(this);
        RelativeLayout rl_mailbox = (RelativeLayout) findViewById(R.id.mine_personalinformation_content_rl_five);
        rl_mailbox.setOnClickListener(this);

        myHeadImg = (ImageView) findViewById(R.id.mine_myhearimg);
        myNameTv = (TextView) findViewById(R.id.mine_set_name);
        myLocationTv = (TextView) findViewById(R.id.mine_set_location);
        myMailBoxTv = (TextView) findViewById(R.id.mine_set_mailbox);
        userImgFile = new File(USER_IMG_PATH);
        if (userImgFile.exists()) {
            PhotoUtilsCircle.showImage(myHeadImg, USER_IMG_PATH);
            Log.e("bmob", "之前头像地址" + USER_IMG_PATH);
        }
        if(!userImgFile.exists()&&user.getHeaderImageUri()!=null){
            pullHeadImg();
        }


        myNameTv.setText(user.getNickName());
        myLocationTv.setText(user.getLocation());
        myMailBoxTv.setText(user.getMailBox());

        mineSetBtn = (Button) findViewById(R.id.mine_set_info);
        mineSetBtn.setOnClickListener(this);


    }

    /**
     * 定义OnclickListener
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_personalinformation_iv_zuojiantou:
                MinePersonalInformationActivity.this.finish();
                break;

            case R.id.mine_personalinformation_content_rl_second:
                AlertDialog.Builder builder = new AlertDialog.Builder(MinePersonalInformationActivity.this);
                builder.setTitle("Head Img")
                        .setMessage("please choose the way to update")
                        .setPositiveButton("camera", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PhotoUtilsCircle.photograph(MinePersonalInformationActivity.this);
                            }
                        })
                        .setNegativeButton("album", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PhotoUtilsCircle.selectPictureFromAlbum(MinePersonalInformationActivity.this);
                            }
                        })
                        .show();

                break;
            case R.id.mine_personalinformation_content_rl_three:
                Intent pInfoSetNameIntent = new Intent(MinePersonalInformationActivity.this, MinePInfoSetNameActivity.class);
                pInfoSetNameIntent.putExtra("newname", myNameTv.getText().toString());
                startActivityForResult(pInfoSetNameIntent,11);
                break;
            case R.id.mine_personalinformation_content_rl_four:
                Intent locationIntent = new Intent(MinePersonalInformationActivity.this, PInfoLocationActivity.class);
                locationIntent.putExtra("city", city);
                startActivityForResult(locationIntent,12);
                break;
            case R.id.mine_personalinformation_content_rl_five:
                Intent pInfoMailBoxIntent = new Intent(MinePersonalInformationActivity.this, MinePInfoMailBoxActivity.class);
                pInfoMailBoxIntent.putExtra("newmailbox", myMailBoxTv.getText().toString());
                startActivityForResult(pInfoMailBoxIntent,13);
                break;
            case R.id.mine_set_info:

                dialog = new ProgressDialog(this);
                dialog.setMessage("changing...");
                dialog.show();

                if (setMyInfo()) {
                    //选中才需要换头像
                    if (isChangeImg)
                    pushHeadImg();
                    else updateUser();
                }


                break;
            default:
                break;
        }
    }

    public boolean setMyInfo() {

        myName = myNameTv.getText().toString();
        myLocation = myLocationTv.getText().toString();
        myMailBox = myMailBoxTv.getText().toString();

        if (!"".equals(myHead) && !"".equals(myName) && !"".equals(myLocation) && !"".equals(myMailBox)) {
            user.setNickName(myName);
            user.setLocation(myLocation);
            myMailBoxTv.setText(myMailBox);
            return true;
        } else return false;


    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case GET_IMG_BMOB:
                    PhotoUtilsCircle.showImage(myHeadImg, USER_IMG_PATH);
                    break;
                case 2:
                    updateUser();
                    break;
                default:
                    break;
            }
        }
    };

    public void pushHeadImg() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if ("".equals(bxfPath)) {
                    bxfPath = USER_IMG_PATH;
                }
                final BmobFile bmobFile = new BmobFile(new File(bxfPath));
                bmobFile.uploadblock(new UploadFileListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Message message = new Message();
                            message.what = 2;
                            try {
                                if (null != user.getHeaderImageUri())
                                    deleteLastHeadImg(user.getHeaderImageUri());
                            } catch (Exception ee) {
                                Log.e("bmob", "删除原来头像失败");

                            } finally {
                                user.setHeaderImageUri(bmobFile.getFileUrl());
                                handler.sendMessage(message);
                                // ToastShow.showToast(MinePersonalInformationActivity.this, "上传图片成功！！");
                            }

                        } else {
                            dialog.dismiss();
                            ToastShow.showToast(MinePersonalInformationActivity.this, "图片未改变！");
                        }
                    }

                    @Override
                    public void onProgress(Integer value) {

                    }
                });
            }
        }).start();
    }

    public void pullHeadImg() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                BmobFile bmobFile = new BmobFile(user.getObjectId() + "userHeadImg", "", user.getHeaderImageUri());
                bmobFile.download(userImgFile, new DownloadFileListener() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (USER_IMG_PATH.equals(s)) {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        } else {
                            ToastShow.showToast(MinePersonalInformationActivity.this, "download headimg error！");
                        }
                    }

                    @Override
                    public void onProgress(Integer integer, long l) {

                    }
                });
            }
        }).start();
    }

    public void updateUser() {
        user.update(user.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                dialog.dismiss();
                if (e == null) {
                    ToastShow.showToast(MinePersonalInformationActivity.this, "change successfully！");

                    if(isChangeImg){
                        editor.putString("lastImg", user.getHeaderImageUri());

                        PhotoUtilsCircle.changeImgName(user.getObjectId() + "userHeadImg",
                                MinePersonalInformationActivity.this);
                    }
                    editor.putString("name",user.getNickName());
                    editor.putString("location",user.getLocation());
                    editor.putString("mail",user.getMailBox());
                    editor.commit();
                    finish();
                } else {
                    ToastShow.showToast(MinePersonalInformationActivity.this, "change error！");
                }
            }
        });
    }

    public void deleteLastHeadImg(String url) {
        BmobFile bmobFile = new BmobFile(user.getObjectId() + "userHeadImg", "", url);
        Log.e("bmob", "之前头像地址" + user.getHeaderImageUri());
        bmobFile.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    editor.putString("name",user.getNickName());
                    editor.putString("location",user.getLocation());
                    editor.putString("mail",user.getMailBox());
                    editor.commit();
                    Log.e("bmob", "bmobFileurl删除成功");
                } else {
                    Log.e("bmob", "bmobFileurl删除失败");
                }
            }
        });
    }

    /**
     * 调用拍照或者照片，裁剪后
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    private String bxfPath = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 11:
                if (data==null)
                    return;
                if(requestCode==11){
                    String newname = data.getStringExtra("setname");
                    myNameTv.setText(newname);
                }
                return;
            case 12:
                if (data==null)
                    return;
                    city = data.getParcelableExtra("city");
                    if (city.getDistrict() != null) {
                        myLocationTv.setText(city.getProvince() + city.getDistrict());
                    } else {
                        myLocationTv.setText(city.getProvince() + city.getCity());
                    }

                return;
            case 13:
                if (data==null)
                    return;
                if(requestCode==13){
                    String newmailbox = data.getStringExtra("setmailbox");
                    myMailBoxTv.setText(newmailbox);
                }
                return;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
        String str = PhotoUtilsCircle.myPictureOnResultOperate(requestCode, resultCode, data, this, user.getObjectId() + "userHeadImgTemp");
        if (str.length() > 3) {
            if (str.substring(0, 3).equals("bxf"))
                bxfPath = str.substring(3);
        } else if (str.equals("3")) {

            isChangeImg=true;
            PhotoUtilsCircle.showImage(myHeadImg, bxfPath);
        }
        Log.e("database", "str:" + str);
    }
}
