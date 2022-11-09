package me.universi.grupo.controller;

import me.universi.grupo.entities.Grupo;
import me.universi.grupo.enums.GrupoTipo;
import me.universi.grupo.services.GrupoService;
import me.universi.perfil.entities.Perfil;
import me.universi.usuario.entities.Usuario;
import me.universi.usuario.services.SecurityUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.List;

@Controller
public class GrupoController
{
    @Autowired
    public GrupoService grupoService;

    // mapaear tudo exceto, /css, /js, /img, /favicon.ico, comflita com static resources do Thymeleaf
    @GetMapping(value = {"{url:(?!css$|js$|img$|favicon.ico$).*}/**"})
    public String grupo_handler(HttpServletRequest request, HttpSession session, ModelMap map) {
        map.addAttribute("usuario", session.getAttribute("usuario"));

        String requestPathSt = request.getRequestURI().toLowerCase();
        String[] nicknameArr = requestPathSt.split("/");

        boolean grupoValido = false;

        Grupo grupoRoot = grupoService.findByNickname(nicknameArr[1]);
        if(grupoRoot != null) {
            grupoValido = grupoRoot.isGrupoRoot() && !parentescoInvalido(grupoRoot, nicknameArr);
        }

        if(grupoValido) {
            Grupo grupoAtual = grupoService.findByNickname(nicknameArr[nicknameArr.length - 1]);
            if (grupoAtual != null) {
                map.addAttribute("grupo", grupoAtual);
            }
        } else {
            map.put("error", "Grupo não foi encontrado!");
        }

        return "grupo/grupo";
    }

    /*
        Verificar o parentesco dos subgrupos
     */
    public boolean parentescoInvalido(Grupo grupoRoot, String[] sequenciaNickArr)
    {
        boolean parenteCkeckFalhou = false;
        Grupo grupoInsta = grupoRoot;
        for(int i = 0; i<sequenciaNickArr.length; i++)
        {
            String nicknameNow = sequenciaNickArr[i];
            if(nicknameNow==null || nicknameNow.length()==0) {
                continue;
            }
            if(i==1) {
                // ignorar o primeiro, ja verificou antes
                continue;
            }
            Grupo sub = null;
            for(Grupo grupoNow : grupoInsta.subGrupos) {
                if(nicknameNow.equals(grupoNow.nickname.toLowerCase())) {
                    sub = grupoNow;
                    break;
                }
            }
            if (sub != null) {
                grupoInsta = sub;
            } else {
                parenteCkeckFalhou = true;
                break;
            }
        }
        return parenteCkeckFalhou;
    }

    // http://localhost:8080/projeto/criar?nome=teste&descricao=teste2
    @RequestMapping("/grupo/criar")
    @ResponseBody
    public Grupo create(HttpServletRequest request, HttpSession session, @RequestParam("nickname") String nickname, @RequestParam("nome") String nome, @RequestParam("descricao") String descricao, @RequestParam("tipo") GrupoTipo tipo)
    {
        Grupo grupoNew = new Grupo();
        grupoNew.setNickname(nickname);
        grupoNew.setNome(nome);
        grupoNew.setDescricao(descricao);
        grupoNew.setTipo(tipo);

        //Usuario usuario = (Usuario)session.getAttribute("usuario");
        //Perfil perfil = usuario.getPerfil();
        //grupoNew.setAdmin(perfil);

        grupoService.save(grupoNew);
        return grupoNew;
    }

    // http://localhost:8080/projeto/remover?id=1
    @RequestMapping("/grupo/remover")
    @ResponseBody
    public String remove(@RequestParam("id") Long id)
    {
        try {
            Grupo proj = grupoService.findById(id);
            if (proj != null) {
                grupoService.delete(proj);
                return "Grupo Removido: " + proj.toString();
            }
        }catch (EntityNotFoundException e) {
            return "Grupo não encontrado";
        }
        return "Falha ao remover";
    }

    // http://localhost:8080/projeto/obter/1
    @RequestMapping("/grupo/obter/{id}")
    @ResponseBody
    public Grupo get(@PathVariable Long id)
    {
        try {
            Grupo proj = grupoService.findById(id);
            return proj;
        }catch (EntityNotFoundException e) {
            return null;
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
