package me.universi.image.services;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import me.universi.Sys;
import me.universi.api.exceptions.UniversiBadRequestException;
import me.universi.api.exceptions.UniversiPayloadTooLargeException;
import me.universi.api.exceptions.UniversiServerException;
import me.universi.api.interfaces.EntityService;
import me.universi.image.controller.ImageMetadataController;
import me.universi.image.entities.ImageMetadata;
import me.universi.image.enums.ImageStoreLocation;
import me.universi.minioConfig.MinioConfig;
import me.universi.profile.services.ProfileService;
import me.universi.util.CastingUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import me.universi.image.repositories.ImageMetadataRepository;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageMetadataService extends EntityService<ImageMetadata> {
    private final ImageMetadataRepository imageMetadataRepository;
    private final MinioImageService minioImageService;
    private final ImageDataService databaseImageService;
    private final FilesystemImageService filesystemImageService;
    private final ProfileService profileService;

    @Value("${SAVE_IMAGE_LOCAL}")
    public boolean saveOnFilesystem;

    @Value("${IMAGE_UPLOAD_LIMIT}")
    private int imageSizeLimitInGigabytes;

    @Value( "${server.servlet.context-path}" )
    private String contextPath;


    public ImageMetadataService(ImageMetadataRepository imageMetadataRepository, MinioImageService minioImageService, ImageDataService databaseImageService, FilesystemImageService filesystemImageService, ProfileService profileService) {
        this.imageMetadataRepository = imageMetadataRepository;
        this.minioImageService = minioImageService;
        this.databaseImageService = databaseImageService;
        this.filesystemImageService = filesystemImageService;
        this.profileService = profileService;

        this.entityName = "Imagem";
    }

    public static ImageMetadataService getInstance() {
        return Sys.context.getBean("imageMetadataService", ImageMetadataService.class);
    }

    @Override
    public Optional<ImageMetadata> find( UUID id ) {
        return imageMetadataRepository.findById( id );
    }

    public Optional<ImageMetadata> findByFilename( String filename, ImageStoreLocation storeType ) {
        return imageMetadataRepository.findFirstByFilenameAndImageStore( filename, storeType );
    }

    public ImageMetadata findByFilenameOrThrow( String filename, ImageStoreLocation storeType ) {
        return imageMetadataRepository.findFirstByFilenameAndImageStore( filename, storeType )
            .orElseThrow( () -> makeNotFoundException( "id", filename ));
    }

    @Override
    public List<ImageMetadata> findAll() {
        return imageMetadataRepository.findAll();
    }

    public ImageMetadata saveImageFromMultipartFile(MultipartFile image) {
        switch ( getImageStore() ) {
            case MINIO:
                return minioImageService.saveNewImage( image );
            case DATABASE:
                return databaseImageService.saveNewImage( image );
            case FILESYSTEM:
                return filesystemImageService.saveNewImage( image );
            default:
                throw new UniversiServerException( "Não foi possível salvar a imagem" );
        }
    }

    public Resource resource( ImageMetadata imageMetadata ) {
        switch ( imageMetadata.getImageStore() ) {
            case DATABASE:
                return resourceByIdOnDatabaseOrThrow( imageMetadata.getId() );
            case FILESYSTEM:
                return resourceByFilenameOnFilesystemOrThrow( imageMetadata.getFilename() );
            case MINIO:
                return resourceByFilenameOnMinioOrThrow( imageMetadata.getFilename() );
            default:
                throw makeNotFoundException( "id", imageMetadata.getFilename() );
        }
    }

    private Resource resourceByFilenameOnMinioOrThrow( String filename ) {
        return minioImageService.findByFilename( filename )
            .orElseThrow( () -> makeNotFoundException( "id", filename ) );
    }

    private Resource resourceByIdOnDatabaseOrThrow( UUID id ) {
        var data = databaseImageService.findOrThrow( id );
        return new ByteArrayResource( data.getData() );
    }

    private Resource resourceByFilenameOnFilesystemOrThrow( String filename ) {
        return filesystemImageService.findByFilename( filename )
            .orElseThrow( () -> makeNotFoundException( "id", filename ) );
    }

    public ImageMetadata saveExternalImage( String imageUrl ) {
        return imageMetadataRepository.saveAndFlush(
            new ImageMetadata(
                imageUrl,
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                profileService.getProfileInSession(),
                ImageStoreLocation.EXTERNAL,
                new Date()
            )
        );
    }

    public byte[] checkImageSize( MultipartFile image) throws UniversiPayloadTooLargeException, UniversiBadRequestException {
        byte[] imageBytes;
        try {
            imageBytes = image.getBytes();
        } catch (Exception e) {
            throw new UniversiBadRequestException( "Não foi possível processar a imagem" );
        }

        if ( imageBytes.length > 1024 * 1024 * imageSizeLimitInGigabytes ) {
            throw new UniversiPayloadTooLargeException( "Imagem muito grande." );
        }

        return imageBytes;
    }

    private ImageStoreLocation getImageStore() {
        if ( MinioConfig.isMinioEnabled() )
            return ImageStoreLocation.MINIO;

        else if ( saveOnFilesystem )
            return ImageStoreLocation.FILESYSTEM;

        return ImageStoreLocation.DATABASE;
    }

    @Override
    public boolean hasPermissionToEdit( ImageMetadata image ) {
        return profileService.isSessionOfProfile( image.getProfile() );
    }

    @Override
    public boolean hasPermissionToDelete(ImageMetadata image) {
        return hasPermissionToEdit( image );
    }

    public static String generateFilename( String originalFilename ) {
        var extension = "";
        var extensionStart = originalFilename.lastIndexOf( '.' );
        if ( extensionStart > 0 )
            extension = originalFilename.substring( extensionStart );

        return UUID.randomUUID().toString() + extension;
    }

    public URI getUri( ImageMetadata imageMetadata ) {
        return getUri( imageMetadata, false );
    }

    public URI getUri( ImageMetadata image, boolean addContextPath ) {
        String path = ( addContextPath ? contextPath : "" )
            + ImageMetadataController.ENTITY_PATH;

        switch ( image.getImageStore() ) {
            case MINIO:
                path += ImageMetadataController.MINIO_PATH + "/" + image.getFilename();
                break;
            case DATABASE:
                path += ImageMetadataController.DATABASE_PATH + "/" + image.getFilename();
                break;
            case FILESYSTEM:
                path += ImageMetadataController.FILESYSTEM_PATH + "/" + image.getFilename();
                break;
            case EXTERNAL:
                path = image.getFilename();
                break;

            default:
                path = null;
                break;
        }

        return CastingUtil.getURI( path )
            .orElseThrow( () -> new UniversiServerException( "Não foi possível obter a URL da imagem." ) );
    }
}
