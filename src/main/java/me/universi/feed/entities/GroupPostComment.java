package me.universi.feed.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "group_post_comments")
public class GroupPostComment {

    @Id
    private String id;
    private String groupPostId;
    private String content;
    private String authorId;
    private boolean deleted;

    public GroupPostComment() {
    }

    public GroupPostComment(String groupPostId, String content, String author, boolean deleted) {
        this.groupPostId = groupPostId;
        this.content = content;
        this.authorId = author;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getGroupPostId() {
        return groupPostId;
    }

    public void setGroupPostId(String groupPostId) {
        this.groupPostId = groupPostId;
    }
}