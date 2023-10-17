package me.universi.image.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;
import me.universi.api.entities.Response;
import me.universi.image.entities.Image;
import me.universi.image.exceptions.ImageException;
import me.universi.image.repositories.ImageRepository;
import me.universi.image.services.ImageService;
import me.universi.user.services.UserService;
import me.universi.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamResource;
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

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

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
    @GetMapping(value = "/img/imagem/{image}.jpg", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public ResponseEntity<InputStreamResource> getImageFromFilesystem(@PathVariable("image") String nameOfImage) {
        try {

            String filename = nameOfImage.replaceAll("[^a-f0-9]", "");
            if(!filename.contains("..") && !filename.contains("/")) {
                InputStreamResource targetStream = imageService.getImageFromFilesystem(filename);
                if(targetStream != null) {
                    return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(targetStream);
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
    public ResponseEntity<InputStreamResource> getImageFromDatabase(@PathVariable("imageId") UUID imageId) {
        try {
            Image img = imageService.findFirstById(imageId);
            if(img != null) {
                InputStream targetStream = new ByteArrayInputStream(img.getData());
                return ResponseEntity.ok().contentType(MediaType.valueOf(img.getContentType())).body(new InputStreamResource(targetStream));
            }
            return ResponseEntity.notFound().build();
        }catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

}
