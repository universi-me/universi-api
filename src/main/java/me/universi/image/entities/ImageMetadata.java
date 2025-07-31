package me.universi.image.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import me.universi.util.HibernateUtil;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import me.universi.image.enums.ImageStoreLocation;
import me.universi.image.services.ImageMetadataService;
import me.universi.profile.entities.Profile;

@Entity( name = "ImageMetadata" )
@Table( name = "image_metadata", schema = "image" )
@SQLDelete( sql = "UPDATE image.image_metadata SET deleted = TRUE WHERE id=?" )
@SQLRestriction( "NOT deleted" )
@JsonSerialize(using = ImageMetadataSerializer.class)
public class ImageMetadata implements Serializable {
    @Id
    @GeneratedValue( strategy = GenerationType.UUID )
    @Column( name = "id" )
    @NotNull
    private UUID id;

    @NotNull
    @Column( name = "filename" )
    private String filename;

    @NotNull
    @Column( name = "content_type" )
    private String contentType;

    @Nullable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn( name = "profile_id" )
    @NotFound( action = NotFoundAction.IGNORE )
    private Profile profile;

    @NotNull
    @Column( name = "stored_at" )
    @Enumerated(EnumType.STRING)
    private ImageStoreLocation imageStore;

    @JsonIgnore
    @Column(name = "public")
    private boolean isPublic = Boolean.FALSE;

    @NotNull
    @CreationTimestamp
    @Temporal( TemporalType.TIMESTAMP )
    @Column( name = "created_at" )
    private Date createdAt;

    public ImageMetadata() {}

    public ImageMetadata( @NotNull String filename, @NotNull String contentType, @NotNull Profile profile, ImageStoreLocation imageStore, Boolean isPublic, Date createdAt ) {
        this.filename = filename;
        this.contentType = contentType;
        this.profile = profile;
        this.imageStore = imageStore;
        this.isPublic = isPublic;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt( Date createdAt ) { this.createdAt = createdAt; }

    public boolean isPublic() { return isPublic; }
    public void setPublic( boolean isPublic ) { this.isPublic = isPublic; }

    public Profile getProfile() { return HibernateUtil.resolveLazyHibernateObject(profile); }

    public ImageStoreLocation getImageStore() { return imageStore; }
    public void setImageStore(ImageStoreLocation imageStore) { this.imageStore = imageStore; }
}

class ImageMetadataSerializer extends JsonSerializer<ImageMetadata> {
    @Override
    public void serialize( ImageMetadata value, JsonGenerator gen, SerializerProvider serializers ) throws IOException {
            gen.writeString( ImageMetadataService.getInstance().getUri( value ).toString() );
    }
}
