package me.universi.image.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;
import me.universi.api.entities.Response;
import me.universi.image.entities.Image;
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
    @Autowired
    private Environment env;

    @Autowired
    private ImageService imageService;

    @PostMapping(value = "/imagem/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response upload_of_image(@RequestParam("imagem") MultipartFile image) {
        return Response.buildResponse(response -> {

            // check if save image in local or database.
            String link = (Boolean.parseBoolean(env.getProperty("SAVE_IMAGE_LOCAL"))) ? saveImageInFilesystem(image) : saveImageInDatabase(image);

            // return link of image remote or local.
            if(link != null) {
                response.body.put("link", link.toString());
                return;
            }

            throw  new Exception("Falha ao salvar imagem.");

        });
    }

    // get image from filesystem
    @GetMapping(value = "/img/imagem/{image}.jpg", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public ResponseEntity<InputStreamResource> getImageFromFilesystem(@PathVariable("image") String nameOfImage) {
        try {

            String filename = nameOfImage.replaceAll("[^a-f0-9]", "");

            if(!filename.contains("..") && !filename.contains("/")) {
                // parse do arquivo imagem salva para saida url.
                File initialFile = new File(env.getProperty("PATH_IMAGE_SAVE"), filename);
                InputStream targetStream = new FileInputStream(initialFile);
                return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(new InputStreamResource(targetStream));
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

    public String saveImageInFilesystem(MultipartFile image) throws Exception {

        String tokenRandom = UUID.randomUUID().toString();

        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] encodedHash = digest.digest(tokenRandom.getBytes(StandardCharsets.UTF_8));
        String nameOfImage = ConvertUtil.bytesToHex(encodedHash);


        File imagemDir = new File(env.getProperty("PATH_IMAGE_SAVE"));
        if (!imagemDir.exists()){
            imagemDir.mkdirs();
        }

        File file = new File(imagemDir.toString() + "/" + nameOfImage);

        OutputStream os = new FileOutputStream(file);
        os.write(image.getBytes());

        if(nameOfImage != null) {
            return "/img/imagem/" + nameOfImage + ".jpg";
        }

        throw new Exception("Falha ao salvar imagem em disco.");
    }

    public String saveImageInDatabase(MultipartFile image) throws Exception {

        // image big than 1MB.
        if(image.getBytes().length > 1024 * 1024 * Integer.parseInt(env.getProperty("IMAGE_UPLOAD_LIMIT"))) {
            throw new Exception("Imagem muito grande.");
        }

        Image img = new Image();
        img.setData(image.getBytes());
        img.setFilename(image.getOriginalFilename());
        img.setContentType(image.getContentType());
        img.setSize(image.getSize());
        img.setAuthor(UserService.getInstance().getUserInSession().getProfile());
        img.setCreated(ConvertUtil.getDateTimeNow());

        img = imageService.save(img);

        if(img.getId() != null) {
            return "/img/store/" + img.getId();
        }

        throw new Exception("Falha ao salvar imagem.");
    }

    public String uploadImagemImgur(MultipartFile imagem) throws Exception {
        // post da imagem para api da Imgur e retornar o link.
        String urlApi = "https://api.imgur.com/3/image";
        String boundary = "----WebKitFormBoundary"+Long.toHexString(System.currentTimeMillis());
        URLConnection connection = new URL(urlApi).openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("Authorization", "Client-ID " + env.getProperty("IMGUR_CLIENT_ID"));
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        OutputStream outPut = connection.getOutputStream();
        outPut.write(("--" + boundary).getBytes());
        outPut.write(("\n").getBytes());
        outPut.write(("Content-Disposition: form-data; name=\"image\"; filename=\"" + imagem.getName() + "\"").getBytes());
        outPut.write(("\n").getBytes());
        outPut.write(("Content-Type: "+imagem.getContentType()).getBytes());
        outPut.write(("\n").getBytes());
        outPut.write(("\n").getBytes());
        outPut.write(imagem.getBytes());
        outPut.write(("\n").getBytes());
        outPut.write(("--" + boundary + "--").getBytes());
        outPut.write(("\n").getBytes());
        HttpURLConnection connectionResp = ((HttpURLConnection)connection);
        String strCurrentLine = "";
        if (connectionResp.getResponseCode() == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader(connectionResp.getInputStream()));
            String resp;
            while ((resp = br.readLine()) != null) {
                strCurrentLine += resp;
            }
            ObjectMapper mapper = new ObjectMapper();
            Map mapRequest = mapper.readValue(strCurrentLine, Map.class);
            return ((Map)mapRequest.get("data")).get("link").toString();
        }
        throw new Exception("Falha ao fazer upload da imagem.");
    }
}
