package me.universi.feed.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "group_posts")
public class GroupPost {

    @Id
    private String id;
    private String groupId;
    private String content;
    private String authorId;
    private boolean deleted;

    public GroupPost() {
    }

    public GroupPost(String groupId, String content, String author, boolean deleted) {
        this.groupId = groupId;
        this.content = content;
        this.authorId = author;
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

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}