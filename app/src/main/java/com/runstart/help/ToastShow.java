package com.runstart.help;

import android.content.Context;
import android.widget.Toast;

/**解决短时间内连续触发Toast的频发显示问题
 * Created by user on 17-8-7.
 */

public class ToastShow {
    private static Toast toast;

    private ToastShow(){

    }
    public static void showToast(Context context, String text) {
        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);

        } else {
            toast.setText(text);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    public static void cancelToast() {
        if (toast != null) {
            toast.cancel();
        }
    }

}
