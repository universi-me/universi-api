package me.universi.perfil.controller;

import me.universi.grupo.services.GrupoService;
import me.universi.perfil.entities.Perfil;
import me.universi.perfil.enums.Sexo;
import me.universi.usuario.entities.Usuario;
import me.universi.usuario.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;

@Controller
public class PerfilController {
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    public GrupoService grupoService;

    @GetMapping("/p/**")
    public String perfil_handler(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap map) {
        try {
            String requestPathSt = request.getRequestURI().toLowerCase();

            boolean flagEditar = requestPathSt.endsWith("/editar");

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
                if(perfil == null) {
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
}
