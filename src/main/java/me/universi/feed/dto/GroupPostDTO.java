package me.universi.feed.dto;

public class GroupPostDTO {

    private String content;


    public GroupPostDTO() {
    }

    public GroupPostDTO(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}