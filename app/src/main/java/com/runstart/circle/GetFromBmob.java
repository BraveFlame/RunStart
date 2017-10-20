package com.runstart.circle;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.runstart.BmobBean.ActivityAndMember;
import com.runstart.BmobBean.ActivityData;
import com.runstart.BmobBean.Friend;
import com.runstart.BmobBean.User;
import com.runstart.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by 10605 on 2017/9/26.
 */

public class GetFromBmob {

    static int myi;
    static Map<String,ImageView> map;
    static BmobFile bmobFile;
    static ArrayList<ActivityData> alAD1;
    static ArrayList<ActivityData> alAD2;
    static Map<String,Object> mapUser1;
    static Map<String,Object> mapUser2;
    //三号箱在显示朋友列表时启用
    static Map<String,Object> mapUser3;
    static ArrayList<String> friendIds;
    static ArrayList<ActivityData> friendReleaseActivitys;

    public static Message ADintoMessage(ActivityData activityData){
        Message msg=new Message();
        msg.what=1;
        Bundle bundle=new Bundle();
        Gson gson=new Gson();
        String str=gson.toJson(activityData);
        bundle.putString("ActivityData",str);
        msg.setData(bundle);
        return msg;
    }

    public static Message MTAintoMessage(ActivityAndMember activityAndMember){
        Message msg=new Message();
        msg.what=2;
        Bundle bundle=new Bundle();
        Gson gson=new Gson();
        String str=gson.toJson(activityAndMember);
        bundle.putString("ActivityAndMember",str);
        msg.setData(bundle);
        return msg;
    }

    public static Message AMLintoMessage(List list){
        Message msg=new Message();
        msg.what=5;
        Bundle bundle=new Bundle();
        Gson gson = new Gson();
        String str = gson.toJson(list);
        bundle.putString("MTAlist",str);
        msg.setData(bundle);
        return msg;
    }

    public static Message UserIntoMessage(User user){
        Message msg=new Message();
        msg.what=3;
        Bundle bundle=new Bundle();
        Gson gson=new Gson();
        String str=gson.toJson(user);
        bundle.putString("User",str);
        msg.setData(bundle);
        return msg;
    }

    public static Message somethingIntoMessage(Object object,String name,int what){
        Message msg=new Message();
        msg.what=what;
        Bundle bundle=new Bundle();
        Gson gson=new Gson();
        String str=gson.toJson(object);
        bundle.putString(name,str);
        msg.setData(bundle);
        return msg;
    }

    static public void getAD(String activityId, final Handler handler){
        BmobQuery<ActivityData> bmobQuery = new BmobQuery<ActivityData>();
        bmobQuery.getObject(activityId, new QueryListener<ActivityData>(){
            @Override
            public void done(ActivityData object,BmobException e) {
                if(e==null){
                    Log.e("database","查询 活动信息 成功");
                    handler.sendMessage(ADintoMessage(object));
                }else{
                    handler.sendEmptyMessage(2);
                    Log.e("database","查询 活动信息 失败：" + e.getMessage());
                }
            }
        });
    }

