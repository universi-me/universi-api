package me.universi.capacity.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import me.universi.capacity.entidades.Content;
import me.universi.capacity.enums.ContentStatusType;
import me.universi.capacity.service.ContentService;
import me.universi.profile.entities.Profile;

public class WatchProfileProgressDTO {
    @JsonIgnore
    private Profile profile;
    private Content content;

    public WatchProfileProgressDTO(Profile profile, Content content) {
        this.profile = profile;
        this.content = content;
    }

    public Profile getProfile() {
        return profile;
    }

    public Content getContent() {
        return content;
    }

    public ContentStatusType getStatus() {
        return ContentService.getInstance().getProfileProgress(content, profile);
    }
}
