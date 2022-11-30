package me.universi.imagem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.universi.api.entities.Resposta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.util.Map;


@RestController
public class ImagemController {
    @Autowired
    private Environment env;

    @PostMapping("/imagem/upload")
    public Object upload_de_imagem(@RequestParam("imagem") MultipartFile imagem) {
        Resposta resposta = new Resposta();
        try {

            String link = (Boolean.parseBoolean(env.getProperty("SALVAR_IMAGEM_EM_DISCO")))?salvarImagemEmDisco(imagem):uploadImagemImgur(imagem);

            if(link != null) {
                resposta.conteudo.put("link", link.toString());
                resposta.sucess = true;
                return resposta;
            }

            resposta.mensagem = "Falha ao salvar imagem.";
            return resposta;

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @ResponseBody
    @GetMapping(value = "/img/imagem/{imagem}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<InputStreamResource> obterImageEmDisco(HttpServletResponse response, @PathVariable("imagem") String nomeImagem) {
        try {

            String filename = nomeImagem.replaceAll("[^a-z0-9]", "");
            filename = filename.replaceAll("jpg", "");

            File initialFile = new File(env.getProperty("DIRETORIO_DA_IMAGEM"),"imagem_" + filename + ".jpg");

            InputStream targetStream = new FileInputStream(initialFile);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(new InputStreamResource(targetStream));

        }catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    public String salvarImagemEmDisco(MultipartFile imagem) throws Exception {

        String nomeDaImage = Long.toHexString(System.currentTimeMillis())+".jpg";

        File imagemDir = new File(env.getProperty("DIRETORIO_DA_IMAGEM"));
        if (!imagemDir.exists()){
            imagemDir.mkdirs();
        }

        File file = new File(imagemDir.toString() + "/imagem_" + nomeDaImage);

        OutputStream os = new FileOutputStream(file);
        os.write(imagem.getBytes());

        if(nomeDaImage != null) {
            return "/img/imagem/" + nomeDaImage;
        }

        throw new Exception("Falha ao salvar imagem em disco.");
    }

    public String uploadImagemImgur(MultipartFile imagem) throws Exception {
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
