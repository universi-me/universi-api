package me.universi.link.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import me.universi.link.enums.TypeLink;
import me.universi.profile.entities.Profile;

import jakarta.persistence.*;

@Entity(name = "link")
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "link_generator")
    @SequenceGenerator(name = "link_generator", sequenceName = "link_sequence", allocationSize = 1)
    @Column(name = "id_link")
    private Long id;
    @Column(name = "type_link")
    @Enumerated(EnumType.STRING)
    private TypeLink typeLink;
    @Column(name = "url")
    private String url;

    @Column(name = "name")
    private String name;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_profile")
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

    public Long getId() {
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
