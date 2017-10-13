package com.runstart.bottom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.runstart.BmobBean.User;
import com.runstart.R;
import com.runstart.friend.ListenMsgService;
import com.runstart.friend.MsgChat;
import com.runstart.friend.PhotoUtilsCircle;
import com.runstart.help.ToastShow;
import com.runstart.history.MyApplication;
import com.runstart.mine.MineAboutSportActivity;
import com.runstart.mine.MineExerciseDiaryActivity;
import com.runstart.mine.MineMessageRecordActivity;
import com.runstart.mine.MineOurMallActivity;
import com.runstart.mine.MinePersionalInformationActivity;
import com.runstart.mine.MineSetUpActivity;
import com.runstart.mine.MyHeaderImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.UpdateListener;


/**
 * Created by zhouj on 2017-09-08.
 */

public class MineFragment extends BaseFragment implements View.OnClickListener{
    User user;
    View view;
    private ImageView myHeadImg;
    private String USER_IMG_PATH;
    private File userImgFile;
    private TextView msgCountTextView;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:
                    PhotoUtilsCircle.showImage(myHeadImg,USER_IMG_PATH);
                    break;
                case 2:
                    user.update(MyApplication.userObjectIdKey, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                ToastShow.showToast(getContext(),"修改成功！");
                            }else {
                                ToastShow.showToast(getContext(),"更新出错！");
                            }
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user=new User();
        user.setObjectId(MyApplication.applicationMap.get(MyApplication.userObjectIdKey));
        USER_IMG_PATH= Environment.getExternalStorageDirectory() + File.separator
                + getContext().getPackageName() + File.separator+"myimages/userHeadImg.png";
        userImgFile=new File(USER_IMG_PATH);
        Log.e("bmob",user.getObjectId());

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       view = inflater.inflate(R.layout.fragment_mine, container, false);
        initMineView();
        BmobQuery<User>userBmobQuery=new BmobQuery<>();
        userBmobQuery.getObject(user.getObjectId(), new QueryListener<User>() {
            @Override
            public void done(User users, BmobException e) {
                if(e==null){
                    user=users;
                    if(!userImgFile.exists()) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                BmobFile bmobFile=new BmobFile("userHeadImg","",user.getHeaderImageUri());
                                bmobFile.download(userImgFile, new DownloadFileListener() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        if(USER_IMG_PATH.equals(s)){
                                            Message message = new Message();
                                            message.what = 1;
                                            handler.sendMessage(message);
                                        }else {
                                            ToastShow.showToast(getContext(),"头像出错！");
                                        }
                                    }

                                    @Override
                                    public void onProgress(Integer integer, long l) {

                                    }
                                });
                            }
                        }).start();
                    }

                }else {
                    ToastShow.showToast(getContext(),"Failed");
                }
            }
        });
        return view;
    }
    /**
     * 初始化组件
     */
    public void initMineView(){
        //pushCard的RelativeLayout
        RelativeLayout rl_exercisediary = (RelativeLayout) view.findViewById(R.id.mine_rl_exercisediary);
        rl_exercisediary.setOnClickListener(this);
        //goodFriend的RelativeLayout
        RelativeLayout rl_aboutsport = (RelativeLayout) view.findViewById(R.id.mine_rl_aboutsport);
        rl_aboutsport.setOnClickListener(this);
        //group的RelativeLayout
        RelativeLayout rl_ourmall = (RelativeLayout) view.findViewById(R.id.mine_rl_ourmall);
        rl_ourmall.setOnClickListener(this);
        //messageRecord的RelativeLayout
        RelativeLayout rl_messagerecord = (RelativeLayout) view.findViewById(R.id.mine_rl_messagerecord);
        rl_messagerecord.setOnClickListener(this);
        //setUp的RelativeLayout
        RelativeLayout rl_setup= (RelativeLayout) view.findViewById(R.id.mine_rl_setup);
        rl_setup.setOnClickListener(this);

        //用户头像的ImageView
        myHeadImg = (MyHeaderImageView) view.findViewById(R.id.mine_user_headImage);
        myHeadImg.setOnClickListener(this);
        if(userImgFile.exists())
            PhotoUtilsCircle.showImage(myHeadImg,USER_IMG_PATH);

        msgCountTextView = (TextView)view.findViewById(R.id.msgCount);
        initMsgCount();
        IntentFilter filter = new IntentFilter(ListenMsgService.FILTER_STR);
        getActivity().registerReceiver(new MsgCountReceiver(), filter);

    }

    @Override
    public void onResume() {
        super.onResume();
        if(userImgFile.exists())
            PhotoUtilsCircle.showImage(myHeadImg,USER_IMG_PATH);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mine_user_headImage:
                Intent personalIntent = new Intent(getActivity(), MinePersionalInformationActivity.class);
                personalIntent.putExtra("userInfo",user);
                startActivity(personalIntent);
                break;
            case R.id.mine_rl_exercisediary:
                Intent exDairyIntent=new Intent(getActivity(),MineExerciseDiaryActivity.class);
                startActivity(exDairyIntent);
                break;
            case R.id.mine_rl_aboutsport:
                Intent aboutSportIntent = new Intent(getActivity(),MineAboutSportActivity.class);
                startActivity(aboutSportIntent);
                break;
            case R.id.mine_rl_ourmall:
                Intent ourMallIntent = new Intent(getActivity(), MineOurMallActivity.class);
                startActivity(ourMallIntent);
                break;
            case R.id.mine_rl_messagerecord:
                Intent messageRecordIntent = new Intent(getActivity(), MineMessageRecordActivity.class);
                startActivity(messageRecordIntent);
                break;
            case R.id.mine_rl_setup:
                Intent setupIntent = new Intent(getActivity(), MineSetUpActivity.class);
                startActivity(setupIntent);
                break;
            default:
                break;
        }
    }


    public class MsgCountReceiver extends BroadcastReceiver {
        public MsgCountReceiver(){
            super();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Map<String, Integer> msgCountMap =
                    ((ArrayList<Map<String, Integer>>)intent.getSerializableExtra("msgCountMapLoader")).get(0);
            int msgCount = 0;
            for (Map.Entry<String, Integer> entry:msgCountMap.entrySet()){
                int data = entry.getValue();
                msgCount += data;
            }
            msgCountTextView.setVisibility(View.VISIBLE);
            msgCountTextView.setText(msgCount + "");
            if (msgCount > 99){
                msgCountTextView.setText("99+");
            }
            if (msgCount == 0){
                msgCountTextView.setVisibility(View.GONE);
            }
        }
    }

    private Map<String, MsgChat> msgChatMap = new HashMap<>();
    private Map<String, Integer> msgCountMap = new HashMap<>();
    private void initMsgCount(){
        new BmobQuery<MsgChat>().setSQL("select * from MsgChat where friendObjectId=?")
                .setPreparedParams(new String[]{MyApplication.applicationMap.get(MyApplication.userObjectIdKey)})
                .doSQLQuery(new SQLQueryListener<MsgChat>() {
                    @Override
                    public void done(BmobQueryResult<MsgChat> bmobQueryResult, BmobException e) {
                        List<MsgChat> msgChatList = bmobQueryResult.getResults();
                        for (MsgChat msgChat: msgChatList){
                            msgChatMap.put(msgChat.getUserObjectId(), msgChat);
                        }
                        int msgCount = 0;
                        for (Map.Entry<String, MsgChat> entry:msgChatMap.entrySet()) {
                            String key = entry.getKey();
                            String data = entry.getValue().toString();
                            if (data == null || data.equals("0")) {
                                msgCountMap.put(key, 0);
                                continue;
                            } else {
                                int count = data.split("\\.\\*\\.\\|\\*\\|").length - 1;
                                msgCountMap.put(key, count);
                                msgCount += count;
                            }
                        }
                        if (msgCount == 0){
                            msgCountTextView.setVisibility(View.GONE);
                        } else {
                            msgCountTextView.setVisibility(View.VISIBLE);
                            msgCountTextView.setText(msgCount + "");
                            if (msgCount > 99){
                                msgCountTextView.setText("99+");
                            }
                        }
                    }
                });
    }
}
