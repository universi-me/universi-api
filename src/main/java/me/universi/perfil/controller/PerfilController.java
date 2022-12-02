package me.universi.perfil.controller;

import me.universi.api.entities.Resposta;
import me.universi.grupo.services.GrupoService;
import me.universi.perfil.entities.Perfil;
import me.universi.perfil.enums.Sexo;
import me.universi.perfil.exceptions.PerfilException;
import me.universi.perfil.services.PerfilService;
import me.universi.usuario.entities.Usuario;
import me.universi.usuario.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Map;

@Controller
public class PerfilController {
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PerfilService perfilService;

    @Autowired
    public GrupoService grupoService;

    @GetMapping("/p/**")
    public String perfil_handler(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap map) {
        try {
            Usuario usuario = (Usuario)session.getAttribute("usuario");

            String requestPathSt = request.getRequestURI().toLowerCase();

            boolean flagEditar = requestPathSt.endsWith("/editar");

            if(!flagEditar && usuarioService.usuarioPrecisaDePerfil(usuario)) {
                return "redirect:/p/"+ usuario.getUsername() +"/editar";
            }

            String[] nicknameArr = requestPathSt.split("/");

            if (flagEditar) {
                nicknameArr = Arrays.copyOf(nicknameArr, nicknameArr.length - 1);
            }

            Usuario usuarioPerfil = null;
            if (nicknameArr.length == 3) {
                String usuarioSt = nicknameArr[nicknameArr.length - 1];
                usuarioPerfil = (Usuario) usuarioService.loadUserByUsername(usuarioSt);
            }

            if(usuarioPerfil != null) {

                Perfil perfil = usuarioPerfil.getPerfil();

                if (perfil == null) {
                    throw new PerfilException("Usuário não possui um perfil.");
                }

                map.put("perfil", perfil);
                map.put("grupoService", grupoService);
                map.put("usuarioService", usuarioService);

                if(flagEditar) {
                    map.addAttribute("sexoTipo", Sexo.values());
                    map.put("flagPage", "flagEditar");
                }

                return "perfil/perfil_index";
            }

            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            throw new PerfilException("Perfil Não Encontrado.");

        } catch (Exception e) {
            map.put("error", "Perfil: " + e.getMessage());
        }
        return "perfil/perfil_index";
    }

    @ResponseBody
    @RequestMapping(value = "/perfil/editar", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object perfil_editar(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {

        Resposta resposta = new Resposta(); // default

        try {

            String perfilId = (String)body.get("perfilId");
            if(perfilId == null) {
                throw new Exception("Parametro perfilId é nulo.");
            }

            String nome         = (String)body.get("nome");
            String sobrenome    = (String)body.get("sobrenome");
            String imagem       = (String)body.get("imagem");
            String bio          = (String)body.get("bio");
            String sexo         = (String)body.get("sexo");

            Perfil perfilAtual = perfilService.findFirstById(perfilId);
            if(perfilAtual == null) {
                throw new PerfilException("Perfil não encontrado.");
            }

            if(!usuarioService.usuarioDonoDaSessao(session, perfilAtual.getUsuario())) {
                throw new PerfilException("Você não tem permissão para editar este perfil.");
            }

            if(nome != null) {
                perfilAtual.setNome(nome);
            }
            if(sobrenome != null) {
                perfilAtual.setSobrenome(sobrenome);
            }
            if(imagem != null) {
                perfilAtual.setImagem(imagem);
            }
            if(bio != null) {
                perfilAtual.setBio(bio);
            }
            if(sexo != null) {
                perfilAtual.setSexo(Sexo.valueOf(sexo));
            }

            perfilService.save(perfilAtual);

            usuarioService.atualizarUsuarioNaSessao(session);

            resposta.mensagem = "As Alterações foram salvas com sucesso.";
            resposta.sucess = true;

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
        }

        return resposta;
    }
}
