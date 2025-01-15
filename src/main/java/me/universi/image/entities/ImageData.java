package me.universi.image.entities;


import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;

@Entity( name = "ImageData" )
@Table( name = "image_data", schema = "image" )
public class ImageData {
    @Id
    @Column( name = "metadata_id" )
    private UUID metadataId;

    @OneToOne
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

    public ImageMetadata getMetadata() { return metadata; }

    public byte[] getData() { return data; }
    public void setData(byte[] data) { this.data = data; }

    @Transient
    public long length() {
        return this.data.length;
    }
}
