package com.runstart.friend;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Adapter;
import android.widget.ListView;

import org.json.JSONObject;

import java.io.File;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;

/**
 * Created by g on 2017/10/2.
 */

public class BmobJdonChat {

    /*
    {"appKey":"6bbf7fb6372e29f54eead6a98a204621","tableName":"MsgChat","objectId":"","action":"updateTable",
    "data":{"content":"大家好","createdAt":"2017-10-02 11:55:32","friendObjectId":"54c48f1bfc","objectId":"2cac02631b","type":1,
    "updatedAt":"2017-10-02 11:55:32","userObjectId":"033d152e41"}}
     */
    //解析出某个节点要的数据
    public static String jsonToString(JSONObject data, String point) {
        String content = "";
        try {
            JSONObject jsonObject = data.getJSONObject("data");
            content = jsonObject.getString(point);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (content.equals(""))
            return "";
        return content;
    }

    /**
     * 留言以.*.|*|为间隔
     *
     * @param list
     * @param leaveMsg
     * @param context
     * @param userID
     * @param friendId
     * @return
     */
    public static List<MsgChat> getLeaveMsg(MsgAdapter adapter, ListView listView,List<MsgChat> list, String leaveMsg,
                                            Context context, String userID, String friendId) {
        LocalChatLog chatDb = LocalChatLog.getLocalChatLog(context);
        String[] msg;
        msg = leaveMsg.split("\\.\\*\\.\\|\\*\\|");
        for (int i = 0; i < msg.length - 1; i++) {
            //接收到图片
            if (msg[i + 1].contains("http://bmob-cdn-14232.b0.upaiyun.com")) {
                String picName = System.currentTimeMillis() + ".png";
                BmobFile bmobFile = new BmobFile(picName, "", msg[i + 1]);
                recImg(adapter,listView,bmobFile, picName, context, userID, friendId, chatDb, list);
            }
            //接收到文字或表情
            else {

                recMsg(adapter,listView,msg[1 + i], userID, friendId, chatDb, list);

            }

        }
        return list;
    }

    /**
     * 接收的是图片链接时，先下载到本地，再发送本地地址到MsgChat的content字段
     *
     * @param bmobFile
     * @param name
     */
    public static void recImg(final MsgAdapter adapter,final ListView listView,
                              BmobFile bmobFile, String name, Context context,
                              final String userId, final String friendId,
                              final LocalChatLog chatDb, final List<MsgChat> list) {
        // 设置文件保存路径这里放在del目录下
        final File file;
        String path;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            //SDCard是否可用
            path = Environment.getExternalStorageDirectory() + File.separator + context.getPackageName() + File.separator + "myimages/" + name;
            file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
        } else {
            path = context.getFilesDir() + File.separator + context.getPackageName() + File.separator + "myimages/" + name;
            file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
        }

        bmobFile.download(file, new DownloadFileListener() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    //获取成功
                    if (s != null)
                        recMsg(adapter,listView,s, userId, friendId, chatDb, list);

                }
            }

            @Override
            public void onProgress(Integer integer, long l) {

            }
        });
    }

    /**
     * 将文字或图片的本地地址传入
     *
     * @param lastRecContent
     */
    public static void recMsg(MsgAdapter adapter,ListView listView,String lastRecContent, String userID, String friendId,
                              LocalChatLog chatDb, List<MsgChat> list) {
        MsgChat msgChat = new MsgChat();
        msgChat.setContent(lastRecContent);
        msgChat.setType(MsgChat.TYPE_RECEIVED);
        chatDb.saveLocalChat(msgChat, userID, friendId);
        list.add(msgChat);
        adapter.notifyDataSetChanged(); // 当有新消息时,刷新
        listView.setSelection(list.size()); // 将ListView
    }

}
