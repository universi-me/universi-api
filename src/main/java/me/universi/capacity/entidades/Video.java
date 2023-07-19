package me.universi.capacity.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="videos")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "video_generator")
    @SequenceGenerator(name = "video_generator", sequenceName = "video_sequence", allocationSize = 1)
    private Long id;
    
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
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
}