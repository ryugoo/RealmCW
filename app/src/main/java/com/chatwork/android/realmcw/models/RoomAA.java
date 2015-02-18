package com.chatwork.android.realmcw.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Rooms")
public class RoomAA extends Model {
    @Column(name = "room_id", index = true, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long roomId;
    @Column(name = "name")
    public String name;
    @Column(name = "type")
    public String type;
    @Column(name = "role")
    public String role;
    @Column(name = "sticky")
    public boolean sticky;
    @Column(name = "unread_num")
    public long unreadNum;
    @Column(name = "mention_num")
    public long mentionNum;
    @Column(name = "mytask_num")
    public long mytaskNum;
    @Column(name = "message_num")
    public long messageNum;
    @Column(name = "file_num")
    public long fileNum;
    @Column(name = "task_num")
    public long taskNum;
    @Column(name = "icon_path")
    public String iconPath;
    @Column(name = "last_update_time")
    public long lastUpdateTime;

    public RoomAA() {
        super();
    }

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
