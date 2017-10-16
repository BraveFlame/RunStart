package com.runstart.friend;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.ListView;

import com.runstart.BmobBean.User;

import org.json.JSONObject;

import java.io.File;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.UpdateListener;

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
        if (content == null || content.equals(""))
            return "";
        return content;
    }


    public static void getUser(JSONObject data,User user){
        if (user==null)
            return;
        try {
            JSONObject jsonObject=data.getJSONObject("data");
            user.setLikeNumberForHistory(Integer.valueOf(jsonObject.getString("likeNumberForHistory")));
        }catch (Exception e){
            e.printStackTrace();
        }
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
    public static List<MsgChat> getLeaveMsg(MsgAdapter adapter, ListView listView, List<MsgChat> list, String leaveMsg,
                                            Context context, String userID, String friendId) {
        LocalChatLog chatDb = LocalChatLog.getLocalChatLog(context);
        String[] msg;
        msg = leaveMsg.split("\\.\\*\\.\\|\\*\\|");
        for (int i = 0; i < msg.length - 1; i++) {
            //接收到图片
            if (msg[i + 1].contains("http://bmob-cdn-14232.b0.upaiyun.com")) {
                String picName = System.currentTimeMillis() + ".png";
                String content = msg[i + 1].substring(0, msg[i + 1].length() - 19);
                String time = msg[i + 1].substring(msg[i + 1].length() - 19, msg[i + 1].length());
                BmobFile bmobFile = new BmobFile(picName, "", content);
                recImg(adapter, listView, bmobFile, picName, time, context, userID, friendId, chatDb, list);
                Log.e("bmob", "file:" + msg[i + 1]);
            }
            //接收到文字或表情
            else {

                recMsg(adapter, listView, msg[1 + i], userID, friendId, chatDb, list);

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
    public static void recImg(final MsgAdapter adapter, final ListView listView,
                              final BmobFile bmobFile, String name, final String time, Context context,
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
                        recMsg(adapter, listView, s + time, userId, friendId, chatDb, list);
                    Log.e("bmob", "bmobFileurl" + bmobFile.getUrl());
                    //删除bmob上面图片
                    bmobFile.delete(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Log.e("bmob", "bmobFileurl删除成功");
                            } else {
                                Log.e("bmob", "bmobFileurl删除失败");
                            }
                        }
                    });

                }
            }

            @Override
            public void onProgress(Integer integer, long l) {

            }
        });
    }

    /**
     * 将文字保存
     *
     * @param lastRecContent
     */
    public static void recMsg(MsgAdapter adapter, ListView listView, String lastRecContent, String userID, String friendId,
                              LocalChatLog chatDb, List<MsgChat> list) {
        String content = lastRecContent.substring(0, lastRecContent.length() - 19);
        String time = lastRecContent.substring(lastRecContent.length() - 19, lastRecContent.length());

        MsgChat msgChat = new MsgChat();
        msgChat.setContent(content);
        msgChat.setTime(time);
        msgChat.setType(MsgChat.TYPE_RECEIVED);
        chatDb.saveLocalChat(msgChat, userID, friendId);
        list.add(msgChat);
        adapter.notifyDataSetChanged(); // 当有新消息时,刷新
        listView.setSelection(list.size()); // 将ListView
    }

}
