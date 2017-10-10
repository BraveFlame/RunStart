package com.runstart.friend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by user on 17-10-9.
 */

/**
 * create table ChatLog(id integer primary key autoincrement,userId text,friendId text,
 * content text,recOrsen integer,time text);
 */
public class LocalChatLog {
    public static final String DB_NAME = "chat_log";
    public static final int VERSION = 1;
    private static LocalChatLog localChatLog;
    private SQLiteDatabase db;

    private LocalChatLog(Context context) {
        LocalChatDbHelper localChatDbHelper = new LocalChatDbHelper(context, DB_NAME, null, VERSION);
        db = localChatDbHelper.getWritableDatabase();
    }


    public synchronized static LocalChatLog getLocalChatLog(Context context) {
        if (localChatLog == null) {
            localChatLog = new LocalChatLog(context);
        }
        return localChatLog;
    }


    public void getLocalChat(String userId, String friendId, List<MsgChat>list) {
        if (db == null||list==null)
            return;
        Cursor cursor = db.query("ChatLog", new String[]{"content", "time","recOrsen"}, "userId=? and friendId=?"
                , new String[]{userId, friendId}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                MsgChat msgChat=new MsgChat();
                msgChat.setContent(cursor.getString(cursor.getColumnIndex("content")));
                msgChat.setType(cursor.getInt(cursor.getColumnIndex("recOrsen")));
                list.add(msgChat);
            } while (cursor.moveToNext());
        }

    }

    private SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");

    public void saveLocalChat(MsgChat msgChat,String userId,String friendId) {
        if(msgChat!=null){
            ContentValues values=new ContentValues();
            values.put("content",msgChat.getContent());
            values.put("userId",userId);
            values.put("friendId",friendId);
            values.put("recOrsen",msgChat.getType());

            values.put("time",format.format(new Date()));

            db.insert("ChatLog",null,values);

        }


    }


}
