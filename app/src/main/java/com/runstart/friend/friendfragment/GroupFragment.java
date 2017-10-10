package com.runstart.friend.friendfragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.runstart.BmobBean.Group;
import com.runstart.R;
import com.runstart.friend.adapter.AdapterForShowGroup;
import com.runstart.friend.adapter.ListViewForScrollView;
import com.runstart.friend.friendactivity.GroupDetailActivity;
import com.runstart.friend.friendactivity.MoreRecommendedGroupActivity;
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

public class GroupFragment extends Fragment {

    private View view;

    private EditText searchGroupText;
    private ImageView searchIcon, backIcon;

    private ListViewForScrollView recommendedGroupListView, myGroupListView, joinedGroupListView;
    private RelativeLayout zeroGroup;
    private Button moreRecommendedGroup;

    private List<Group> groupList=new ArrayList<>();
    private List<Map<String, Object>> allGroupList = new ArrayList<>();
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_group, container, false);
        searchGroupText = (EditText)view.findViewById(R.id.searchGroupText);
        searchIcon = (ImageView)view.findViewById(R.id.searchIcon);
        backIcon = (ImageView)view.findViewById(R.id.backIcon);
        recommendedGroupListView = (ListViewForScrollView)view.findViewById(R.id.recommendedGroupListView);
        myGroupListView = (ListViewForScrollView)view.findViewById(R.id.myGroupListView);
        joinedGroupListView = (ListViewForScrollView)view.findViewById(R.id.joinedGroupListView);
        zeroGroup = (RelativeLayout)view.findViewById(R.id.zero_group);
        moreRecommendedGroup = (Button)view.findViewById(R.id.moreRecommendedGroup);
        return view;
    }

   @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
       super.onActivityCreated(savedInstanceState);

       String sql = "select * from Group order by memberCount desc";
       BmobQuery<Group> query = new BmobQuery<>();
       query.setSQL(sql);
       query.doSQLQuery(new SQLQueryListener<Group>() {
           @Override
           public void done(BmobQueryResult<Group> bmobQueryResult, BmobException e) {
               if (e == null){
                   groupList = bmobQueryResult.getResults();
                   if(groupList.size()>0)
                   handler.sendEmptyMessage(FINISH_LOADING_GROUP);
               }else {
                   Toast.makeText(getActivity(), "load groups failed", Toast.LENGTH_SHORT).show();
               }
           }
       });

       moreRecommendedGroup.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(getActivity(), MoreRecommendedGroupActivity.class));
           }
       });


       searchGroupText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
           @Override
           public void onFocusChange(View v, boolean hasFocus) {
               if (hasFocus){
                   searchIcon.setVisibility(View.GONE);
                   backIcon.setVisibility(View.VISIBLE);
               }else {
                   searchIcon.setVisibility(View.VISIBLE);
                   backIcon.setVisibility(View.GONE);
               }
           }
       });
       backIcon.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               backIcon.setVisibility(View.GONE);
               searchIcon.setVisibility(View.VISIBLE);
               backIcon.setFocusable(true);
           }
       });
       searchGroupText.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               searchGroupText.requestFocus();
           }
       });

       myGroupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               groupItemClickedRespond(view);
           }
       });
       joinedGroupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        int index = Integer.parseInt(((TextView)view.findViewById(R.id.index)).getText().toString());
        Map<String, Object> itemMap = allGroupList.get(index);
        ArrayList<Map<String, Object>> newArrayList = new ArrayList<>();
        newArrayList.add(itemMap);
        Bundle data = new Bundle();
        data.putSerializable("groupDetail", newArrayList);
        startActivity(new Intent(getActivity(), GroupDetailActivity.class).putExtras(data));
    }


    private void getBitmap(){
        for (Group group: groupList){
            final int objectIdLength = group.getObjectId().length();
            new BmobFile(group.getObjectId() + ".png", "", group.getGroupImageUri())
                    .download(new File(Environment.getExternalStorageDirectory() + File.separator + "lovesportimage",
                            group.getObjectId() + ".png"), new DownloadFileListener() {
                        @Override
                        public void done(String s, BmobException e) {
                            synchronized (GroupFragment.class){
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
        List<Map<String, Object>> myGroupList = new ArrayList<>();
        List<Map<String, Object>> joinedGroupList = new ArrayList<>();
        List<Map<String, Object>> recommendedGroupList2 = new ArrayList<>();
        int index = 0;
        for (Group group:groupList){
            Map<String, Object> map = new HashMap<>();
            map.put("index", index++);
            map.put("groupImage", bitmapMap.get(group.getObjectId()));
            map.put("groupName", group.getGroupName());
            String groupDetail = group.getGroupDetail();
            if (groupDetail.length() > 43){
                groupDetail = groupDetail.substring(0, 40) + "...";
            }
            map.put("groupDetail", groupDetail);
            map.put("memberCount", group.getMemberObjectIdList().size() + " people");
            map.put("distance", group.getDistance() + "km");
            map.put("memberObjectIdList", group.getMemberObjectIdList());
            map.put("creatorId", group.getCreatorId());
            map.put("objectId", group.getObjectId());

            allGroupList.add(map);

            if (MyApplication.applicationMap.get("userObjectId").equals(group.getCreatorId())){
                myGroupList.add(map);
            } else {
                List<String> memberObjectIdList = group.getMemberObjectIdList();
                boolean hasJoined = false;
                for (String memberObjectId: memberObjectIdList){
                    if (MyApplication.applicationMap.get("userObjectId").equals(memberObjectId)){
                        hasJoined = true;
                        break;
                    }
                }
                if (hasJoined){
                    joinedGroupList.add(map);
                }else {
                    if (recommendedGroupList2.size() < 2){
                        recommendedGroupList2.add(map);
                    }
                }
            }
        }

        if (myGroupList.size() == 0 && joinedGroupList.size() == 0){
            zeroGroup.setVisibility(View.VISIBLE);
        }else {
            zeroGroup.setVisibility(View.GONE);
        }

        String[] from = new String[]{"index", "groupImage", "groupName", "groupDetail", "memberCount", "distance"};
        int[] to = new int[]{R.id.index, R.id.groupImage, R.id.groupName, R.id.groupDetail, R.id.memberCount, R.id.distance};
        recommendedGroupListView.setAdapter(new AdapterForShowGroup(getActivity(),
                recommendedGroupList2, R.layout.item_group, from, to));
        myGroupListView.setAdapter(new AdapterForShowGroup(getActivity(),
                myGroupList, R.layout.item_group, from, to));
        joinedGroupListView.setAdapter(new AdapterForShowGroup(getActivity(),
                joinedGroupList, R.layout.item_group, from, to));
    }
}
