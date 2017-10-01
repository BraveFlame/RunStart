package com.runstart.friend.friendactivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.runstart.BmobBean.Group;
import com.runstart.R;
import com.runstart.friend.adapter.AdapterForShowGroup;
import com.runstart.friend.adapter.ListViewForScrollView;
import com.runstart.history.MyApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.SQLQueryListener;

public class MoreRecommendedGroupActivity extends AppCompatActivity {

    private Button goBack;
    private ListViewForScrollView recommendedGroupListView;

    private List<Group> groupList;
    private Map<String, Bitmap> bitmapMap = new ArrayMap<>();

    private final int FINISH_LOADING_GROUP = 0x115;
    private final int FINISH_LOADING_BITMAP = 0x116;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == FINISH_LOADING_GROUP){
                getBitmap();
            }
            if (msg.what == FINISH_LOADING_BITMAP){
                showResult();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_recommended_group);

        goBack = (Button)findViewById(R.id.goBack);
        recommendedGroupListView = (ListViewForScrollView)findViewById(R.id.recommendedGroupListView);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String sql = "select * from Group order by memberCount desc";
        BmobQuery<Group> query = new BmobQuery<>();
        query.setSQL(sql);
        query.doSQLQuery(new SQLQueryListener<Group>() {
            @Override
            public void done(BmobQueryResult<Group> bmobQueryResult, BmobException e) {
                if (e == null){
                    groupList = bmobQueryResult.getResults();
                    handler.sendEmptyMessage(FINISH_LOADING_GROUP);
                }else {
                    Toast.makeText(MoreRecommendedGroupActivity.this, "load groups failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getBitmap(){
        for (Group group: groupList){
            final int objectIdLength = group.getObjectId().length();
            new BmobFile(group.getObjectId() + ".png", "", group.getGroupImageUri())
                    .download(new File(Environment.getExternalStorageDirectory() + File.separator + "lovesportimage",
                            group.getObjectId() + ".png"), new DownloadFileListener() {
                        @Override
                        public void done(String s, BmobException e) {
                            synchronized (MoreRecommendedGroupActivity.class){
                                bitmapMap.put(s.substring(s.length() - objectIdLength - 4, s.length() - 4), BitmapFactory.decodeFile(s));
                            }
                            if (bitmapMap.size() == groupList.size()){
                                handler.sendEmptyMessage(FINISH_LOADING_BITMAP);
                            }
                        }
                        @Override
                        public void onProgress(Integer integer, long l) {
                        }
                    });
        }

    }

    private void showResult(){
        List<Map<String, Object>> recommendedGroupList = new ArrayList<>();
        for (Group group:groupList){
            Map<String, Object> map = new HashMap<>();
            map.put("groupImage", bitmapMap.get(group.getObjectId()));
            map.put("groupName", group.getGroupName());
            String groupDetail = group.getGroupDetail();
            if (groupDetail.length() > 43){
                groupDetail = groupDetail.substring(0, 40) + "...";
            }
            map.put("groupDetail", groupDetail);

            map.put("memberCount", group.getMemberCount() + " people");
            map.put("distance", group.getDistance() + "km");

            if (! MyApplication.applicationMap.get("userObjectId").equals(group.getCreatorId())){
                recommendedGroupList.add(map);
            }
        }

        String[] from = new String[]{"groupImage", "groupName", "groupDetail", "memberCount", "distance"};
        int[] to = new int[]{R.id.groupImage, R.id.groupName, R.id.groupDetail, R.id.memberCount, R.id.distance};
        recommendedGroupListView.setAdapter(new AdapterForShowGroup(this,
                recommendedGroupList, R.layout.item_group, from, to));
    }
}
