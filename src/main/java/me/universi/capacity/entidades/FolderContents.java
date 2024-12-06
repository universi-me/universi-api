package me.universi.capacity.entidades;

import java.util.UUID;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;

@Entity( name = "FolderContents" )
@Table(
    name = "folder_contents",
    uniqueConstraints = {
        // No repeated content
        @UniqueConstraint( columnNames = { "contents_id", "folders_id" } ),
        // Unique position
        @UniqueConstraint( columnNames = { "contents_id", "folders_id", "order_num" } ),
    }
)
@SQLRestriction( "NOT deleted" )
@SQLDelete( sql = "UPDATE folder_contents SET deleted = TRUE WHERE id = ?" )
public class FolderContents {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @ManyToOne()
    @JoinColumn( name = "contents_id", nullable = false )
    private Content content;

    @ManyToOne()
    @JoinColumn( name = "folders_id", nullable = false )
    private Folder folder;

    @Column( name = "order_num", nullable = false )
    @NotNull
    Integer orderNum;

    @Column( name = "deleted", nullable = false )
    @NotNull
    boolean deleted;

    public FolderContents() { }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Content getContent() { return content; }
    public void setContent(Content content) { this.content = content; }

    public Folder getFolder() { return folder; }
    public void setFolder(Folder folder) { this.folder = folder; }

    public Integer getOrderNum() { return orderNum; }
    public void setOrderNum(Integer orderNum) { this.orderNum = orderNum; }

    public Boolean getDeleted() { return deleted; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }
}
