package me.universi.perfil.controller;

import me.universi.api.entities.Response;
import me.universi.competencia.enums.Level;
import me.universi.competencia.services.CompetenciaTipoService;
import me.universi.grupo.services.GrupoService;
import me.universi.perfil.entities.Perfil;
import me.universi.perfil.enums.Sexo;
import me.universi.perfil.exceptions.PerfilException;
import me.universi.perfil.services.PerfilService;
import me.universi.user.entities.User;
import me.universi.user.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@Controller
public class PerfilController {
    @Autowired
    public UsuarioService usuarioService;

    @Autowired
    public PerfilService perfilService;

    @Autowired
    public GrupoService grupoService;
    @Autowired
    public CompetenciaTipoService competenciaTipoService;

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

            Perfil perfilAtual = perfilService.findFirstById(perfilId);
            if(perfilAtual == null) {
                throw new PerfilException("Perfil não encontrado.");
            }

            if(!usuarioService.usuarioDonoDaSessao(perfilAtual.getUsuario())) {

                User userSession = usuarioService.obterUsuarioNaSessao();
                if(!usuarioService.isContaAdmin(userSession)) {
                    throw new PerfilException("Você não tem permissão para editar este perfil.");
                }
            }

            if(nome != null) {
                perfilAtual.setNome(nome);
            }
            if(sobrenome != null) {
                perfilAtual.setSobrenome(sobrenome);
            }
            if(imagem != null && imagem.length()>0) {
                perfilAtual.setImagem(imagem);
            }
            if(bio != null) {
                perfilAtual.setBio(bio);
            }
            if(sexo != null) {
                perfilAtual.setSexo(Sexo.valueOf(sexo));
            }

            perfilService.save(perfilAtual);

            usuarioService.atualizarUsuarioNaSessao();

            resposta.message = "As Alterações foram salvas com sucesso.";
            resposta.success = true;

        } catch (Exception e) {
            resposta.message = e.getMessage();
        }

        return resposta;
    }
}
