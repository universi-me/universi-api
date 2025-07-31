package me.universi.capacity.entidades;

import jakarta.persistence.*;
import java.util.UUID;

import me.universi.util.HibernateUtil;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.validation.constraints.NotNull;

@Entity( name = "FolderContents" )
@Table(
    name = "folder_contents",
    schema = "capacity",
    uniqueConstraints = {
        // No repeated content
        @UniqueConstraint( columnNames = { "contents_id", "folders_id" } ),
        // Unique position
        @UniqueConstraint( columnNames = { "contents_id", "folders_id", "order_num" } ),
    }
)
@SQLRestriction( "NOT deleted" )
@SQLDelete( sql = "UPDATE capacity.folder_contents SET deleted = TRUE WHERE id = ?" )
public class FolderContents {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn( name = "contents_id", nullable = false )
    private Content content;

    @ManyToOne(fetch = FetchType.LAZY)
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

    public Content getContent() { return HibernateUtil.resolveLazyHibernateObject(content); }
    public void setContent(Content content) { this.content = content; }

    public Folder getFolder() { return HibernateUtil.resolveLazyHibernateObject(folder); }
    public void setFolder(Folder folder) { this.folder = folder; }

    public Integer getOrderNum() { return orderNum; }
    public void setOrderNum(Integer orderNum) { this.orderNum = orderNum; }

    public Boolean getDeleted() { return deleted; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }
}
