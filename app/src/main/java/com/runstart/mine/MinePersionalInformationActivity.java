package com.runstart.mine;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.runstart.BmobBean.User;
import com.runstart.R;
import com.runstart.friend.ChatActivity;
import com.runstart.friend.PhotoUtilsCircle;
import com.runstart.friend.adapter.PhotoUtils;
import com.runstart.help.ActivityCollector;
import com.runstart.help.ToastShow;
import com.runstart.history.MyApplication;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;


/**
 * Created by zhouj on 2017-10-09.
 */

public class MinePersionalInformationActivity extends Activity implements View.OnClickListener {

    private ImageView myHeadImg;
    private Button mineSetBtn;
    private TextView myNameTv, myLocationTv, myMailBoxTv;
    private User user;
    private String USER_IMG_PATH;
    private File userImgFile;
    private final int GET_IMG_BMOB = 1;
    private String myName, myHead, myLocation, myMailBox;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_mine_personalinformation);
        ActivityCollector.addActivity(this);
        USER_IMG_PATH = Environment.getExternalStorageDirectory() + File.separator
                + getPackageName() + File.separator + "myimages/userHeadImg.png";
        user = (User) getIntent().getSerializableExtra("userInfo");
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
        } else {
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
                MinePersionalInformationActivity.this.finish();
                break;

            case R.id.mine_personalinformation_content_rl_second:
                AlertDialog.Builder builder = new AlertDialog.Builder(MinePersionalInformationActivity.this);
                builder.setTitle("头像")
                        .setMessage("选择上传头像的方式")
                        .setPositiveButton("拍照", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PhotoUtilsCircle.photograph(MinePersionalInformationActivity.this);
                            }
                        })
                        .setNegativeButton("相册", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PhotoUtilsCircle.selectPictureFromAlbum(MinePersionalInformationActivity.this);
                            }
                        })
                        .show();


                break;
            case R.id.mine_personalinformation_content_rl_three:

                break;
            case R.id.mine_personalinformation_content_rl_four:
                break;
            case R.id.mine_personalinformation_content_rl_five:
                break;

            case R.id.mine_set_info:


                if (setMyInfo()) {
                    pushHeadImg();
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

    public void pushHeadImg() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final BmobFile bmobFile = new BmobFile(new File(bxfPath));
                bmobFile.uploadblock(new UploadFileListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Message message = new Message();
                            message.what = 2;
                            deleteLastHeadImg(user.getHeaderImageUri());

                            user.setHeaderImageUri(bmobFile.getFileUrl());
                            handler.sendMessage(message);
                            ToastShow.showToast(MinePersionalInformationActivity.this, "上传图片成功！！");
                        } else {
                            ToastShow.showToast(MinePersionalInformationActivity.this, "上传图片失败！");
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
                BmobFile bmobFile = new BmobFile("userHeadImg", "", user.getHeaderImageUri());
                bmobFile.download(userImgFile, new DownloadFileListener() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (USER_IMG_PATH.equals(s)) {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        } else {
                            ToastShow.showToast(MinePersionalInformationActivity.this, "下载头像出错！");
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
                if (e == null) {
                    ToastShow.showToast(MinePersionalInformationActivity.this, "修改成功！");
                } else {
                    ToastShow.showToast(MinePersionalInformationActivity.this, "更新出错！");
                }
            }
        });
    }

    public void deleteLastHeadImg(String url) {
        BmobFile bmobFile = new BmobFile("userHeadImg", "", url);
        Log.e("bmob", "之前头像地址" + user.getHeaderImageUri());
        bmobFile.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
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
        super.onActivityResult(requestCode, resultCode, data);
        String str = PhotoUtilsCircle.myPictureOnResultOperate(requestCode, resultCode, data, this,"userHeadImg");
        if (str.length() > 3) {
            if (str.substring(0, 3).equals("bxf"))
                bxfPath = str.substring(3);
        } else if (str.equals("3")) {

            PhotoUtilsCircle.showImage(myHeadImg, bxfPath);
        }
        Log.e("database", "str:" + str);
    }
}
