package com.runstart.bottom;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Created by zhouj on 2017-09-08.
 */

public class BaseFragment extends Fragment {
    protected Context mcontext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mcontext=getContext();
    }
}
