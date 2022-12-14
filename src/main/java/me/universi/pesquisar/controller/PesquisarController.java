package me.universi.pesquisar.controller;

import me.universi.grupo.entities.Grupo;
import me.universi.grupo.services.GrupoService;
import me.universi.perfil.entities.Perfil;
import me.universi.perfil.services.PerfilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PesquisarController {
    @Autowired
    private PerfilService perfilService;

    @Autowired
    public GrupoService grupoService;

    @ResponseBody
    @RequestMapping(value = "/pesquisar", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object perfil_pesquisar(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {

        // retorno dos resultados
        ArrayList<Object> resultsBusca = new ArrayList<>();

        // termo de pesquisa
        String term = (String)body.get("term");

        boolean searchUsuario = false;
        boolean searchGrupo = false;

        // filtros para a pesquisa
        if(body.get("filtro") != null) {
            ArrayList<String> filtro = (ArrayList<String>)(body.get("filtro"));
            searchUsuario = filtro.contains((String)"usuario");
            searchGrupo = filtro.contains((String)"grupo");
        }

        if(term != null && term.length() > 0) {

            if(searchUsuario) {
                Collection<Perfil> perfilSearch = perfilService.findTop5ByNomeContainingIgnoreCase(term);
                for (Perfil perfNow : perfilSearch) {
                    HashMap<String, Object> perfilDic = new HashMap<>();
                    perfilDic.put("value", perfNow.getNome());
                    perfilDic.put("id", perfNow.getUsuario().getUsername());
                    perfilDic.put("img", perfNow.getImagem() == null ? "https://i.imgur.com/vUBrCxr.png" : perfNow.getImagem());
                    perfilDic.put("url", "/p/" + perfNow.getUsuario().getUsername());
                    perfilDic.put("tipo", "perfil");
                    resultsBusca.add(perfilDic);
                }
            }
            if(searchGrupo) {
                Collection<Grupo> grupoSearch = grupoService.findTop5ByNomeContainingIgnoreCase(term);
                for (Grupo grupoNow : grupoSearch) {
                    HashMap<String, Object> grupoDic = new HashMap<>();
                    grupoDic.put("value", grupoNow.getNome());
                    grupoDic.put("id", grupoNow.getNickname());
                    grupoDic.put("img", grupoNow.getImagem() == null ? "https://i.imgur.com/SfAl1Vb.png" : grupoNow.getImagem());
                    grupoDic.put("url", grupoService.diretorioParaGrupo(grupoNow.getId()));
                    grupoDic.put("tipo", "grupo");
                    resultsBusca.add(grupoDic);
                }
            }
        }

        return resultsBusca;
    }
}
