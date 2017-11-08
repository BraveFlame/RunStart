package com.runstart.friend.chat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * Created by zhonghao.song on 17-10-9.
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
                , new String[]{userId, friendId}, null, null, "time");
        if (cursor.moveToFirst()) {
            do {
                MsgChat msgChat=new MsgChat();
                msgChat.setContent(cursor.getString(cursor.getColumnIndex("content")));
                msgChat.setType(cursor.getInt(cursor.getColumnIndex("recOrsen")));
                msgChat.setTime(cursor.getString(cursor.getColumnIndex("time")));
                list.add(msgChat);
            } while (cursor.moveToNext());
        }
        if(cursor!=null)
            cursor.close();

    }


    public void saveLocalChat(MsgChat msgChat,String userId,String friendId) {
        if(msgChat!=null){
            ContentValues values=new ContentValues();
            values.put("content",msgChat.getContent());
            values.put("userId",userId);
            values.put("friendId",friendId);
            values.put("recOrsen",msgChat.getType());

           // values.put("time",format.format(new Date()));
            values.put("time",msgChat.getTime());

            db.insert("ChatLog",null,values);

        }


    }

    /**
     * 获取最近一条信息
     * @param userId
     * @param friendId
     * @return
     */
    public String getLastMsgChat(String userId,String friendId){
        String content="";
        Cursor cursor = db.query("ChatLog", new String[]{"content","time","recOrsen","max(time)"}, "userId=? and friendId=?"
                , new String[]{userId, friendId}, null, null,null);
        if (cursor.moveToFirst()) {
            do {
                if(null!=cursor.getString(cursor.getColumnIndex("content")))
                content=cursor.getString(cursor.getColumnIndex("content"))
                        +cursor.getString(cursor.getColumnIndex("time"));
            } while (cursor.moveToNext());
        }
        if(cursor!=null)
            cursor.close();
        return content;

    }


}
