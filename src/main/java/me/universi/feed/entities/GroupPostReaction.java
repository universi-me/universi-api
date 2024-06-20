package me.universi.feed.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "group_post_reactions")
public class GroupPostReaction {

    @Id
    private String id;
    private String groupId;
    private String groupPostId;
    private String reaction;
    private String authorId;
    private boolean deleted;

    public GroupPostReaction() {
    }

    public GroupPostReaction(String groupId, String groupPostId, String reaction, String author, boolean deleted) {
        this.groupId = groupId;
        this.groupPostId = groupPostId;
        this.reaction = reaction;
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

    public String getGroupPostId() {
        return groupPostId;
    }

    public void setGroupPostId(String groupPostId) {
        this.groupPostId = groupPostId;
    }

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
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