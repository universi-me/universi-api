package me.universi.image.controller;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import me.universi.api.exceptions.UniversiServerException;
import me.universi.image.entities.ImageMetadata;
import me.universi.image.enums.ImageStoreLocation;
import me.universi.image.services.ImageDataService;
import me.universi.image.services.ImageMetadataService;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Nullable;


@RestController
@RequestMapping( ImageMetadataController.ENTITY_PATH )
public class ImageMetadataController {
    public static final String ENTITY_PATH = "/img";
    public static final String FILESYSTEM_PATH = "/imagem";
    public static final String DATABASE_PATH = "/store";
    public static final String MINIO_PATH = "/minio";

    private final ImageMetadataService imageMetadataService;
    private final ImageDataService databaseImageService;

    public ImageMetadataController(ImageMetadataService imageMetadataService, ImageDataService databaseImageService) {
        this.imageMetadataService = imageMetadataService;
        this.databaseImageService = databaseImageService;
    }

    @PostMapping( path = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Void> upload( @RequestParam("image") MultipartFile image ) {
        var imageMetadata = imageMetadataService.saveImageFromMultipartFile( image );
        var link = imageMetadataService.getUri( imageMetadata );

        return ResponseEntity.created( link ).build();
    }

    @GetMapping( path = "/{imageId}" )
    public ResponseEntity<Resource> getImageFromMetadataId( @PathVariable UUID imageId ) {
        return makeResponseFromMetadata( imageMetadataService.findOrThrow( imageId ) );
    }

    // get image from minIO
    @GetMapping( path = MINIO_PATH + "/{filename}" )
    @Cacheable("img")
    public ResponseEntity<Resource> getImageFromMinio( @PathVariable String filename ) {
        return makeResponseFromMetadata(
            imageMetadataService.findByFilenameOrThrow( filename, ImageStoreLocation.MINIO )
        );
    }

    // get image from filesystem
    @GetMapping( path = FILESYSTEM_PATH + "/{filename}" )
    @Cacheable("img")
    public ResponseEntity<Resource> getImageFromFilesystem( @PathVariable String filename ) {
        return makeResponseFromMetadata(
            imageMetadataService.findByFilenameOrThrow( filename, ImageStoreLocation.FILESYSTEM )
        );
    }

    // get image from database
    @GetMapping( path = DATABASE_PATH + "/{imageId}" )
    @Cacheable("img")
    public ResponseEntity<Resource> getImageFromDatabase( @PathVariable UUID imageId ) {
        return makeResponseFromMetadata(
            databaseImageService.findOrThrow( imageId ).getMetadata()
        );
    }

    public static ResponseEntity<Resource> redirectToImage( @Nullable ImageMetadata image ) {
        if ( image == null )
            return ResponseEntity.notFound().build();

        var imageMetadataService = ImageMetadataService.getInstance();
        return ResponseEntity
            .status( HttpStatus.FOUND )
            .location( imageMetadataService.getUri( image, true ) )
            .build();
    }

    private ResponseEntity<Resource> makeResponseFromMetadata( ImageMetadata metadata ) {
        if ( metadata.getImageStore() == ImageStoreLocation.EXTERNAL )
            return ResponseEntity
                .status( HttpStatus.FOUND )
                .location( imageMetadataService.getUri( metadata ) )
                .build();

        var resource = imageMetadataService.resource( metadata );
        long contentLength;
        try {
            contentLength = resource.contentLength();
        } catch ( IOException e ) {
            throw new UniversiServerException( "Não foi possível recuperar a imagem: " + e.getMessage() );
        }

        return ResponseEntity
            .ok()
            .contentLength( contentLength )
            .contentType( MediaType.parseMediaType( metadata.getContentType() ) )
            .cacheControl( CacheControl.maxAge( 365, TimeUnit.DAYS ) )
            .body( resource );
    }
}
