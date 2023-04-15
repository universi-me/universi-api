package me.universi.grupo.services;

import me.universi.grupo.entities.Group;
import me.universi.grupo.exceptions.GroupException;
import me.universi.grupo.repositories.GrupoRepository;
import me.universi.perfil.entities.Perfil;
import me.universi.user.entities.User;
import me.universi.user.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GrupoService {
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private GrupoRepository grupoRepository;

    public Group findFirstById(Long id) {
        Optional<Group> grupoOptional = grupoRepository.findFirstById(id);
        if(grupoOptional.isPresent()){
            return grupoOptional.get();
        }else{
            return null;
        }
    }

    public Long findGrupoPaiDoGrupo(Long id) {
        Optional<Long> grupoOptional = grupoRepository.findGrupoIdPaiDoGrupoId(id);
        if(grupoOptional.isPresent()){
            return grupoOptional.get();
        }else{
            return null;
        }
    }

    public Group findFirstByNickname(String nickname) {
        Optional<Group> grupoOptional = grupoRepository.findFirstByNickname(nickname);
        if(grupoOptional.isPresent()){
            return grupoOptional.get();
        }else{
            return null;
        }
    }

    public Group findFirstByGrupoRootAndNickname(boolean grupoRoot, String nickname) {
        Optional<Group> grupoOptional = grupoRepository.findFirstByGrupoRootAndNickname(grupoRoot, nickname);
        if(grupoOptional.isPresent()){
            return grupoOptional.get();
        }else{
            return null;
        }
    }

    public List<Group> findByGrupoPublico(boolean grupoPublico) {
        List<Group> grupoOptional = grupoRepository.findByGrupoPublico(grupoPublico);
        return grupoOptional;
    }

    public long count() {
        try {
            return grupoRepository.count();
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean verificarPermissaoParaGrupo(Group grupo, User user) throws GroupException {

        if (grupo == null) {
            throw new GroupException("Grupo não encontrado.");
        }

        if (user == null) {
            throw new GroupException("Usuário não encontrado.");
        }

        Perfil perfil = user.getPerfil();
        if (usuarioService.usuarioPrecisaDePerfil(user)) {
            throw new GroupException("Você precisa criar um Perfil.");
        } else if(perfil.getId()!=0 && grupo.getAdmin().getId() != perfil.getId()) {
            if(!usuarioService.isContaAdmin(user)) {
                throw new GroupException("Apenas administradores podem editar seus grupos!");
            }
        }

        return true;
    }

    public boolean temPermissaoParaGrupo(Group grupo, User user) {
        try {
            return verificarPermissaoParaGrupo(grupo, user);
        } catch (Exception e) {
            return false;
        }
    }

    public void adicionarSubgrupo(Group grupo, Group sub) {
        Collection<Group> grupoArr = grupo.getSubGroups();
        if(grupoArr == null) {
            grupoArr = new ArrayList<Group>();
        }
        if(!grupoArr.contains(sub)) {
            grupoArr.add(sub);
            grupo.setSubGroups(grupoArr);
            this.save(grupo);
        }
    }

    public void removerSubgrupo(Group grupo, Group sub) {
        Collection<Group> grupoArr = grupo.getSubGroups();
        if(grupoArr == null) {
            grupoArr = new ArrayList<Group>();
        }
        if(grupoArr.contains(sub)) {
            grupoArr.remove(sub);
            grupo.setSubGroups(grupoArr);
            this.save(grupo);
            this.delete(sub);
        }
    }

    public boolean adicionarParticipante(Group grupo, Perfil perfil) throws GroupException {
        if(perfil == null) {
            throw new GroupException("Parametro Perfil é nulo.");
        }
        Collection<Perfil> participantesArr = grupo.getParticipants();
        if(participantesArr == null) {
            participantesArr = new ArrayList<Perfil>();
        }
        Perfil participante = obterParticipanteNoGrupo(grupo, perfil.getId());
        if(participante == null) {
            participantesArr.add(perfil);
            grupo.setParticipants(participantesArr);
            this.save(grupo);
            return true;
        }
        return false;
    }

    public boolean removerParticipante(Group grupo, Perfil perfil) throws GroupException {
        if(perfil == null) {
            throw new GroupException("Parametro Perfil é nulo.");
        }
        Collection<Perfil> participantesArr = grupo.getParticipants();
        if(participantesArr == null) {
            participantesArr = new ArrayList<Perfil>();
        }
        Perfil participante = obterParticipanteNoGrupo(grupo, perfil.getId());
        if(participante != null) {
            participantesArr.remove(participante);
            grupo.setParticipants(participantesArr);
            this.save(grupo);
            return true;
        }
        return false;
    }

    public Perfil obterParticipanteNoGrupo(Group grupo, Long idParticipante)
    {
        if(idParticipante != null && grupo.getParticipants() != null) {
            for (Perfil participanteNow : grupo.getParticipants()) {
                if (participanteNow.getId() == idParticipante) {
                    return participanteNow;
                }
            }
        }
        return null;
    }

    public boolean nicknameDisponivelParaGrupo(Group grupo, String nickname) {
        boolean disponivel = true;
        try {
            String nicknameLower = nickname.toLowerCase();

            String[] palavrasReservadas = new String[] {
                    "admin",
                    "rem-participante",
                    "add-participante",
                    "participantes",
                    "adicionar",
                    "remover",
                    "conta",
                    "grupo",
                    "grupos",
                    "editar",
                    "criar",
                    "obter",
                    "listar",
                    "competencia",
                    "atualizar",
                    "recomendacao",
                    "perfil",
                    "registrar",
                    "login",
                    "logout",
                    "login.js",
                    "usuario",
                    ""
            };

            if(Arrays.asList(palavrasReservadas).contains(nicknameLower)) {
                disponivel = false;
            }

            if(disponivel) {
                for (Group grupoNow : grupo.getSubGroups()) {
                    if (grupoNow.nickname.toLowerCase().equals(nicknameLower)) {
                        disponivel = false;
                        break;
                    }
                }
            }

            if(disponivel) {
                disponivel = nicknameRegex(nickname);
            }

        }catch (Exception e) {
            disponivel = false;
        }
        return disponivel;
    }

    public boolean nicknameRegex(String nickname) {
        String nicknameRegex = "^[a-z0-9_-]+$";
        Pattern emailPattern = Pattern.compile(nicknameRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = emailPattern.matcher(nickname);
        return matcher.find();
    }

    public void save(Group grupo) {
        grupoRepository.saveAndFlush(grupo);
    }

    public void delete(Group grupo) {
        this.delete_recursive(grupo, false);
    }

    private void delete_recursive(Group grupo, boolean insideRecursive) {

        if(grupo.participants != null) {
            grupo.participants.clear();
        }

        if(grupo.subGroups != null) {
            for(Group gNow : grupo.subGroups) {
                this.delete_recursive(gNow, true);
            }
        }

        // não executar quando estiver na recursao, conflita na remoção dos subgrupos acima.
        if(!insideRecursive) {
            Long paiId = this.findGrupoPaiDoGrupo(grupo.getId());
            if (paiId != null) {
                Group gPai = findFirstById(paiId);
                if (gPai.subGroups != null) {
                    gPai.subGroups.remove(grupo);
                    grupoRepository.save(gPai);
                }
            }
        }

        grupoRepository.delete(grupo);
    }

    public List<Group> findAll() {
        return grupoRepository.findAll();
    }

    // verifica se o usuario está acessando a url do grupo corretamente, apartir do grupo root/master ate seu subgrupo
    public Group parentescoCheckGrupo(Group grupoRoot, String[] sequenciaNickArr) {
        Group finalGrupo = null;

        boolean parenteCkeckFalhou = false;
        try {
            Group grupoInsta = grupoRoot;
            for (int i = 0; i < sequenciaNickArr.length; i++) {
                String nicknameNow = sequenciaNickArr[i];
                if (nicknameNow == null || nicknameNow.length() == 0) {
                    continue;
                }
                if (i == 1) {
                    // ignorar o primeiro, ja verificou antes
                    continue;
                }
                Group sub = null;
                for (Group grupoNow : grupoInsta.subGroups) {
                    if (nicknameNow.equals(grupoNow.nickname.toLowerCase())) {
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
            finalGrupo = grupoInsta;
        }catch (Exception e){
            e.printStackTrace();
        }
        if(!parenteCkeckFalhou) {
            return finalGrupo;
        }
        return null;
    }

    // retornar url do grupo a partir do id do grupo
    public String diretorioParaGrupo(Long grupoId) {
        ArrayList<String> nickArr = new ArrayList<String>();
        Group grupoCurr = findFirstById(grupoId);
        while(grupoCurr != null) {
            nickArr.add(grupoCurr.nickname);
            grupoCurr = findFirstById(findGrupoPaiDoGrupo(grupoCurr.getId()));
        }
        Collections.reverse(nickArr);
        return "/" + String.join("/", nickArr);
    }

    // pesquisar os 5 primeiros contendo a string maiusculo ou minusculo
    public Collection<Group> findTop5ByNomeContainingIgnoreCase(String nome){ return grupoRepository.findTop5ByNomeContainingIgnoreCase(nome); }
}
