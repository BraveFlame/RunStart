package com.runstart.bottom;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.runstart.R;
import com.runstart.mine.MineAboutSportActivity;
import com.runstart.mine.MineExerciseDiaryActivity;
import com.runstart.mine.MineMessageRecordActivity;
import com.runstart.mine.MineOurMallActivity;
import com.runstart.mine.MinePersionalInformationActivity;
import com.runstart.mine.MineSetUpActivity;
import com.runstart.mine.MyHeaderImageView;


/**
 * Created by zhouj on 2017-09-08.
 */

public class MineFragment extends BaseFragment implements View.OnClickListener{
    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       view = inflater.inflate(R.layout.fragment_mine, container, false);

        initMineView();
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
        MyHeaderImageView iv_userPortrait = (MyHeaderImageView) view.findViewById(R.id.mine_user_headImage);
        iv_userPortrait.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mine_user_headImage:
                Intent personalIntent = new Intent(getActivity(), MinePersionalInformationActivity.class);
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
}
