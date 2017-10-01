package com.runstart.BmobBean;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by user on 17-9-21.
 */
public class Group extends BmobObject {
    private String creatorId;
    private String groupImageUri;
    private String groupName;
    private String groupDetail;
    private int memberCount;
    private int distance;
    private List<String> menberObjectIdList;

    public Group(){}
    public Group(String creatorId, String groupImageUri, int memberCount, int distance,
                 String groupName, String groupDetail, List<String> menberObjectIdList) {
        this.creatorId = creatorId;
        this.groupImageUri = groupImageUri;
        this.memberCount = memberCount;
        this.distance = distance;
        this.groupName = groupName;
        this.groupDetail = groupDetail;
        this.menberObjectIdList = menberObjectIdList;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getGroupImageUri() {
        return groupImageUri;
    }

    public void setGroupImageUri(String groupImageUri) {
        this.groupImageUri = groupImageUri;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDetail() {
        return groupDetail;
    }

    public void setGroupDetail(String groupDetail) {
        this.groupDetail = groupDetail;
    }

    public List<String> getMemberObjectIdList() {
        return menberObjectIdList;
    }

    public void setMemberObjectIdList(List<String> menberObjectIdList) {
        this.menberObjectIdList = menberObjectIdList;
    }

    @Override
    public String toString() {
        return "Group{" +
                ", creatorId=" + creatorId +
                ", groupImageUri=" + groupImageUri +
                ", memberCount=" + memberCount +
                ", distance=" + distance +
                ", groupName='" + groupName + '\'' +
                ", groupDetail='" + groupDetail + '\'' +
                ", memberObjectIdList='" + menberObjectIdList + '\'' +
                '}';
    }
}
