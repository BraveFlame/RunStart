package com.runstart.bottom;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.runstart.R;
import com.runstart.circle.CircleCreateActivity;
import com.runstart.circle.CirclePushCardActivity;
import com.runstart.mine.MineGroupActivity;
import com.runstart.mine.MineMessageRecordActivity;
import com.runstart.mine.MineSetUpActivity;


/**
 * Created by zhouj on 2017-09-08.
 */

public class MineFragment extends BaseFragment implements View.OnClickListener{

    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       view = inflater.inflate(R.layout.fragment_mine, container, false);
        initView();
        return view;
    }
    /**
     * 初始化组件
     */
    public void initView(){
        //pushCard的RelativeLayout
        RelativeLayout rl_pushCard = (RelativeLayout) view.findViewById(R.id.mine_rl_pushcard);
        rl_pushCard.setOnClickListener(this);
        //goodFriend的RelativeLayout
        RelativeLayout rl_goodFriend = (RelativeLayout) view.findViewById(R.id.mine_rl_goodfriend);
        rl_goodFriend.setOnClickListener(this);
        //group的RelativeLayout
        RelativeLayout rl_group = (RelativeLayout) view.findViewById(R.id.mine_rl_group);
        rl_group.setOnClickListener(this);
        //messageRecord的RelativeLayout
        RelativeLayout rl_messagerecord = (RelativeLayout) view.findViewById(R.id.mine_rl_messagerecord);
        rl_messagerecord.setOnClickListener(this);
        //setUp的RelativeLayout
        RelativeLayout rl_setup= (RelativeLayout) view.findViewById(R.id.mine_rl_setup);
        rl_setup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mine_rl_pushcard:
                Intent pushcardIntent=new Intent(getActivity(),CirclePushCardActivity.class);
                startActivity(pushcardIntent);
                break;
            case R.id.mine_rl_goodfriend:
                Intent goodfriendIntent = new Intent(getActivity(),CircleCreateActivity.class);
                startActivity(goodfriendIntent);
                break;
            case R.id.mine_rl_group:
                Intent groupIntent = new Intent(getActivity(), MineGroupActivity.class);
                startActivity(groupIntent);
                break;
            case R.id.mine_rl_messagerecord:
                Intent messagerecordIntent = new Intent(getActivity(), MineMessageRecordActivity.class);
                startActivity(messagerecordIntent);
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
