package me.universi.link.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import me.universi.link.enums.TypeLink;
import me.universi.profile.entities.Profile;

import jakarta.persistence.*;

import java.util.UUID;

@Entity(name = "link")
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    private UUID id;
    @Column(name = "type_link")
    @Enumerated(EnumType.STRING)
    @NotNull
    private TypeLink typeLink;
    @Column(name = "url")
    private String url;

    @Column(name = "name")
    private String name;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    @NotNull
    private Profile profile;

    public Link(TypeLink typeLink, String url){
        this.typeLink = typeLink;
        this.url = url;
    }

    public Link() {}

    public TypeLink getTypeLink() {
        return typeLink;
    }

    public void setTypeLink(TypeLink typeLink) {
        this.typeLink = typeLink;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public UUID getId() {
        return id;
    }

    public Profile getPerfil() {
        return profile;
    }

    public void setPerfil(Profile profile) {
        this.profile = profile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
