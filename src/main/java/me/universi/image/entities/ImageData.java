package me.universi.image.entities;


import jakarta.persistence.*;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import me.universi.util.HibernateUtil;

@Entity( name = "ImageData" )
@Table( name = "image_data", schema = "image" )
public class ImageData {
    @Id
    @Column( name = "metadata_id" )
    private UUID metadataId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn( name = "metadata_id", referencedColumnName = "id" )
    @NotNull
    private ImageMetadata metadata;

    @Column(name = "data", columnDefinition="BYTEA")
    private byte[] data;

    public ImageData() {}

    public ImageData( byte[] data, ImageMetadata metadata ) {
        this.data = data;
        this.metadata = metadata;
    }

    public ImageMetadata getMetadata() { return HibernateUtil.resolveLazyHibernateObject(metadata); }

    public byte[] getData() { return data; }
    public void setData(byte[] data) { this.data = data; }

    @Transient
    public long length() {
        return this.data.length;
    }
}
