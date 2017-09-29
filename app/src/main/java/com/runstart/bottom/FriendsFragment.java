package com.runstart.bottom;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.runstart.R;
import com.runstart.friend.ChatActivity;


/**
 * Created by zhouj on 2017-09-08.
 */

public class FriendsFragment extends BaseFragment {

    Button startChatBtn;
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        startChatBtn=(Button) view.findViewById(R.id.start_chat_btn);
        startChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("name","独孤求败");
                startActivity(intent);
            }
        });
        return view;
    }
}
