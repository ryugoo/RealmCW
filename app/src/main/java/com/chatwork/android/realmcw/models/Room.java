package com.chatwork.android.realmcw.models;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class Room extends RealmObject {
    @PrimaryKey
    private long roomId;

    @Index
    private String name;

    private String type;
    private String role;
    private boolean sticky;
    private long unreadNum;
    private long mentionNum;
    private long mytaskNum;
    private long messageNum;
    private long fileNum;
    private long taskNum;
    private String iconPath;
    private long lastUpdateTime;

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isSticky() {
        return sticky;
    }

    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }

    public long getUnreadNum() {
        return unreadNum;
    }

    public void setUnreadNum(long unreadNum) {
        this.unreadNum = unreadNum;
    }

    public long getMentionNum() {
        return mentionNum;
    }

    public void setMentionNum(long mentionNum) {
        this.mentionNum = mentionNum;
    }

    public long getMytaskNum() {
        return mytaskNum;
    }

    public void setMytaskNum(long mytaskNum) {
        this.mytaskNum = mytaskNum;
    }

    public long getMessageNum() {
        return messageNum;
    }

    public void setMessageNum(long messageNum) {
        this.messageNum = messageNum;
    }

    public long getFileNum() {
        return fileNum;
    }

    public void setFileNum(long fileNum) {
        this.fileNum = fileNum;
    }

    public long getTaskNum() {
        return taskNum;
    }

    public void setTaskNum(long taskNum) {
        this.taskNum = taskNum;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
