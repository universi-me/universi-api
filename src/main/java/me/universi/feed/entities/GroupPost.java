package me.universi.feed.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "group_posts")
public class GroupPost {

    @Id
    private String id;
    private String groupId;
    private String content;

    public GroupPost() {
    }

    public GroupPost(String groupId, String content) {
        this.groupId = groupId;
        this.content = content;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}