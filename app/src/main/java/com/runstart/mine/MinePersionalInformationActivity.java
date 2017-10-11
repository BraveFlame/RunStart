package com.runstart.mine;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.runstart.R;
import com.runstart.friend.ChatActivity;
import com.runstart.friend.PhotoUtilsCircle;
import com.runstart.help.ActivityCollector;


/**
 * Created by zhouj on 2017-10-09.
 */

public class MinePersionalInformationActivity extends Activity implements View.OnClickListener{

    private ImageView myHeadImg;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_mine_personalinformation);
        ActivityCollector.addActivity(this);
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
    public void pInformationInitView(){
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

        myHeadImg=(ImageView)findViewById(R.id.mine_myhearimg);




    }

    /**
     * 定义OnclickListener
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mine_personalinformation_iv_zuojiantou:
                MinePersionalInformationActivity.this.finish();
                break;

            case R.id.mine_personalinformation_content_rl_second:
                AlertDialog.Builder builder=new AlertDialog.Builder(MinePersionalInformationActivity.this);
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
            default:
                break;
        }
    }

    /**
     * 调用拍照或者照片，裁剪后直接发送
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    private String bxfPath = "";
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String str = PhotoUtilsCircle.myPictureOnResultOperate(requestCode, resultCode, data, this);
        if (str.length() > 3) {
            if (str.substring(0, 3).equals("bxf"))
                bxfPath = str.substring(3);
        } else if (str.equals("3")) {
            //发送图片
            PhotoUtilsCircle.showImage(myHeadImg, bxfPath);
        }
        Log.e("database", "str:" + str);
    }
}
