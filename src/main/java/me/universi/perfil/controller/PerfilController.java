package me.universi.perfil.controller;

import me.universi.api.entities.Resposta;
import me.universi.grupo.services.GrupoService;
import me.universi.perfil.entities.Perfil;
import me.universi.perfil.enums.Sexo;
import me.universi.perfil.repositories.PerfilRepository;
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
import java.util.Optional;

@Controller
public class PerfilController {
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PerfilService perfilService;

    @Autowired
    public GrupoService grupoService;

    @Autowired
    public PerfilRepository perfilRepository;

    @GetMapping("/p/**")
    public String perfil_handler(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap map) {
        try {
            Usuario usuario = (Usuario)session.getAttribute("usuario");

            String requestPathSt = request.getRequestURI().toLowerCase();

            boolean flagEditar = requestPathSt.endsWith("/editar");

            if(!flagEditar && (usuario.getPerfil() == null || usuario.getPerfil().getNome() == null)) {
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
                if(flagEditar) {

                    if(perfil==null && usuario.getId() == usuarioPerfil.getId()) {
                        perfil = new Perfil();
                        perfil.setUsuario(usuario);
                        perfilRepository.save(perfil);
                    }

                }

                if (perfil == null) {
                    throw new Exception("Usuário não possui um perfil.");
                }

                map.put("perfil", perfil);
                map.put("grupoService", grupoService);

                if(flagEditar) {
                    map.addAttribute("sexoTipo", Sexo.values());
                    map.put("flagPage", "flagEditar");
                }

                return "perfil/perfil_index";
            }

            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            throw new Exception("Perfil Não Encontrado.");

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

            perfilAtual.setNome(nome);
            perfilAtual.setSobrenome(sobrenome);
            perfilAtual.setImagem(imagem);
            perfilAtual.setBio(bio);
            perfilAtual.setSexo(Sexo.valueOf(sexo));

            Usuario usuario = (Usuario)session.getAttribute("usuario");
            usuario.setPerfil(perfilAtual);

            perfilRepository.save(perfilAtual);
            usuarioService.save(usuario);

            resposta.enderecoParaRedirecionar = "/p/" + usuario.getUsername();

            // resposta.mensagem = "Implementar edição de perfil.";

        } catch (Exception e) {
            resposta.mensagem = e.getMessage();
        }

        return resposta;
    }
}
