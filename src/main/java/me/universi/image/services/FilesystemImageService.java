package me.universi.image.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import me.universi.api.exceptions.UniversiServerException;
import me.universi.image.entities.ImageMetadata;
import me.universi.image.enums.ImageStoreLocation;
import me.universi.image.repositories.ImageMetadataRepository;
import me.universi.profile.services.ProfileService;

@Service
public class FilesystemImageService {
    private final ImageMetadataRepository imageMetadataRepository;

    @Value("${PATH_IMAGE_SAVE}")
    private String pathImageSave;


    public FilesystemImageService( ImageMetadataRepository imageMetadataRepository ) {
        this.imageMetadataRepository = imageMetadataRepository;
    }

    public Optional<Resource> findByFilename( String filename ) {
        File initialFile = new File( pathImageSave, filename );

        return initialFile.exists() && initialFile.isFile()
            ? Optional.of( new FileSystemResource( initialFile ) )
            : Optional.empty();
    }

    public ImageMetadata saveNewImage( MultipartFile image ) {
        var imageBytes = ImageMetadataService.getInstance().checkImageSize( image );
        var filename = ImageMetadataService.generateFilename( image.getOriginalFilename() );

        File imageDir = new File( pathImageSave );
        if ( !imageDir.exists() )
            imageDir.mkdirs();

        File file = Paths.get( imageDir.toString(), filename ).toFile();

        try {
            OutputStream os = new FileOutputStream(file);
            os.write( imageBytes );
            os.close();
        } catch ( IOException e ) {
            throw new UniversiServerException("Falha ao salvar imagem em disco.");
        }

        return imageMetadataRepository.saveAndFlush(
            new ImageMetadata(
                filename,
                image.getContentType(),
                ProfileService.getInstance().getProfileInSession(),
                ImageStoreLocation.FILESYSTEM,
                new Date()
            )
        );
    }
}
