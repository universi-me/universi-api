package me.universi.image.controller;

import java.io.*;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import me.universi.api.entities.Response;
import me.universi.image.entities.Image;
import me.universi.image.exceptions.ImageException;
import me.universi.image.services.ImageService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ImageController {

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
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
                        .contentLength(img.getSize())
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
