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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private LinearLayout default_show;

    private EditText search_edit_input;
    private ImageView search_iv_delete;
    private ListViewForScrollView search_lv_tips;

    String[] mFrom;
    int[] mTo;

    private ListViewForScrollView recommendedGroupListView, myGroupListView, joinedGroupListView;
    private RelativeLayout zeroGroup;
    private Button moreRecommendedGroup;

    private List<Group> groupList;
    private List<Map<String, Object>> allGroupList = new ArrayList<>();
    private Map<String, Bitmap> bitmapMap = new ArrayMap<>();

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFrom = new String[]{"index", "groupImage", "groupName", "groupDetail", "memberCount", "distance"};
        mTo = new int[]{R.id.index, R.id.groupImage, R.id.groupName, R.id.groupDetail, R.id.memberCount, R.id.distance};
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_group, container, false);
        default_show = (LinearLayout)view.findViewById(R.id.default_show);
        search_edit_input = (EditText)view.findViewById(R.id.search_edit_input);
        search_iv_delete = (ImageView)view.findViewById(R.id.search_iv_delete);
        search_lv_tips = (ListViewForScrollView)view.findViewById(R.id.search_lv_tips);
        recommendedGroupListView = (ListViewForScrollView)view.findViewById(R.id.recommendedGroupListView);
        myGroupListView = (ListViewForScrollView)view.findViewById(R.id.myGroupListView);
        joinedGroupListView = (ListViewForScrollView)view.findViewById(R.id.joinedGroupListView);
        zeroGroup = (RelativeLayout)view.findViewById(R.id.zero_group);
        moreRecommendedGroup = (Button)view.findViewById(R.id.moreRecommendedGroup);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        queryGroup();
    }

    private void queryGroup(){
        allGroupList.clear();
        bitmapMap.clear();

        new BmobQuery<Group>().setSQL("select * from Group order by memberCount desc")
                .doSQLQuery(new SQLQueryListener<Group>() {
                    @Override
                    public void done(BmobQueryResult<Group> bmobQueryResult, BmobException e) {
                        if (e == null){
                            groupList = bmobQueryResult.getResults();
                            handler.sendEmptyMessage(FINISH_LOADING_GROUP);
                        }else {
                            Toast.makeText(getActivity(), "load groups failed", Toast.LENGTH_SHORT).show();
                        }
                    }
        });
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
       super.onActivityCreated(savedInstanceState);

       search_edit_input.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {
           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {
               if (!"".equals(s.toString())) {
                   search_iv_delete.setVisibility(View.VISIBLE);
                   search_lv_tips.setVisibility(View.VISIBLE);
                   if (allGroupList.size() != 0){
                       List<Map<String, Object>> searchResultList = new ArrayList<>();
                       for (Map<String, Object> map:allGroupList){
                           if (map.get("groupName").toString().contains(s.toString())){
                               searchResultList.add(map);
                           }
                       }
                       search_lv_tips.setAdapter(new AdapterForShowGroup(getActivity(),
                               searchResultList, R.layout.item_group, mFrom, mTo));

                       view.setBackgroundColor(0xff888888);
                   }
                   default_show.setVisibility(View.GONE);
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

       moreRecommendedGroup.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(getActivity(), MoreRecommendedGroupActivity.class));
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
       search_lv_tips.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               groupItemClickedRespond(view);
           }
       });
    }

    private void groupItemClickedRespond(View view){
        if (allGroupList.size() == 0 || bitmapMap.size() != groupList.size()){
            Toast.makeText(getActivity(), "Please waiting for loading data", Toast.LENGTH_SHORT).show();
            return;
        }
        int index = Integer.parseInt(((TextView)view.findViewById(R.id.index)).getText().toString());
        Map<String, Object> itemMap = allGroupList.get(index);
        ArrayList<Map<String, Object>> newArrayList = new ArrayList<>();
        newArrayList.add(itemMap);
        Bundle data = new Bundle();
        data.putSerializable("groupDetail", newArrayList);
        startActivity(new Intent(getActivity(), GroupDetailActivity.class).putExtras(data));
    }

    private void queryBitmap(){
        for (Group group: groupList){
            final int objectIdLength = group.getObjectId().length();
            String saveName;
            File saveFile;
            String groupImageUri;
            synchronized (Object.class) {
                saveName = group.getObjectId() + ".png";
                groupImageUri = group.getGroupImageUri();
                saveFile = new File(Environment.getExternalStorageDirectory() + File.separator + "lovesportimage", saveName);
                if (groupImageUri == null || groupImageUri.trim().length() == 0) {
                    bitmapMap.put(saveFile.toString().substring(saveFile.toString().length() - objectIdLength - 4, saveFile.toString().length() - 4), null);
                    //if (bitmapMap.size() == groupList.size()) {
                        //handler.sendEmptyMessage(FINISH_LOADING_BITMAP);
                    //}
                    showResult();
                    continue;
                }
            }
            new BmobFile(saveName, "", groupImageUri).download(saveFile, new DownloadFileListener() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null){
                        synchronized (Object.class){
                            bitmapMap.put(s.substring(s.length() - objectIdLength - 4, s.length() - 4), BitmapFactory.decodeFile(s));
                            //if (bitmapMap.size() == groupList.size()){
                                //handler.sendEmptyMessage(FINISH_LOADING_BITMAP);
                            //}
                            showResult();
                        }
                    } else {
                        Toast.makeText(getActivity(), "loading group images failed", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onProgress(Integer integer, long l) {}
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

        recommendedGroupListView.setAdapter(new AdapterForShowGroup(getActivity(),
                recommendedGroupList2, R.layout.item_group, mFrom, mTo));
        myGroupListView.setAdapter(new AdapterForShowGroup(getActivity(),
                myGroupList, R.layout.item_group, mFrom, mTo));
        joinedGroupListView.setAdapter(new AdapterForShowGroup(getActivity(),
                joinedGroupList, R.layout.item_group, mFrom, mTo));

    }
}
