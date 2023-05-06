package me.universi.perfil.controller;

import me.universi.api.entities.Response;
import me.universi.competencia.services.CompetenceTypeService;
import me.universi.grupo.services.GroupService;
import me.universi.perfil.entities.Profile;
import me.universi.perfil.enums.Gender;
import me.universi.perfil.exceptions.PerfilException;
import me.universi.perfil.services.PerfilService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@Controller
public class PerfilController {
    @Autowired
    public UserService userService;

    @Autowired
    public PerfilService perfilService;

    @Autowired
    public GroupService grupoService;
    @Autowired
    public CompetenceTypeService competenciaTipoService;

    @PostMapping(value = "/perfil/editar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response perfil_editar(@RequestBody Map<String, Object> body) {

        Response resposta = new Response(); // default

        try {

            String perfilId = (String)body.get("perfilId");
            if(perfilId == null) {
                throw new Exception("Parametro perfilId é nulo.");
            }

            String nome         = (String)body.get("nome");
            String sobrenome    = (String)body.get("sobrenome");
            String imagem       = (String)body.get("imagemUrl");
            String bio          = (String)body.get("bio");
            String sexo         = (String)body.get("sexo");

            Profile profileAtual = perfilService.findFirstById(perfilId);
            if(profileAtual == null) {
                throw new PerfilException("Perfil não encontrado.");
            }

            if(!userService.usuarioDonoDaSessao(profileAtual.getUsuario())) {

                User userSession = userService.obterUsuarioNaSessao();
                if(!userService.isContaAdmin(userSession)) {
                    throw new PerfilException("Você não tem permissão para editar este perfil.");
                }
            }

            if(nome != null) {
                profileAtual.setFirstname(nome);
            }
            if(sobrenome != null) {
                profileAtual.setLastname(sobrenome);
            }
            if(imagem != null && imagem.length()>0) {
                profileAtual.setImage(imagem);
            }
            if(bio != null) {
                profileAtual.setBio(bio);
            }
            if(sexo != null) {
                profileAtual.setSexo(Gender.valueOf(sexo));
            }

            perfilService.save(profileAtual);

            userService.atualizarUsuarioNaSessao();

            resposta.message = "As Alterações foram salvas com sucesso.";
            resposta.success = true;

        } catch (Exception e) {
            resposta.message = e.getMessage();
        }

        return resposta;
    }
}
