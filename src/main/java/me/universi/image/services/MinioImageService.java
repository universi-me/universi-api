package me.universi.image.services;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import me.universi.api.exceptions.UniversiServerException;
import me.universi.image.entities.ImageMetadata;
import me.universi.image.enums.ImageStoreLocation;
import me.universi.image.repositories.ImageMetadataRepository;
import me.universi.minioConfig.MinioConfig;
import me.universi.profile.services.ProfileService;

@Service
public class MinioImageService {
    private final MinioConfig minioConfig;
    private final MinioClient minioClient;
    private final ImageMetadataRepository imageMetadataRepository;

    public MinioImageService( @Autowired(required = false) MinioClient minioClient, @Autowired(required = false) MinioConfig minioConfig, ImageMetadataRepository imageMetadataRepository ) {
        this.minioConfig = minioConfig;
        this.minioClient = minioClient;
        this.imageMetadataRepository = imageMetadataRepository;
    }

    public Optional<Resource> findByFilename( String filename ) {
        byte[] byteArray;

        try {
            var stream = minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket( minioConfig.bucketName )
                    .object( filename )
                    .build()
            );

            byteArray = stream.readAllBytes();
            stream.close();
        } catch ( Exception e ) {
            return Optional.empty();
        }

        return Optional.of( new ByteArrayResource( byteArray ) );
    }

    public ImageMetadata saveNewImage( MultipartFile image ) {
        //Valida o tamanho da imagem
        ImageMetadataService.getInstance().checkImageSize( image );

        //Gera um novo nome para o arquivo
        var filename = ImageMetadataService.generateFilename( image.getOriginalFilename() );

        //Faz o upload da imagem para o MinIO
        try {
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket( minioConfig.bucketName )
                    .object( filename )
                    .stream( image.getInputStream(), image.getSize(), -1 )
                    .contentType( image.getContentType() )
                    .build()
            );

            return imageMetadataRepository.saveAndFlush(
                new ImageMetadata(
                    filename,
                    image.getContentType(),
                    ProfileService.getInstance().getProfileInSession(),
                    ImageStoreLocation.MINIO,
                    new Date()
                )
            );
        } catch ( Exception e ) {
            throw new UniversiServerException( e );
        }
    }
}
