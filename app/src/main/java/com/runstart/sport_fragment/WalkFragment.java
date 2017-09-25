package com.runstart.sport_fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.runstart.R;


public class WalkFragment extends Fragment {

    private FragmentWalkFirstPage fragmentWalkFirstPage;
    private FragmentWalkSecondPage fragmentWalkSecondPage;

    private View view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_walk, container, false);
        initView();
        return view;
    }
    /**
     * 初始化View
     */
    private void initView() {
        fragmentWalkFirstPage = new FragmentWalkFirstPage();
        fragmentWalkSecondPage = new FragmentWalkSecondPage();

        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.first_page, fragmentWalkFirstPage).add(R.id.second_page, fragmentWalkSecondPage)
                .commit();


    }

}
