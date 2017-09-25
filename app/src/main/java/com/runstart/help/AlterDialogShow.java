package com.runstart.help;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by user on 17-9-19.
 */

public class AlterDialogShow {
    private static AlertDialog.Builder alterDialog;
    private AlterDialogShow(){}
    public synchronized static AlertDialog.Builder getAlter(Context context){
       if(alterDialog==null){
           alterDialog=new AlertDialog.Builder(context);
       }
        return alterDialog;
    }

    public void showAlter(String title,String message,int img){
        alterDialog.setTitle(title)
                .setMessage(message)
                .setIcon(img)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alterDialog.show();
    }
}
