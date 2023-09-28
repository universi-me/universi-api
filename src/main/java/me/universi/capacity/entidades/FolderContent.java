package me.universi.capacity.entidades;

import java.util.Date;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import jakarta.validation.constraints.NotNull;

@Entity(name = "folder_content")
public class FolderContent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "content", nullable = false)
    @ManyToOne
    @NotNull
    private Content content;

    @Column(name = "folder", nullable = false)
    @ManyToOne
    @NotNull
    private Folder folder;

    @Column(name = "previous_content", unique = true, nullable = true)
    @OneToOne
    private FolderContent previousContent;

    @Column(name = "created_at", nullable = false)
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    public FolderContent() { }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    public FolderContent getPreviousContent() {
        return previousContent;
    }

    public void setPreviousContent(FolderContent previousContent) {
        this.previousContent = previousContent;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
