package me.universi.feed.dto;



public class GroupPostDTO {

    private String content;

    private String authorId;


    public GroupPostDTO() {
    }

    public GroupPostDTO(String content, String authorId) {
        this.content = content;
        this.authorId = authorId;
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
}