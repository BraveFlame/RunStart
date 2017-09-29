package com.runstart.help;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

import com.runstart.R;

/**
 * Created by user on 17-9-27.
 */

public class LockScreenActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_walk_secondpage);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

    }

    @Override
    public void onBackPressed() {

    }
}
