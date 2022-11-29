package me.universi.imagem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.universi.api.entities.Resposta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;


@RestController
public class ImagemController {
    @Autowired
    private Environment env;

    @PostMapping("/imagem/upload")
    public Object upload_de_imagem(@RequestParam("imagem") MultipartFile imagem) {
        Resposta resposta = new Resposta();
        try {

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

                resposta.conteudo.put("link", ((Map)mapRequest.get("data")).get("link").toString());
                resposta.sucess = true;
                return resposta;
            }

            resposta.mensagem = "Falha ao fazer upload da imagem.";
            return resposta;

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }
}
