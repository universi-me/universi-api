package me.universi.image.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import me.universi.api.interfaces.EntityService;
import me.universi.image.entities.ImageData;
import me.universi.image.entities.ImageMetadata;
import me.universi.image.enums.ImageStoreLocation;
import me.universi.image.repositories.ImageDataRepository;
import me.universi.image.repositories.ImageMetadataRepository;
import me.universi.profile.services.ProfileService;

@Service
public class ImageDataService extends EntityService<ImageData> {
    private final ImageMetadataRepository imageMetadataRepository;
    private final ImageDataRepository imageDataRepository;

    public ImageDataService( ImageMetadataRepository imageMetadataRepository, ImageDataRepository imageDataRepository ) {
        this.imageMetadataRepository = imageMetadataRepository;
        this.imageDataRepository = imageDataRepository;

        this.entityName = "Imagem";
    }

    @Override
    public Optional<ImageData> find( UUID id ) {
        return imageDataRepository.findById( id );
    }

    @Override
    public List<ImageData> findAll() {
        return imageDataRepository.findAll();
    }

    public Optional<Resource> findByFilename( String filename ) {
        var metadata = imageDataRepository.findByMetadataFilename( filename );
        if ( metadata.isEmpty() )
            return Optional.empty();

        return Optional.of( new ByteArrayResource(
            findOrThrow( metadata.get().getMetadata().getId() ).getData()
        ) );
    }

    public ImageMetadata saveNewImage( MultipartFile image ) {
        var imageBytes = ImageMetadataService.getInstance().checkImageSize(image);
        var filename = ImageMetadataService.generateFilename( image.getOriginalFilename() );

        var metadata = imageMetadataRepository.saveAndFlush(
            new ImageMetadata(
                filename,
                image.getContentType(),
                ProfileService.getInstance().getProfileInSession(),
                ImageStoreLocation.DATABASE,
                new Date()
            )
        );

        imageDataRepository.saveAndFlush(
            new ImageData( imageBytes, metadata )
        );

        return metadata;
    }

    @Override
    public boolean hasPermissionToEdit( ImageData imageData ) {
        return ProfileService.getInstance().isSessionOfProfile( imageData.getMetadata().getProfile() );
    }

    @Override
    public boolean hasPermissionToDelete( ImageData imageData ) {
        return hasPermissionToEdit( imageData );
    }
}
