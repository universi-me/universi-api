package me.universi.image.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.UUID;
import me.universi.image.entities.Image;
import me.universi.image.exceptions.ImageException;
import me.universi.user.services.UserService;
import me.universi.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import me.universi.image.repositories.ImageRepository;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {
    private final  Environment env;
    private final  ImageRepository imageRepository;

    @Autowired
    public ImageService(Environment env, ImageRepository imageRepository) {
        this.env = env;
        this.imageRepository = imageRepository;
    }

    public Image findFirstById(UUID id) {
        return imageRepository.findFirstById(id).orElse(null);
    }

    public Image save(Image image) {
        return imageRepository.saveAndFlush(image);
    }

    public String saveImageFromMultipartFile(MultipartFile image) throws Exception {
        // check if save image in local or database.
        return (Boolean.parseBoolean(env.getProperty("SAVE_IMAGE_LOCAL"))) ? saveImageInFilesystem(image) : saveImageInDatabase(image);
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

        throw new ImageException("Falha ao salvar imagem em disco.");
    }

    public String saveImageInDatabase(MultipartFile image) throws Exception {

        // image big than 1MB.
        if(image.getBytes().length > 1024 * 1024 * Integer.parseInt(env.getProperty("IMAGE_UPLOAD_LIMIT"))) {
            throw new ImageException("Imagem muito grande.");
        }

        Image img = new Image();
        img.setData(image.getBytes());
        img.setFilename(image.getOriginalFilename());
        img.setContentType(image.getContentType());
        img.setSize(image.getSize());
        img.setAuthor(UserService.getInstance().getUserInSession().getProfile());
        img.setCreated(ConvertUtil.getDateTimeNow());

        img = save(img);

        if(img.getId() != null) {
            return "/img/store/" + img.getId();
        }

        throw new ImageException("Falha ao salvar imagem.");
    }

    public Resource getImageFromFilesystem(String filename) throws Exception {
        File initialFile = new File(env.getProperty("PATH_IMAGE_SAVE"), filename);
        if(initialFile.exists() && !initialFile.isDirectory()) {
            return new FileSystemResource(filename);
        }
        return null;
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
        throw new ImageException("Falha ao fazer upload da imagem.");
    }

}
