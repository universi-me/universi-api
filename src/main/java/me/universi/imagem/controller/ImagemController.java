package me.universi.imagem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;
import me.universi.api.entities.Response;
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

import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ImagemController {
    @Autowired
    private Environment env;

    @PostMapping(value = "/imagem/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response upload_de_image(@RequestParam("imagem") MultipartFile imagem) {
        return Response.buildResponse(response -> {

            // check if save image in local or remote.
            // TODO: save image in database.
            String link = (Boolean.parseBoolean(env.getProperty("SAVE_IMAGE_LOCAL")))?salvarImagemEmDisco(imagem):uploadImagemImgur(imagem);
            
            // return link of image remote or local.
            if(link != null) {
                response.body.put("link", link.toString());
                return;
            }

            throw  new Exception("Falha ao salvar imagem.");

        });
    }

    @GetMapping(value = "/img/imagem/{imagem}.jpg", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public ResponseEntity<InputStreamResource> obterImageEmDisco(HttpServletResponse response, @PathVariable("imagem") String nomeImagem) {
        try {

            String filename = nomeImagem.replaceAll("[^a-f0-9]", "");

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

    public String salvarImagemEmDisco(MultipartFile imagem) throws Exception {

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
        os.write(imagem.getBytes());

        if(nameOfImage != null) {
            return "/img/imagem/" + nameOfImage + ".jpg";
        }

        throw new Exception("Falha ao salvar imagem em disco.");
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
