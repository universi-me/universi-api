package me.universi.capacity.entidades;

import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.UUID;

@Entity(name="video")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    private UUID id;
    
    @Column(unique = true)
    @Size(max = 45)
    private String url;
    
    @Column(unique = true)
    @Size(max = 100)
    private String title;
    
    @Column
    @Size(max = 200)
    private String description;

    @Column
    private String category;

    @Column
    private String playlist;
    
    @Column
    @NotNull
    @Min(0)
    @Max(5)
    private Integer rating;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;
    
    // Construtores da Entidade Vídeo
    public Video() {
    }

    public Video(String url, String title, String description, String category, Integer rating, String playlist) {
        this.url = url;
        this.title = title;
        this.description = description;
        this.category = category;
        this.rating = rating;
        this.playlist = playlist;
    }

    // Getters e Setters da Entidade Vídeo
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getcategory() {
        return category;
    }
    

    public void setcategory(String category) {
        this.category = category;
    }

    public String getPlaylist() {
        return playlist;
    }

    public void setPLaylist(String playlist) {
        this.playlist = playlist;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}