    static public void getAAM(String activityId, String myId, final Handler handler){
        BmobQuery<ActivityAndMember> bmobQuery = new BmobQuery<ActivityAndMember>();
        bmobQuery.addWhereEqualTo("memberId",myId);
        bmobQuery.addWhereEqualTo("activityId",activityId);
        bmobQuery.findObjects(new FindListener<ActivityAndMember>() {
            @Override
            public void done(List<ActivityAndMember> object, BmobException e) {
                if(e==null){
                    Log.e("bmob","查询 活动和成员关系表 成功：共"+object.size()+"条数据。");
                    handler.sendMessage(MTAintoMessage(object.get(0)));
                }else{
                    Log.e("bmob"," 活动和成员关系表 失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    static public void getOneAAM(String activityId, String userId,final Handler handler){
        BmobQuery<ActivityAndMember> bmobQuery = new BmobQuery<ActivityAndMember>();
        bmobQuery.addWhereEqualTo("activityId",activityId);
        bmobQuery.addWhereEqualTo("memberId",userId);
        bmobQuery.findObjects(new FindListener<ActivityAndMember>() {
            @Override
            public void done(List<ActivityAndMember> object, BmobException e) {
                if(e==null){
                    Log.e("database","查询 活动信息 成"+object.size());
                    if(object.size()==0)
                        handler.sendEmptyMessage(9);
                    else
                        handler.sendEmptyMessage(8);
                }else{
                    handler.sendEmptyMessage(9);
                    Log.e("database","查询 活动信息 失败：" + e.getMessage());
                }
            }
        });
    }

    static public void getActivityMember(String activityId, final Handler handler){
        BmobQuery<ActivityAndMember> bmobQuery = new BmobQuery<ActivityAndMember>();
        bmobQuery.addWhereEqualTo("activityId",activityId);
        bmobQuery.findObjects(new FindListener<ActivityAndMember>() {
            @Override
            public void done(List<ActivityAndMember> object, BmobException e) {
                if(e==null){
                    Log.e("bmob","查询 活动成员 成功：共"+object.size()+"条数据。");
                    for (ActivityAndMember gameScore : object) {
                        Log.e("database","成员Id："+gameScore.getMemberId());
                    }
                    handler.sendMessage(AMLintoMessage(object));
                }else{
                    Log.e("bmob","活动成员 失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    static public void getUserImage(final ArrayList<String> userIds, final ArrayList<ImageView> imageViews, final Handler handler){
        BmobQuery<User> bmobQuery;
        map=new HashMap<>();
        for (int i=0;((i<userIds.size())&(i<6));i++){
            map.put(userIds.get(i),imageViews.get(i));
        }
        for(myi=0;((myi<userIds.size())&(myi<6));myi++){
            bmobQuery = new BmobQuery<>();
            bmobQuery.getObject(userIds.get(myi), new QueryListener<User>(){
                @Override
                public void done(User object,BmobException e) {
                    if(e==null){
                        Log.e("database","查询 用户头像 成功"+object.getHeaderImageUri()+",objectId:"+object.getObjectId());
                        ImageView imageViewToShow=map.get(object.getObjectId());
                        Picasso.with(imageViews.get(0).getContext()).load(object.getHeaderImageUri()).noFade().into(imageViewToShow);
                        handler.sendEmptyMessage(6);
                    }else{
                        Log.e("database","查询 用户头像 失败：" + e.getMessage());
                        handler.sendEmptyMessage(6);
                    }
                }
            });
        }

    }

    static public void getUserById(String userId, final Handler handler){
        BmobQuery<User> bmobQuery = new BmobQuery<User>();
        bmobQuery.getObject(userId, new QueryListener<User>(){
            @Override
            public void done(User object,BmobException e) {
                if(e==null){
                    Log.e("database","靠ID查询单个用户成功");
                    handler.sendMessage(UserIntoMessage(object));
                }else{
                    Log.e("database","靠ID查询单个用户失败：" + e.getMessage());
                }
            }
        });
    }

    //参数中的final是外卖单，whoIsUsing是客户名，activityIds是总共点了外卖的份数，handler是开邮箱的钥匙
    static public void getActivitysByActivityIds(final int whoIsUsing, final ArrayList<String> activityIds, final Handler handler){
        alAD1=new ArrayList<>();
        for(int i=0;i<activityIds.size();i++) {
            BmobQuery<ActivityData> bmobQuery = new BmobQuery<ActivityData>();
            bmobQuery.getObject(activityIds.get(i), new QueryListener<ActivityData>() {
                @Override
                //收到一份外卖时
                public void done(ActivityData object, BmobException e) {
                    if (e == null) {
                        //外卖单上写着1号
                        if(whoIsUsing==1)
                            //放1号邮箱
                            alAD1add(object);
                        Log.e("database", "一份外卖到了"+object.getObjectId()+",ArrayList大小："+alAD1.size());
                        //1号邮箱满18份
                        if (alAD1.size()==activityIds.size()){
                            //铃响，拿外卖
                            handler.sendMessage(somethingIntoMessage(alAD1,"alAD1",6));
                        }
                    } else {
                        Log.e("database", "一份外卖失败：" + e.getMessage());
                    }
                }
            });
        }
    }
    static private synchronized void alAD1add(ActivityData activityData){
        alAD1.add(activityData);
    }

    //叫外卖小哥送餐具过来，whoIsUsing是客户名，userIdMaps是要了些什么餐具，handler是开邮箱的钥匙
    static public void getUsersByUserIds(final int whoIsUsing, final Map<String,User> userIdMaps, final Handler handler){
        mapUser1=new HashMap<>();
        mapUser2=new HashMap<>();
        mapUser3=new HashMap<>();
        for (String key : userIdMaps.keySet()) {
            BmobQuery<User> bmobQuery = new BmobQuery<User>();
            bmobQuery.getObject(key, new QueryListener<User>() {
                @Override
                //有一种餐具到了
                public void done(User object, BmobException e) {
                    if (e == null) {
                        Log.e("database", "一种餐具到了"+object.getObjectId());
                        //如果是1号客户
                        if(whoIsUsing==1) {
                            //放进一号邮箱
                            alUser1Add(object);
                            if(mapUser1.size()==userIdMaps.size()){
                                //取餐具
                                handler.sendMessage(somethingIntoMessage(mapUser1,"mapUser1",7));
                            }
                        }
                        if(whoIsUsing==2) {
                            //放进一号邮箱
                            alUser2Add(object);
                            if(mapUser2.size()==userIdMaps.size()){
                                //取餐具
                                handler.sendMessage(somethingIntoMessage(mapUser2,"mapUser2",10));
                            }
                        }
                        if(whoIsUsing==3) {
                            //放进3号邮箱
                            alUser3Add(object);
                            if(mapUser3.size()==userIdMaps.size()){
                                //取餐具
                                handler.sendMessage(somethingIntoMessage(mapUser3,"mapUser3",11));
                            }
                        }
                    } else {
                        Log.e("database", "一种餐具投递失败：" + e.getMessage());
                    }
                }
            });
        }
    }
    static private synchronized void alUser1Add(User user){
        mapUser1.put(user.getObjectId(),user);
    }
    static private synchronized void alUser2Add(User user){
        mapUser2.put(user.getObjectId(),user);
    }
    static private synchronized void alUser3Add(User user){
        mapUser3.put(user.getObjectId(),user);
    }

    static public void getActivityIdsByUserId(String userId, final Handler handler){
        BmobQuery<ActivityAndMember> bmobQuery = new BmobQuery<ActivityAndMember>();
        bmobQuery.addWhereEqualTo("memberId",userId);
        bmobQuery.findObjects(new FindListener<ActivityAndMember>() {
            @Override
            public void done(List<ActivityAndMember> object, BmobException e) {
                if(e==null){
                    Log.e("bmob","查询 活动成员 成功：共"+object.size()+"条数据。");
                    for (ActivityAndMember gameScore : object) {
                        Log.e("database","活动Id："+gameScore.getActivityId());
                    }
                    handler.sendMessage(AMLintoMessage(object));
                }else{
                    Log.e("bmob","活动成员 失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    static public void getFriendsIdsByUserId(String userId, final Handler handler){
        friendIds=new ArrayList<>();
        BmobQuery<Friend> bmobQuery = new BmobQuery<Friend>();
        bmobQuery.addWhereEqualTo("userObjectId",userId);
        bmobQuery.addWhereEqualTo("isFriend",1);
        bmobQuery.findObjects(new FindListener<Friend>() {
            @Override
            public void done(List<Friend> object, BmobException e) {
                if(e==null){
                    Log.e("bmob","获取朋友们ids成功：共"+object.size()+"条数据。");
                    for (Friend gameScore : object) {
                        friendIds.add(gameScore.getFriendObjectId());
                        Log.e("database","朋友Id："+gameScore.getFriendObjectId());
                    }
                    handler.sendMessage(somethingIntoMessage(friendIds,"friendIds",8));
                }else{
                    Log.e("bmob","获取朋友们ids 失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    static public void getFriendsByUserId(String userId, final Handler handler){
        BmobQuery<Friend> bmobQuery = new BmobQuery<Friend>();
        bmobQuery.addWhereEqualTo("userObjectId",userId);
        bmobQuery.findObjects(new FindListener<Friend>() {
            @Override
            public void done(List<Friend> object, BmobException e) {
                if(e==null){
                    Log.e("bmob","获取朋友们成功：共"+object.size()+"条数据。");
                    handler.sendMessage(somethingIntoMessage(object,"friends",1));
                }else{
                    Log.e("bmob","获取朋友们失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    static public void getADbyFriendsIds(final List<String> alFriendId, final Handler handler){
        for(int i=0;i<alFriendId.size();i++){
            Log.e("database","friendId:"+alFriendId.get(i));
        }
        friendReleaseActivitys=new ArrayList<>();
        //让商家把每一种外卖都送过来
        BmobQuery<ActivityData> bmobQuery = new BmobQuery<ActivityData>();
        bmobQuery.findObjects(new FindListener<ActivityData>() {
            @Override
            public void done(List<ActivityData> object, BmobException e) {
                if(e==null){
                    String creatorId;
                    boolean flag;
                    Log.e("bmob","每一种外卖都到了：共"+object.size()+"条数据。");
                    Log.e("database","alFriend.size:"+alFriendId.size());
                    //每送来一份，外卖小哥过一遍，看看有没有朋友名字，有就放入
                    for (ActivityData gameScore : object) {
                        flag=false;
                        creatorId=gameScore.getCreatorId();
                        Log.e("database","creatorId:"+creatorId);
                        for(int j=0;j<alFriendId.size();j++)
                            if(creatorId.equals(alFriendId.get(j))){
                                flag=true;
                                Log.e("database","if true");
                                break;
                            }
                        checkAndAdd(flag,gameScore);
                        Log.e("database","通过朋友ID对应的外卖Id："+gameScore.getCreatorId());
                    }
                    //最后一种外卖都送完了，取外卖
                    for(int i=0;i<friendReleaseActivitys.size();i++){
                        Log.e("database","活动id:"+friendReleaseActivitys.get(i).getObjectId());
                    }
                    handler.sendMessage(somethingIntoMessage(friendReleaseActivitys,"friendReleaseActivities",9));
                }else{
                    Log.e("bmob","有一种外卖 失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }
    private static void checkAndAdd(boolean flag,ActivityData gameScore){
        if(flag)friendReleaseActivitys.add(gameScore);
    }

    static public void updateAAM(ActivityAndMember activityAndMember, final Handler handler){
        activityAndMember.update(activityAndMember.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Log.e("database","更新 活动和成员关系表 成功");
                    handler.sendEmptyMessage(4);
                }else{
                    Log.e("database","更新 活动和成员关系表 失败：" + e.getMessage());
                }
            }
        });
    }

    static public void addAAM(String activityId, String userId, final Handler handler){
        ActivityAndMember activityAndMember=new ActivityAndMember();
        activityAndMember.setMemberId(userId);
        activityAndMember.setActivityId(activityId);
        activityAndMember.setSignInTimes(0);
        activityAndMember.setSignInFlag(0);
        activityAndMember.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if(e==null){
                    Log.e("database","添加数据成功，返回objectId为："+objectId);
                    handler.sendEmptyMessage(7);
                }else{
                    Log.e("database","创建数据失败：" + e.getMessage());
                }
            }
        });
    }

    static public void addAD(ActivityData activityData,final Handler handler){
        activityData.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if(e==null){
                    Log.e("database","添加AD成功，返回objectId为："+objectId);
                    Message message=new Message();
                    Bundle bundle=new Bundle();
                    bundle.putString("objectId",objectId);
                    message.setData(bundle);
                    message.what=2;
                    handler.sendMessage(message);
                }else{
                    Log.e("database","创建数据失败：" + e.getMessage());
                }
            }
        });
    }

    public static void upload_picture(String picPath, final Handler handler){
        bmobFile = new BmobFile(new File(picPath));
        bmobFile.uploadblock(new UploadFileListener() {

            @Override
            public void done(BmobException e) {
                if(e==null){
                    //bmobFile.getFileUrl()--返回的上传文件的完整地址
                    Log.e("database","上传文件成功" );
                    Message message=new Message();
                    Bundle bundle=new Bundle();
                    bundle.putString("url",bmobFile.getFileUrl());
                    message.what=1;
                    message.setData(bundle);
                    handler.sendMessage(message);
                }else{
                    Log.e("database","上传文件失败：" + e.getMessage());
                }

            }

            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
            }
        });
    }
}
