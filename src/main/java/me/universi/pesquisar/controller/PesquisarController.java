package me.universi.pesquisar.controller;

import me.universi.group.entities.Group;
import me.universi.group.services.GroupService;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.PerfilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PesquisarController {
    @Autowired
    private PerfilService perfilService;

    @Autowired
    public GroupService grupoService;

    @PostMapping(value = "/pesquisar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ArrayList<Object> perfil_pesquisar(@RequestBody Map<String, Object> body, HttpServletRequest request, HttpSession session) {

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
                Collection<Profile> profileSearches = perfilService.findTop5ByNomeContainingIgnoreCase(term);
                for (Profile perfNow : profileSearches) {
                    HashMap<String, Object> perfilDic = new HashMap<>();
                    perfilDic.put("value", perfNow.getFirstname());
                    perfilDic.put("id", perfNow.getUsuario().getUsername());
                    perfilDic.put("img", perfNow.getImage() == null ? "https://i.imgur.com/vUBrCxr.png" : perfNow.getImage());
                    perfilDic.put("url", "/p/" + perfNow.getUsuario().getUsername());
                    perfilDic.put("tipo", "perfil");
                    resultsBusca.add(perfilDic);
                }
            }
            if(searchGrupo) {
                Collection<Group> grupoSearch = grupoService.findTop5ByNameContainingIgnoreCase(term);
                for (Group grupoNow : grupoSearch) {
                    HashMap<String, Object> grupoDic = new HashMap<>();
                    grupoDic.put("value", grupoNow.getName());
                    grupoDic.put("id", grupoNow.getNickname());
                    grupoDic.put("img", grupoNow.getImage() == null ? "https://i.imgur.com/SfAl1Vb.png" : grupoNow.getImage());
                    grupoDic.put("url", grupoService.getGroupPath(grupoNow.getId()));
                    grupoDic.put("tipo", "grupo");
                    resultsBusca.add(grupoDic);
                }
            }
        }

        return resultsBusca;
    }
}
