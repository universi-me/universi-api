package me.universi.grupo.controller;

import me.universi.grupo.entities.Grupo;
import me.universi.grupo.enums.GrupoTipo;
import me.universi.grupo.exceptions.GrupoException;
import me.universi.grupo.services.GrupoService;

import me.universi.usuario.entities.Usuario;
import me.universi.usuario.services.SecurityUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class GrupoController
{
    @Autowired
    public GrupoService grupoService;

    @Autowired
    public SecurityUserDetailsService usuarioService;

    // mapaear tudo exceto, /css, /js, /img, /favicon.ico, comflita com static resources do Thymeleaf
    @GetMapping(value = {"{url:(?!css$|js$|img$|favicon.ico$).*}/**"})
    public String grupo_handler(HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap map) {
        try {
            Usuario usuario = (Usuario)session.getAttribute("usuario");

            String requestPathSt = request.getRequestURI().toLowerCase();

            boolean flagEditar = requestPathSt.endsWith("/editar");
            boolean flagCriar = requestPathSt.endsWith("/criar");
            boolean flagAdicionar = requestPathSt.endsWith("/adicionar");
            boolean flagEdicao = flagEditar | flagCriar | flagAdicionar;

            if(flagCriar) {
                requestPathSt = requestPathSt.substring(0, requestPathSt.length() - 6);
            } else if(flagEditar) {
                requestPathSt = requestPathSt.substring(0, requestPathSt.length() - 7);
            } else if(flagAdicionar) {
                requestPathSt = requestPathSt.substring(0, requestPathSt.length() - 10);
            }

            String[] nicknameArr = requestPathSt.split("/");

            Grupo grupoRoot = null;
            Grupo grupoAtual = null;

            grupoRoot = grupoService.findFirstByGrupoRootAndNickname(true, nicknameArr[1]);
            if(grupoRoot != null) {
                grupoAtual = grupoService.parentescoCheckGrupo(grupoRoot, nicknameArr);
            }

            if(grupoAtual != null) {
                map.addAttribute("grupo", grupoAtual);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                throw new GrupoException("Grupo não foi encontrado!");
            }

            if(flagEdicao) {
                grupoService.verificarPermissaoParaGrupo(grupoAtual, usuario);
                session.setAttribute("lastPath", requestPathSt);
                map.addAttribute("tiposGrupo", GrupoTipo.values());

                if(flagEditar) {
                    return "grupo/editar";
                }

                if(flagCriar) {
                    return "grupo/criar";
                }

                if(flagAdicionar) {
                    return "grupo/adicionar";
                }
            }

        } catch (Exception e){
            map.put("error", e.getMessage());
        }
        return "grupo/grupo";
    }


    // http://localhost:8080/projeto/criar?nome=teste&descricao=teste2
    @RequestMapping("/grupo/criar")
    public Object grupo_criar(HttpSession session, HttpServletResponse response, @RequestParam("grupoId") Long grupoId, @RequestParam("nickname") String nickname, @RequestParam("nome") String nome, @RequestParam("descricao") String descricao, @RequestParam("tipo") GrupoTipo tipo) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            Grupo grupoPai = grupoService.findById(grupoId);

            if(grupoService.verificarPermissaoParaGrupo(grupoPai, usuario)) {
                Grupo grupoNew = new Grupo();
                grupoNew.setNickname(nickname);
                grupoNew.setNome(nome);
                grupoNew.setDescricao(descricao);
                grupoNew.setTipo(tipo);
                grupoNew.setAdmin(usuario.getPerfil());
                grupoService.adicionarSubgrupo(grupoPai, grupoNew);

                return "redirect:"+session.getAttribute("lastPath");
            }

            return "Falha ao criar grupo";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @RequestMapping("/grupo/editar")
    public Object grupo_editar(HttpSession session, HttpServletResponse response, @RequestParam("grupoId") Long grupoId, @RequestParam("nickname") String nickname, @RequestParam("nome") String nome, @RequestParam("descricao") String descricao, @RequestParam("tipo") GrupoTipo tipo) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            Grupo grupoEdit = grupoService.findById(grupoId);

            if(grupoService.verificarPermissaoParaGrupo(grupoEdit, usuario)) {
                grupoEdit.setNome(nome);
                grupoEdit.setNickname(nickname);
                grupoEdit.setDescricao(descricao);
                grupoEdit.setTipo(tipo);
                grupoService.save(grupoEdit);
                return "redirect:"+session.getAttribute("lastPath");
            }

            return "Falha ao criar grupo";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @RequestMapping("/grupo/adicionar")
    public Object grupo_adicionar_participante(HttpSession session, HttpServletResponse response, @RequestParam("grupoId") Long grupoId, @RequestParam("participante") String participante) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            Grupo grupoEdit = grupoService.findById(grupoId);

            Usuario participanteUser = null;
            if(participante != null && participante.length() > 0) {
                if (participante.contains("@")) {
                    participanteUser = (Usuario) usuarioService.findFirstByEmail(participante);
                } else {
                    participanteUser = (Usuario) usuarioService.loadUserByUsername(participante);
                }
            }

            if(participanteUser != null && grupoService.verificarPermissaoParaGrupo(grupoEdit, usuario)) {
                grupoService.adicionarParticipante(grupoEdit, participanteUser.getPerfil());
                return "redirect:"+session.getAttribute("lastPath");
            }

            return "Falha ao criar grupo";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    // http://localhost:8080/projeto/remover?id=1
    @RequestMapping("/grupo/remover")
    @ResponseBody
    public Object grupo_remove(HttpSession session, HttpServletResponse response, @RequestParam("id") Long id) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            Grupo grupo = grupoService.findById(id);

            if(grupoService.verificarPermissaoParaGrupo(grupo, usuario)) {
                grupoService.delete(grupo);
            }

            return grupo;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    // http://localhost:8080/projeto/obter/1
    @RequestMapping("/grupo/obter/{id}")
    @ResponseBody
    public Object get(HttpServletResponse response, @PathVariable Long id)
    {
        try {
            Grupo grupo = grupoService.findById(id);
            if(grupo == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                throw new GrupoException("Grupo não encontrado.");
            }
            return grupo;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    // http://localhost:8080/projeto/listar
    @RequestMapping("/grupo/listar")
    @ResponseBody
    public List<Grupo> getlist()
    {
        List<Grupo> ret = grupoService.findAll();
        return ret;
    }
}
