package me.universi.feed.dto;


import me.universi.profile.entities.Profile;

public class GroupPostCommentDTO {

    private String id;

    private String content;

    private String authorId;

    private Profile author;


    public GroupPostCommentDTO() {
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

    public String getAuthorId(){
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public Profile getAuthor() {
        return author;
    }

    public void setAuthor(Profile author) {
        this.author = author;
    }
}