package me.universi.link.controller;

import me.universi.api.entities.Response;
import me.universi.link.entities.Link;
import me.universi.link.enums.TypeLink;
import me.universi.link.exceptions.LinkException;
import me.universi.link.services.LinkService;
import me.universi.profile.entities.Profile;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class LinkController {
    @Autowired
    public LinkService linkService;
    @Autowired
    public UserService userService;

    @PostMapping(value = "/link/criar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response create(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            User user = userService.getUserInSession();

            String url = (String)body.get("url");
            if(url == null) {
                throw new LinkException("Parametro url é nulo.");
            }

            String tipo = (String)body.get("tipo");
            if(tipo == null) {
                throw new LinkException("Parametro tipo é nulo.");
            }

            String nome = (String)body.get("nome");

            Link linkNew = new Link();
            linkNew.setTypeLink(TypeLink.valueOf(tipo));
            linkNew.setUrl(url);
            if(nome != null) {
                linkNew.setName((!nome.isEmpty())?nome:null);
            }


            Profile profile = user.getProfile();
            linkNew.setProfile(profile);

            linkService.save(linkNew);

            response.message = "Link Criado";
            response.success = true;

        });
    }

    @PostMapping(value = "/link/remover", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response remove(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            String id = (String)body.get("linkId");
            if(id == null) {
                throw new LinkException("Parametro linkId é nulo.");
            }

            Link link = linkService.findFirstById(id);
            if (link == null) {
                throw new LinkException("Link não encontrada.");
            }

            if(!userService.isSessionOfUser(link.getProfile().getUser())) {
                throw new LinkException("Você não tem permissão para editar este Link.");
            }

            linkService.delete(link);

            response.message = "Link removido";
            response.success = true;

        });
    }

    @PostMapping(value = "/link/atualizar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response update(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            String id = (String)body.get("linkId");
            if(id == null) {
                throw new LinkException("Parametro linkId é nulo.");
            }

            String url = (String)body.get("url");
            String tipo = (String)body.get("tipo");
            String nome = (String)body.get("nome");

            Link link = linkService.findFirstById(id);
            if (link == null) {
                throw new LinkException("Link não encontrada.");
            }

            if(!userService.isSessionOfUser(link.getProfile().getUser())) {
                throw new LinkException("Você não tem permissão para editar este Link.");
            }

            if(url != null) {
                link.setUrl(url);
            }
            if (tipo != null) {
                link.setTypeLink(TypeLink.valueOf(tipo));
            }
            if(nome != null) {
                link.setName((!nome.isEmpty())?nome:null);
            }

            linkService.save(link);

            response.message = "Link atualizado";
            response.success = true;

        });
    }

    @PostMapping(value = "/link/obter", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response get(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            String id = (String)body.get("linkId");
            if(id == null) {
                throw new LinkException("Parametro linkId é nulo.");
            }

            Link link = linkService.findFirstById(id);
            if (link == null) {
                throw new LinkException("Link não encontrada.");
            }

            response.body.put("link", link);
            response.success = true;

        });
    }

}
