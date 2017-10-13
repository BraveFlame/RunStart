package com.runstart.sport_fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.runstart.R;
import com.runstart.help.GetSHA1;


public class RunFragment extends Fragment{
    private FragmentRunFirstPage fragmentRunFirstPage;

    private View view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_run, container, false);
        initView();
        // GetSHA1.getCertificateSHA1Fingerprint(getContext());
        Log.e("SHA", GetSHA1.getCertificateSHA1Fingerprint(getContext()));
        return view;
    }
    /**
     * 初始化View
     */
    private void initView() {
        fragmentRunFirstPage = new FragmentRunFirstPage();

        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.first_run_page, fragmentRunFirstPage)
                .commit();


    }

}
