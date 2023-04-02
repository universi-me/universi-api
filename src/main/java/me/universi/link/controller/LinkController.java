package me.universi.link.controller;

import me.universi.api.entities.Resposta;
import me.universi.link.entities.Link;
import me.universi.link.enums.TipoLink;
import me.universi.link.exceptions.LinkException;
import me.universi.link.services.LinkService;
import me.universi.perfil.entities.Perfil;
import me.universi.usuario.entities.Usuario;
import me.universi.usuario.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class LinkController {
    @Autowired
    public LinkService linkService;
    @Autowired
    public UsuarioService usuarioService;

    @PostMapping(value = "/link/criar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Resposta create(@RequestBody Map<String, Object> body) {
        Resposta resposta = new Resposta();
        try {

            Usuario usuario = usuarioService.obterUsuarioNaSessao();

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
            linkNew.setTipo(TipoLink.valueOf(tipo));
            linkNew.setUrl(url);
            if(nome != null) {
                linkNew.setNome((nome.length()>0)?nome:null);
            }


            Perfil perfil = usuario.getPerfil();
            linkNew.setPerfil(perfil);

            linkService.save(linkNew);

            resposta.mensagem = "Link Criado";
            resposta.sucess = true;
            return resposta;

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/link/remover", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Resposta remove(@RequestBody Map<String, Object> body) {
        Resposta resposta = new Resposta();
        try {

            String id = (String)body.get("linkId");
            if(id == null) {
                throw new LinkException("Parametro linkId é nulo.");
            }

            Link link = linkService.findFirstById(Long.valueOf(id));
            if (link == null) {
                throw new LinkException("Link não encontrada.");
            }

            Usuario usuario = usuarioService.obterUsuarioNaSessao();

            Perfil perfil = usuario.getPerfil();

            if(link.getPerfil().getId() != perfil.getId()) {
                throw new LinkException("Você não tem permissão para editar este Link.");
            }

            linkService.delete(link);

            resposta.mensagem = "Link removido";
            resposta.sucess = true;
            return resposta;

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/link/atualizar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Resposta update(@RequestBody Map<String, Object> body) {
        Resposta resposta = new Resposta();
        try {

            String id = (String)body.get("linkId");
            if(id == null) {
                throw new LinkException("Parametro linkId é nulo.");
            }

            String url = (String)body.get("url");
            String tipo = (String)body.get("tipo");
            String nome = (String)body.get("nome");

            Link link = linkService.findFirstById(Long.valueOf(id));
            if (link == null) {
                throw new LinkException("Link não encontrada.");
            }

            Usuario usuario = usuarioService.obterUsuarioNaSessao();

            Perfil perfil = usuario.getPerfil();

            if(link.getPerfil().getId() != perfil.getId()) {
                throw new LinkException("Você não tem permissão para editar este Link.");
            }

            if(url != null) {
                link.setUrl(url);
            }
            if (tipo != null) {
                link.setTipo(TipoLink.valueOf(tipo));
            }
            if(nome != null) {
                link.setNome((nome.length()>0)?nome:null);
            }

            linkService.save(link);

            resposta.mensagem = "Link atualizado";
            resposta.sucess = true;
            return resposta;

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

    @PostMapping(value = "/link/obter", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Resposta get(@RequestBody Map<String, Object> body) {
        Resposta resposta = new Resposta();
        try {

            String id = (String)body.get("linkId");
            if(id == null) {
                throw new LinkException("Parametro linkId é nulo.");
            }

            Link link = linkService.findFirstById(Long.valueOf(id));
            if (link == null) {
                throw new LinkException("Link não encontrada.");
            }

            resposta.conteudo.put("link", link);

            resposta.sucess = true;
            return resposta;

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
            return resposta;
        }
    }

}
