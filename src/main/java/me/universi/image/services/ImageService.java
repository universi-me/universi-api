package me.universi.image.services;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.UUID;
import me.universi.Sys;
import me.universi.image.entities.Image;
import me.universi.image.exceptions.ImageException;
import me.universi.minioConfig.MinioConfig;
import me.universi.user.services.UserService;
import me.universi.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import me.universi.image.repositories.ImageRepository;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {
    private final ImageRepository imageRepository;
    public final MinioClient minioClient;

    @Value("${SAVE_IMAGE_LOCAL}")
    public boolean saveImageLocal;

    @Value("${PATH_IMAGE_SAVE}")
    public String pathImageSave;

    @Value("${IMAGE_UPLOAD_LIMIT}")
    public int imageUploadLimit;

    @Value("${IMGUR_CLIENT_ID}")
    public String imgurClientId;


    
    public ImageService(ImageRepository imageRepository, @Autowired(required = false) MinioClient minioClient) {
        this.imageRepository = imageRepository;
        this.minioClient = minioClient;
    }

    public static ImageService getInstance() {
        return Sys.context.getBean("imageService", ImageService.class);
    }

    public Image findFirstById(UUID id) {
        return imageRepository.findFirstById(id).orElse(null);
    }

    public Image save(Image image) {
        return imageRepository.saveAndFlush(image);
    }

    public String saveImageFromMultipartFile(MultipartFile image) throws Exception {
        // check if save image in local or minIO
        if(MinioConfig.isMinioEnabled()) {
            return saveImageInMinIO(image);
        }
        return isSaveImageLocal() ? saveImageInFilesystem(image) : saveImageInDatabase(image);
    }

    public String saveImageInFilesystem(MultipartFile image) throws Exception {

        checkImageSize(image.getBytes().length);

        String tokenRandom = UUID.randomUUID().toString();

        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] encodedHash = digest.digest(tokenRandom.getBytes(StandardCharsets.UTF_8));
        String nameOfImage = ConvertUtil.bytesToHex(encodedHash);


        File imagemDir = new File(getPathImageSave());
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

        checkImageSize(image.getBytes().length);

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
    
    public String saveImageInMinIO(MultipartFile image) throws ImageException {
        try {
            //Valida o tamanho da imagem
            checkImageSize(image.getSize());

            //Gera um novo nome para o arquivo
            String objectName = UUID.randomUUID().toString() + ".jpg";

            //Faz o upload da imagem para o MinIO
            try (InputStream inputStream = image.getInputStream()) {
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(MinioConfig.getInstance().bucketName)
                        .object(objectName)
                        .stream(inputStream, image.getSize(), -1)
                        .contentType(image.getContentType())
                        .build());
            }

            //Retorna o nome do objeto salvo
            return "/img/minio/" + objectName;
            
        } catch (Exception e) {
            throw new ImageException("Erro ao salvar imagem no MinIO: " + e.getMessage());
        }
    }

    public Resource getImageFromFilesystem(String filename) throws Exception {
        File initialFile = new File(getPathImageSave(), filename);
        if(initialFile.exists() && !initialFile.isDirectory()) {
            return new FileSystemResource(initialFile);
        }
        return null;
    }

    public String uploadImagemImgur(MultipartFile imagem) throws Exception {
        // post da imagem para api da Imgur e retornar o link.
        String urlApi = "https://api.imgur.com/3/image";
        String boundary = "----WebKitFormBoundary"+Long.toHexString(System.currentTimeMillis());
        URLConnection connection = new URL(urlApi).openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("Authorization", "Client-ID " + getImgurClientId());
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

    public boolean isSaveImageLocal() {
        return saveImageLocal;
    }

    public String getPathImageSave() {
        return pathImageSave;
    }

    public int getImageUploadLimit() {
        return imageUploadLimit;
    }

    public String getImgurClientId() {
        return imgurClientId;
    }

    public void checkImageSize(long imageSize) throws ImageException {
        if (imageSize > 1024 * 1024 * getImageUploadLimit()) {
            throw new ImageException("Imagem muito grande.");
        }
    }

}
