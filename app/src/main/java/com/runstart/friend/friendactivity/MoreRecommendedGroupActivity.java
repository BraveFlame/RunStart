package com.runstart.friend.friendactivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
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

    private ScrollView view;
    private Button goBack;
    private ListViewForScrollView recommendedGroupListView;
    private LinearLayout default_show;
    private String[] mFrom;
    private int[] mTo;

    private EditText search_edit_input;
    private ImageView search_iv_delete;
    private ListViewForScrollView search_lv_tips;

    private List<Group> groupList;
    private Map<String, Bitmap> bitmapMap = new ArrayMap<>();
    private List<Map<String, Object>> recommendedGroupList = new ArrayList<>();

    private final int FINISH_LOADING_GROUP = 0x115;
    private final int FINISH_LOADING_BITMAP = 0x116;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == FINISH_LOADING_GROUP){
                queryBitmap();
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

        view = (ScrollView)findViewById(R.id.rootView);
        goBack = (Button)findViewById(R.id.goBack);
        default_show = (LinearLayout)findViewById(R.id.default_show);
        recommendedGroupListView = (ListViewForScrollView)findViewById(R.id.recommendedGroupListView);
        search_edit_input = (EditText)findViewById(R.id.search_edit_input);
        search_iv_delete = (ImageView)findViewById(R.id.search_iv_delete);
        search_lv_tips = (ListViewForScrollView)findViewById(R.id.search_lv_tips);

        mFrom = new String[]{"index", "groupImage", "groupName", "groupDetail", "memberCount", "distance"};
        mTo = new int[]{R.id.index, R.id.groupImage, R.id.groupName, R.id.groupDetail, R.id.memberCount, R.id.distance};

        queryGroup();

        init();

    }

    private void queryGroup(){new BmobQuery<Group>().setSQL("select * from Group where creatorId!=? order by memberCount desc")
            .setPreparedParams(new String[]{MyApplication.applicationMap.get("userObjectId")})
            .doSQLQuery(new SQLQueryListener<Group>() {
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

    private void init(){
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        search_edit_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!"".equals(s.toString())) {
                    search_iv_delete.setVisibility(View.VISIBLE);
                    search_lv_tips.setVisibility(View.VISIBLE);
                    default_show.setVisibility(View.GONE);
                    view.setBackgroundColor(0xff888888);

                    if (recommendedGroupList.size() != 0){
                        List<Map<String, Object>> searchResultList = new ArrayList<>();
                        for (Map<String, Object> map:recommendedGroupList){
                            if (map.get("groupName").toString().contains(s.toString())){
                                searchResultList.add(map);
                            }
                        }
                        search_lv_tips.setAdapter(new AdapterForShowGroup(MoreRecommendedGroupActivity.this,
                                searchResultList, R.layout.item_group, mFrom, mTo));
                    }
                } else {
                    search_iv_delete.setVisibility(View.GONE);
                    search_lv_tips.setVisibility(View.GONE);
                    view.setBackgroundColor(0xfff2f1f1);
                    default_show.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        search_iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_edit_input.setText("");
                search_iv_delete.setVisibility(View.GONE);
                default_show.setVisibility(View.VISIBLE);
            }
        });

        search_lv_tips.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                groupItemClickedRespond(view);
            }
        });
        recommendedGroupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                groupItemClickedRespond(view);
            }
        });
    }

    private void groupItemClickedRespond(View view){
        if (recommendedGroupList.size() == 0){
            Toast.makeText(this, "Please waiting for loading data", Toast.LENGTH_SHORT).show();
            return;
        }
        int index = Integer.parseInt(((TextView)view.findViewById(R.id.index)).getText().toString());
        Map<String, Object> itemMap = recommendedGroupList.get(index);
        ArrayList<Map<String, Object>> newArrayList = new ArrayList<>();
        newArrayList.add(itemMap);
        Bundle data = new Bundle();
        data.putSerializable("groupDetail", newArrayList);
        startActivity(new Intent(this, GroupDetailActivity.class).putExtras(data));
    }

    private void queryBitmap(){
        for (Group group: groupList){
            final int objectIdLength = group.getObjectId().length();
            synchronized (MoreRecommendedGroupActivity.class) {
                String saveName = group.getObjectId() + ".png";
                String groupImageUri = group.getGroupImageUri();
                File saveFile = new File(Environment.getExternalStorageDirectory() + File.separator + "lovesportimage", saveName);
                if (groupImageUri == null || groupImageUri.trim().length() == 0) {
                    synchronized (Object.class) {
                        bitmapMap.put(saveFile.toString().substring(saveFile.toString().length() - objectIdLength - 4, saveFile.toString().length() - 4), null);
                        if (bitmapMap.size() == groupList.size()) {
                            handler.sendEmptyMessage(FINISH_LOADING_BITMAP);
                        }
                    }
                    continue;
                }
                new BmobFile(saveName, "", groupImageUri).download(saveFile, new DownloadFileListener() {
                    @Override
                    public void done(String s, BmobException e) {
                        synchronized (MoreRecommendedGroupActivity.class) {
                            bitmapMap.put(s.substring(s.length() - objectIdLength - 4, s.length() - 4), BitmapFactory.decodeFile(s));
                            if (bitmapMap.size() == groupList.size()) {
                                handler.sendEmptyMessage(FINISH_LOADING_BITMAP);
                            }
                        }
                    }

                    @Override
                    public void onProgress(Integer integer, long l) {
                    }
                });
            }
        }

    }

    private void showResult(){
        int index = 0;
        for (Group group:groupList){
            Map<String, Object> map = new HashMap<>();
            map.put("index", index++);
            map.put("objectId", group.getObjectId());
            map.put("groupImage", bitmapMap.get(group.getObjectId()));
            map.put("groupName", group.getGroupName());
            String groupDetail = group.getGroupDetail();
            if (groupDetail.length() > 43){
                groupDetail = groupDetail.substring(0, 40) + "...";
            }
            map.put("groupDetail", groupDetail);
            map.put("memberCount", group.getMemberObjectIdList().size() + " people");
            map.put("memberObjectIdList", group.getMemberObjectIdList());
            map.put("creatorId", group.getCreatorId());
            map.put("distance", group.getDistance() + "km");

            if (! group.getMemberObjectIdList().contains(MyApplication.applicationMap.get("userObjectId"))){
                recommendedGroupList.add(map);
            }
        }
        recommendedGroupListView.setAdapter(new AdapterForShowGroup(this,
                recommendedGroupList, R.layout.item_group, mFrom, mTo));
    }
}
