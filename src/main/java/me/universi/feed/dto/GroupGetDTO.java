package me.universi.feed.dto;

import me.universi.profile.entities.Profile;

public class GroupGetDTO {

    private String content;
    private Profile author;
    private String postId;
    private String groupId;

    public GroupGetDTO(String content, Profile author, String postId, String groupId) {
        this.content = content;
        this.author = author;
        this.postId = postId;
        this.groupId = groupId;
    }

    public GroupGetDTO() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Profile getAuthor() {
        return author;
    }

    public void setAuthor(Profile author) {
        this.author = author;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
