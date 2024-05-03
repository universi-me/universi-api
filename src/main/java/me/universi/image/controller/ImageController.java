package me.universi.image.controller;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import me.universi.api.entities.Response;
import me.universi.image.entities.Image;
import me.universi.image.exceptions.ImageException;
import me.universi.image.services.ImageService;

import me.universi.minioConfig.MinioConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;


@RestController
@RequestMapping("/api")
public class ImageController {

    private final ImageService imageService;
    private final MinioClient minioClient;

    public ImageController(ImageService imageService, @Autowired(required = false) MinioClient minioClient) {
        this.imageService = imageService;
        this.minioClient = minioClient;
    }

    @PostMapping(value = "/imagem/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response upload_of_image(@RequestParam("imagem") MultipartFile image) {
        return Response.buildResponse(response -> {

            String link = imageService.saveImageFromMultipartFile(image);
            if(link != null) {
                response.body.put("link", link.toString());
                return;
            }

            throw  new ImageException("Falha ao salvar imagem.");
        });
    }

    // get image from minIO
    @GetMapping(value = "/img/minio/{imageId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> getImageFromMinio(@PathVariable("imageId") String imageId) {
        try {
            //Recupera a imagem do MinIO
            InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(MinioConfig.getConfig().bucketName)
                    .object(imageId)
                    .build()
            );

            //Lê os bytes da imagem do stream
            byte[] imageBytes = stream.readAllBytes();

            //Fecha o stream de entrada
            stream.close();

            //Retorna os bytes da imagem com um status HTTP 200 OK
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(imageBytes);
        } catch (Exception e) {
            //Se ocorrer um erro ao recuperar a imagem, retorna um status HTTP 404 Not Found
            return ResponseEntity.notFound().build();
        }
    }

    // get image from filesystem
    @GetMapping(value = "/img/imagem/{image}.jpg", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    @Cacheable("img")
    public ResponseEntity<Resource> getImageFromFilesystem(@PathVariable("image") String nameOfImage) {
        try {

            String filename = nameOfImage.replaceAll("[^a-f0-9]", "");
            if(!filename.contains("..") && !filename.contains("/")) {

                Resource imageResource = imageService.getImageFromFilesystem(filename);

                if(imageResource != null) {
                    return ResponseEntity
                            .ok()
                            .contentLength(imageResource.contentLength())
                            .contentType(MediaType.IMAGE_JPEG)
                            .cacheControl(CacheControl.maxAge(365, TimeUnit.DAYS))
                            .body(imageResource);
                }
            }
            return ResponseEntity.notFound().build();

        }catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // get image from database
    @GetMapping(value = "/img/store/{imageId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    @Cacheable("img")
    public ResponseEntity<Resource> getImageFromDatabase(@PathVariable("imageId") UUID imageId) {
        try {
            Image img = imageService.findFirstById(imageId);
            if(img != null) {

                byte[] imageData = img.getData();
                ByteArrayResource resource = new ByteArrayResource(imageData);

                return ResponseEntity
                        .ok()
                        .contentLength(resource.contentLength())
                        .contentType(MediaType.valueOf(img.getContentType()))
                        .cacheControl(CacheControl.maxAge(365, TimeUnit.DAYS))
                        .body(resource);
            }
            return ResponseEntity.notFound().build();
        }catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

}
