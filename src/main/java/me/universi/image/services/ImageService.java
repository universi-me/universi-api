package me.universi.image.services;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import me.universi.Sys;
import me.universi.api.exceptions.UniversiBadRequestException;
import me.universi.api.exceptions.UniversiPayloadTooLargeException;
import me.universi.api.exceptions.UniversiServerException;
import me.universi.api.interfaces.EntityService;
import me.universi.image.controller.ImageController;
import me.universi.image.entities.Image;
import me.universi.image.exceptions.ImageException;
import me.universi.minioConfig.MinioConfig;
import me.universi.profile.services.ProfileService;
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
public class ImageService extends EntityService<Image> {
    private final ImageRepository imageRepository;
    private final MinioConfig minioConfig;
    public final MinioClient minioClient;

    private final ProfileService profileService;

    @Value("${SAVE_IMAGE_LOCAL}")
    public boolean saveImageLocal;

    @Value("${PATH_IMAGE_SAVE}")
    public String pathImageSave;

    @Value("${IMAGE_UPLOAD_LIMIT}")
    public int imageUploadLimit;

    @Value("${IMGUR_CLIENT_ID}")
    public String imgurClientId;


    public ImageService(ImageRepository imageRepository, @Autowired(required = false) MinioClient minioClient, @Autowired(required = false) MinioConfig minioConfig, ProfileService profileService) {
        this.imageRepository = imageRepository;
        this.minioClient = minioClient;
        this.minioConfig = minioConfig;
        this.profileService = profileService;

        this.entityName = "Imagem";
    }

    public static ImageService getInstance() {
        return Sys.context.getBean("imageService", ImageService.class);
    }

    @Override
    public Optional<Image> find( UUID id ) {
        return imageRepository.findById( id );
    }

    @Override
    public List<Image> findAll() {
        return imageRepository.findAll();
    }

    public String saveImageFromMultipartFile(MultipartFile image) {
        // check if save image in local or minIO
        if(MinioConfig.isMinioEnabled()) {
            return saveImageInMinIO(image);
        }
        return isSaveImageLocal() ? saveImageInFilesystem(image) : saveImageInDatabase(image);
    }

    private String saveImageInFilesystem(MultipartFile image) {
        var imageBytes = checkImageSize( image );

        String tokenRandom = UUID.randomUUID().toString();

        byte[] encodedHash = getSha1Digest().digest(tokenRandom.getBytes(StandardCharsets.UTF_8));
        String nameOfImage = ConvertUtil.bytesToHex(encodedHash);


        File imagemDir = new File(getPathImageSave());
        if (!imagemDir.exists()){
            imagemDir.mkdirs();
        }

        File file = new File(imagemDir.toString() + "/" + nameOfImage);

        try {
            OutputStream os = new FileOutputStream(file);
            os.write( imageBytes );
            os.close();
        } catch ( IOException e ) {
            throw new UniversiServerException("Falha ao salvar imagem em disco.");
        }

        if(nameOfImage != null) {
            return ImageController.ENTITY_PATH + ImageController.FILESYSTEM_PATH + "/" + nameOfImage + ".jpg";
        }

        throw new UniversiServerException("Falha ao salvar imagem em disco.");
    }

    private String saveImageInDatabase(MultipartFile image) throws UniversiBadRequestException {
        var imageBytes = checkImageSize(image);

        Image img = new Image();
        img.setData(imageBytes);
        img.setFilename(image.getOriginalFilename());
        img.setContentType(image.getContentType());
        img.setSize(image.getSize());
        img.setAuthor(UserService.getInstance().getUserInSession().getProfile());
        img.setCreated(ConvertUtil.getDateTimeNow());

        img = imageRepository.saveAndFlush( img );

        return ImageController.ENTITY_PATH + ImageController.DATABASE_PATH + "/" + img.getId();
    }

    public byte[] findByMinioId( String imageId ) {
        byte[] imageBytes;

        try {
            var stream = minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket( minioConfig.bucketName )
                    .object( imageId + ".jpg" )
                    .build()
            );

            imageBytes = stream.readAllBytes();
            stream.close();
        } catch ( Exception e ) {
            throw makeNotFoundException( "id", imageId );
        }

        return imageBytes;
    }

    private String saveImageInMinIO(MultipartFile image) {
        //Valida o tamanho da imagem
        checkImageSize( image );

        //Gera um novo nome para o arquivo
        var objectName = UUID.randomUUID().toString();

        //Faz o upload da imagem para o MinIO
        try {
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket( minioConfig.bucketName )
                    .object( objectName + ".jpg" )
                    .stream( image.getInputStream(), image.getSize(), -1 )
                    .contentType( image.getContentType() )
                    .build()
            );

            return ImageController.ENTITY_PATH + ImageController.MINIO_PATH + "/" + objectName;
        } catch ( Exception e ) {
            throw new UniversiServerException( e );
        }
    }

    public Optional<Resource> getImageFromFilesystem(String filename) {
        File initialFile = new File(getPathImageSave(), filename);
        if(initialFile.exists() && !initialFile.isDirectory()) {
            return Optional.of( new FileSystemResource(initialFile) );
        }
        return Optional.empty();
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

    private boolean isSaveImageLocal() {
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

    public byte[] checkImageSize( MultipartFile image) throws UniversiPayloadTooLargeException, UniversiBadRequestException {
        byte[] imageBytes;
        try {
            imageBytes = image.getBytes();
        } catch (Exception e) {
            throw new UniversiBadRequestException( "Não foi possível processar a imagem" );
        }

        if (imageBytes.length > 1024 * 1024 * getImageUploadLimit()) {
            throw new UniversiPayloadTooLargeException( "Imagem muito grande." );
        }

        return imageBytes;
    }

    public MessageDigest getSha1Digest() throws UniversiServerException {
        try {
            return MessageDigest.getInstance("SHA-1");
        } catch (Exception e) {
            throw new UniversiServerException( e );
        }
    }

    @Override
    public boolean hasPermissionToEdit( Image image ) {
        return profileService.isSessionOfProfile( image.getAuthor() );
    }

    @Override
    public boolean hasPermissionToDelete(Image image) {
        return hasPermissionToEdit( image );
    }
}
