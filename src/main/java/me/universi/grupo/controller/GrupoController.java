package me.universi.grupo.controller;

import me.universi.grupo.entities.Grupo;
import me.universi.grupo.repositories.GrupoRepository;
import me.universi.usuario.entities.Usuario;
import me.universi.usuario.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Controller
public class GrupoController
{
    @Autowired
    public GrupoRepository grupoRepository;

    @Autowired
    public UsuarioRepository usuarioRepository;

    // mapaear tudo exceto, /css, /js, /img, /favicon.ico, comflita com static resources do Thymeleaf
    @GetMapping(value = {"{url:(?!css$|js$|img$|favicon.ico$).*}/**"})
    public String grupo_handler(HttpServletRequest request, HttpSession session, ModelMap map)
    {
        String username = request.getRemoteUser();
        Usuario usuario = usuarioRepository.findByEmail(username).get();
        map.addAttribute("usuario", usuario);

        String requestPathSt = request.getRequestURI().toLowerCase();
        String[] nicknameArr = requestPathSt.split("/");

        boolean grupoValido = false;

        Optional<Grupo> grupoRootOpt = grupoRepository.findByNickname(nicknameArr[1]);
        if(grupoRootOpt.isPresent()) {
            Grupo grupoRoot = grupoRootOpt.get();
            grupoValido = grupoRoot.isGrupoRoot() && !parentescoInvalido(grupoRoot, nicknameArr);
        }

        if(grupoValido) {
            Optional<Grupo> grupoAtualOpt = grupoRepository.findByNickname(nicknameArr[nicknameArr.length - 1]);
            if (grupoAtualOpt.isPresent()) {
                Grupo grupoAtual = grupoAtualOpt.get();
                map.addAttribute("grupo", grupoAtual);
            }
        } else {
            map.put("error", "Grupo não foi encontrado!");
        }

        return "grupo";
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
    public String create(@RequestParam("nome") String nome, @RequestParam("descricao") String descricao)
    {
        Grupo grupoNew = new Grupo();
        grupoRepository.save(grupoNew);
        return "Grupo Criado: "+ grupoNew.toString();
    }

    // http://localhost:8080/projeto/remover?id=1
    @RequestMapping("/grupo/remover")
    @ResponseBody
    public String remove(@RequestParam("id") Long id)
    {
        try {
            Grupo proj = grupoRepository.findById(id).get();
            if (proj != null) {
                grupoRepository.delete(proj);
                return "Grupo Removido: " + proj.toString();
            }
        }catch (EntityNotFoundException e) {
            return "Grupo não encontrado";
        }
        return "Falha ao remover";
    }

    // http://localhost:8080/projeto/obter?id=1
    @RequestMapping("/grupo/obter/{id}")
    @ResponseBody
    public Grupo get(@PathVariable Long id)
    {
        try {
            Grupo proj = grupoRepository.findById(id).get();
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
        List<Grupo> ret = grupoRepository.findAll();
        return ret;
    }
}
