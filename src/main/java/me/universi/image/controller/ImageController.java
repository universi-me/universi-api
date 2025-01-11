package me.universi.image.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import me.universi.api.exceptions.UniversiServerException;
import me.universi.image.services.ImageService;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping( ImageController.ENTITY_PATH )
public class ImageController {
    public static final String ENTITY_PATH = "/img";
    public static final String FILESYSTEM_PATH = "/imagem";
    public static final String DATABASE_PATH = "/store";
    public static final String MINIO_PATH = "/minio";

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping( path = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Void> upload( @RequestParam("image") MultipartFile image ) {
        var link = imageService.saveImageFromMultipartFile( image );

        try {
            return ResponseEntity.created( new URI( link ) ).build();
        } catch ( URISyntaxException e ) {
            throw new UniversiServerException( e );
        }
    }

    // get image from minIO
    @GetMapping( path = MINIO_PATH + "/{imageId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE )
    @Cacheable("img")
    public ResponseEntity<Resource> getImageFromMinio( @PathVariable String imageId ) {
        var imageBytes = imageService.findByMinioId( imageId );
        return makeResponseFromBytes( imageBytes );
    }

    // get image from filesystem
    @GetMapping( path = FILESYSTEM_PATH + "/{image}.jpg", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE )
    @Cacheable("img")
    public ResponseEntity<Resource> getImageFromFilesystem(@PathVariable("image") String nameOfImage) {
        var filename = nameOfImage.replaceAll( "[^a-f0-9]", "" );
        if ( filename.contains( ".." ) || filename.contains( "/" ) || filename.contains( "\\" ) )
            throw imageService.makeNotFoundException( "nome", nameOfImage );

        var imageResource = imageService.getImageFromFilesystem( filename );
        if ( imageResource.isEmpty() )
            throw imageService.makeNotFoundException( "nome", nameOfImage );

        return makeResponseFromResource( imageResource.get() );
    }

    // get image from database
    @GetMapping( path = DATABASE_PATH + "/{imageId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE )
    @Cacheable("img")
    public ResponseEntity<Resource> getImageFromDatabase( @PathVariable UUID imageId ) {
        var img = imageService.findOrThrow( imageId );
        var resource = new ByteArrayResource( img.getData() );

        return makeResponseFromResource( resource );
    }

    private ResponseEntity<Resource> makeResponseFromBytes( byte[] bytes ) {
        return makeResponseFromResource( new ByteArrayResource( bytes ) );
    }

    private ResponseEntity<Resource> makeResponseFromResource( Resource resource ) {
        long contentLength;
        try {
            contentLength = resource.contentLength();
        } catch ( IOException e ) {
            throw new UniversiServerException( e );
        }

        return ResponseEntity
            .ok()
            .contentLength( contentLength )
            .contentType( MediaType.IMAGE_JPEG )
            .cacheControl( CacheControl.maxAge( 365, TimeUnit.DAYS ) )
            .body( resource );
    }
}
