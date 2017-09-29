package com.runstart.circle;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.runstart.BmobBean.ActivityAndMember;
import com.runstart.BmobBean.ActivityData;
import com.runstart.BmobBean.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by 10605 on 2017/9/26.
 */

public class GetFromBmob {

    static int myi;
    static Map<String, ImageView> map;

    public static Message ADintoMessage(ActivityData activityData) {
        Message msg = new Message();
        msg.what = 1;
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        String str = gson.toJson(activityData);
        bundle.putString("ActivityData", str);
        msg.setData(bundle);
        return msg;
    }

    public static Message MTAintoMessage(ActivityAndMember activityAndMember) {
        Message msg = new Message();
        msg.what = 2;
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        String str = gson.toJson(activityAndMember);
        bundle.putString("ActivityAndMember", str);
        msg.setData(bundle);
        return msg;
    }

    public static Message AMLintoMessage(List list) {
        Message msg = new Message();
        msg.what = 5;
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        String str = gson.toJson(list);
        bundle.putString("MTAlist", str);
        msg.setData(bundle);
        return msg;
    }

    public static Message UserIntoMessage(User user) {
        Message msg = new Message();
        msg.what = 3;
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        String str = gson.toJson(user);
        bundle.putString("User", str);
        msg.setData(bundle);
        return msg;
    }

    static public void getAD(String activityId, final Handler handler) {
        BmobQuery<ActivityData> bmobQuery = new BmobQuery<ActivityData>();
        bmobQuery.getObject(activityId, new QueryListener<ActivityData>() {
            @Override
            public void done(ActivityData object, BmobException e) {
                if (e == null) {
                    Log.e("database", "查询成功1");
                    handler.sendMessage(ADintoMessage(object));
                } else {
                    Log.e("database", "查询失败1：" + e.getMessage());
                }
            }
        });
    }

    static public void getAAM(String activityId, String myId, final Handler handler) {
        BmobQuery<ActivityAndMember> bmobQuery = new BmobQuery<ActivityAndMember>();
        bmobQuery.addWhereEqualTo("memberId", myId);
        bmobQuery.addWhereEqualTo("activityId", activityId);
        bmobQuery.findObjects(new FindListener<ActivityAndMember>() {
            @Override
            public void done(List<ActivityAndMember> object, BmobException e) {
                if (e == null) {
                    Log.e("bmob", "查询成功2：共" + object.size() + "条数据。");
                    handler.sendMessage(MTAintoMessage(object.get(0)));
                } else {
                    Log.e("bmob", "失败2：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    static public void getActivityMember(String activityId, final Handler handler) {
        BmobQuery<ActivityAndMember> bmobQuery = new BmobQuery<ActivityAndMember>();
        bmobQuery.addWhereEqualTo("activityId", activityId);
        bmobQuery.findObjects(new FindListener<ActivityAndMember>() {
            @Override
            public void done(List<ActivityAndMember> object, BmobException e) {
                if (e == null) {
                    Log.e("bmob", "查询成功2：共" + object.size() + "条数据。");
                    for (ActivityAndMember gameScore : object) {
                        Log.e("database", "成员Id：" + gameScore.getMemberId());
                    }
                    handler.sendMessage(AMLintoMessage(object));
                } else {
                    Log.e("bmob", "失败2：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    static public void getUserImage(final ArrayList<String> userIds, final ArrayList<ImageView> imageViews, final Handler handler) {
        BmobQuery<User> bmobQuery;
        map = new HashMap<>();
        for (int i = 0; ((i < userIds.size()) & (i < 6)); i++) {
            map.put(userIds.get(i), imageViews.get(i));
        }
        for (myi = 0; ((myi < userIds.size()) & (myi < 6)); myi++) {
            bmobQuery = new BmobQuery<>();
            bmobQuery.getObject(userIds.get(myi), new QueryListener<User>() {
                @Override
                public void done(User object, BmobException e) {
                    if (e == null) {
                        Log.e("database", "查询成功3" + object.getHeaderImageUri() + ",objectId:" + object.getObjectId());
                        ImageView imageViewToShow = map.get(object.getObjectId());
                        Picasso.with(imageViews.get(0).getContext()).load(object.getHeaderImageUri()).noFade().into(imageViewToShow);
                        handler.sendEmptyMessage(6);
                    } else {
                        Log.e("database", "查询失败3：" + e.getMessage());
                    }
                }
            });
        }

    }

    static public void getUserById(String userId, final Handler handler) {
        BmobQuery<User> bmobQuery = new BmobQuery<User>();
        bmobQuery.getObject(userId, new QueryListener<User>() {
            @Override
            public void done(User object, BmobException e) {
                if (e == null) {
                    Log.e("database", "查询成功3");
                    handler.sendMessage(UserIntoMessage(object));
                } else {
                    Log.e("database", "查询失败3：" + e.getMessage());
                }
            }
        });
    }

    static public void updateAAM(ActivityAndMember activityAndMember, final Handler handler) {
        activityAndMember.update(activityAndMember.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.e("database", "更新成功");
                    handler.sendEmptyMessage(4);
                } else {
                    Log.e("database", "更新失败：" + e.getMessage());
                }
            }
        });
    }

    static public void delete(String id) {
        final ActivityAndMember activityAndMember = new ActivityAndMember();
        activityAndMember.setObjectId(id);
        activityAndMember.delete(new UpdateListener() {

            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.e("database", "删除成功:" + activityAndMember.getUpdatedAt());
                } else {
                    Log.e("database", "删除失败：" + e.getMessage());
                }
            }

        });
    }
}
