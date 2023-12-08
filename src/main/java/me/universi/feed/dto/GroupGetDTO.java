package me.universi.feed.dto;

import me.universi.profile.entities.Profile;

public class GroupGetDTO {

    private String content;
    private Profile author;

    public GroupGetDTO() {
    }

    public GroupGetDTO(String content, Profile author) {
        this.content = content;
        this.author = author;
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
}
