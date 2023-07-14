package me.universi.profile.controller;

import me.universi.api.entities.Response;
import me.universi.competence.services.CompetenceTypeService;
import me.universi.group.services.GroupService;
import me.universi.profile.entities.Profile;
import me.universi.profile.enums.Gender;
import me.universi.profile.exceptions.PerfilException;
import me.universi.profile.services.PerfilService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


import java.util.Map;

@RestController
@RequestMapping(value = "/api")
public class ProfileController {
    @Autowired
    public UserService userService;

    @Autowired
    public PerfilService perfilService;

    @Autowired
    public GroupService grupoService;
    @Autowired
    public CompetenceTypeService competenciaTipoService;

    @GetMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response profile() {
        Response response = new Response(); // default
        try {

            User userSession = userService.getUserInSession();

            Profile userProfile = userSession.getProfile();
            if(userProfile == null) {
                throw new PerfilException("Perfil não encontrado.");
            }

            response.body.put("profile", userProfile);
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }

        return response;
    }

    @PostMapping(value = "/profile/edit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response perfil_editar(@RequestBody Map<String, Object> body) {

        Response resposta = new Response(); // default

        try {

            String profileId = (String)body.get("profileId");
            if(profileId == null) {
                throw new Exception("Parametro perfilId é nulo.");
            }

            String name         = (String)body.get("name");
            String lastname    = (String)body.get("lastname");
            String imageUrl       = (String)body.get("imageUrl");
            String bio          = (String)body.get("bio");
            String sexo         = (String)body.get("sexo");

            Profile profileAtual = perfilService.findFirstById(profileId);
            if(profileAtual == null) {
                throw new PerfilException("Perfil não encontrado.");
            }

            if(!userService.isSessionOfUser(profileAtual.getUsuario())) {
                User userSession = userService.getUserInSession();
                if(!userService.isUserAdmin(userSession)) {
                    throw new PerfilException("Você não tem permissão para editar este perfil.");
                }
            }

            if(name != null) {
                profileAtual.setFirstname(name);
            }
            if(lastname != null) {
                profileAtual.setLastname(lastname);
            }
            if(imageUrl != null && imageUrl.length()>0) {
                profileAtual.setImage(imageUrl);
            }
            if(bio != null) {
                profileAtual.setBio(bio);
            }
            if(sexo != null) {
                profileAtual.setSexo(Gender.valueOf(sexo));
            }

            perfilService.save(profileAtual);

            userService.updateUserInSession();

            resposta.message = "As Alterações foram salvas com sucesso.";
            resposta.success = true;

        } catch (Exception e) {
            resposta.message = e.getMessage();
        }

        return resposta;
    }
}
