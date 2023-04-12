package me.universi.grupo.services;

import me.universi.grupo.entities.Grupo;
import me.universi.grupo.exceptions.GrupoException;
import me.universi.grupo.repositories.GrupoRepository;
import me.universi.perfil.entities.Perfil;
import me.universi.usuario.entities.User;
import me.universi.usuario.services.UsuarioService;
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

    public Grupo findFirstById(Long id) {
        Optional<Grupo> grupoOptional = grupoRepository.findFirstById(id);
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

    public Grupo findFirstByNickname(String nickname) {
        Optional<Grupo> grupoOptional = grupoRepository.findFirstByNickname(nickname);
        if(grupoOptional.isPresent()){
            return grupoOptional.get();
        }else{
            return null;
        }
    }

    public Grupo findFirstByGrupoRootAndNickname(boolean grupoRoot, String nickname) {
        Optional<Grupo> grupoOptional = grupoRepository.findFirstByGrupoRootAndNickname(grupoRoot, nickname);
        if(grupoOptional.isPresent()){
            return grupoOptional.get();
        }else{
            return null;
        }
    }

    public List<Grupo> findByGrupoPublico(boolean grupoPublico) {
        List<Grupo> grupoOptional = grupoRepository.findByGrupoPublico(grupoPublico);
        return grupoOptional;
    }

    public long count() {
        try {
            return grupoRepository.count();
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean verificarPermissaoParaGrupo(Grupo grupo, User user) throws GrupoException {

        if (grupo == null) {
            throw new GrupoException("Grupo não encontrado.");
        }

        if (user == null) {
            throw new GrupoException("Usuário não encontrado.");
        }

        Perfil perfil = user.getPerfil();
        if (usuarioService.usuarioPrecisaDePerfil(user)) {
            throw new GrupoException("Você precisa criar um Perfil.");
        } else if(perfil.getId()!=0 && grupo.getAdmin().getId() != perfil.getId()) {
            if(!usuarioService.isContaAdmin(user)) {
                throw new GrupoException("Apenas administradores podem editar seus grupos!");
            }
        }

        return true;
    }

    public boolean temPermissaoParaGrupo(Grupo grupo, User user) {
        try {
            return verificarPermissaoParaGrupo(grupo, user);
        } catch (Exception e) {
            return false;
        }
    }

    public void adicionarSubgrupo(Grupo grupo, Grupo sub) {
        Collection<Grupo> grupoArr = grupo.getSubGrupos();
        if(grupoArr == null) {
            grupoArr = new ArrayList<Grupo>();
        }
        if(!grupoArr.contains(sub)) {
            grupoArr.add(sub);
            grupo.setSubGrupos(grupoArr);
            this.save(grupo);
        }
    }

    public void removerSubgrupo(Grupo grupo, Grupo sub) {
        Collection<Grupo> grupoArr = grupo.getSubGrupos();
        if(grupoArr == null) {
            grupoArr = new ArrayList<Grupo>();
        }
        if(grupoArr.contains(sub)) {
            grupoArr.remove(sub);
            grupo.setSubGrupos(grupoArr);
            this.save(grupo);
            this.delete(sub);
        }
    }

    public boolean adicionarParticipante(Grupo grupo, Perfil perfil) throws GrupoException {
        if(perfil == null) {
            throw new GrupoException("Parametro Perfil é nulo.");
        }
        Collection<Perfil> participantesArr = grupo.getParticipantes();
        if(participantesArr == null) {
            participantesArr = new ArrayList<Perfil>();
        }
        Perfil participante = obterParticipanteNoGrupo(grupo, perfil.getId());
        if(participante == null) {
            participantesArr.add(perfil);
            grupo.setParticipantes(participantesArr);
            this.save(grupo);
            return true;
        }
        return false;
    }

    public boolean removerParticipante(Grupo grupo, Perfil perfil) throws GrupoException {
        if(perfil == null) {
            throw new GrupoException("Parametro Perfil é nulo.");
        }
        Collection<Perfil> participantesArr = grupo.getParticipantes();
        if(participantesArr == null) {
            participantesArr = new ArrayList<Perfil>();
        }
        Perfil participante = obterParticipanteNoGrupo(grupo, perfil.getId());
        if(participante != null) {
            participantesArr.remove(participante);
            grupo.setParticipantes(participantesArr);
            this.save(grupo);
            return true;
        }
        return false;
    }

    public Perfil obterParticipanteNoGrupo(Grupo grupo, Long idParticipante)
    {
        if(idParticipante != null && grupo.getParticipantes() != null) {
            for (Perfil participanteNow : grupo.getParticipantes()) {
                if (participanteNow.getId() == idParticipante) {
                    return participanteNow;
                }
            }
        }
        return null;
    }

    public boolean nicknameDisponivelParaGrupo(Grupo grupo, String nickname) {
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
                for (Grupo grupoNow : grupo.getSubGrupos()) {
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

    public void save(Grupo grupo) {
        grupoRepository.saveAndFlush(grupo);
    }

    public void delete(Grupo grupo) {
        this.delete_recursive(grupo, false);
    }

    private void delete_recursive(Grupo grupo, boolean insideRecursive) {

        if(grupo.participantes != null) {
            grupo.participantes.clear();
        }

        if(grupo.subGrupos != null) {
            for(Grupo gNow : grupo.subGrupos) {
                this.delete_recursive(gNow, true);
            }
        }

        // não executar quando estiver na recursao, conflita na remoção dos subgrupos acima.
        if(!insideRecursive) {
            Long paiId = this.findGrupoPaiDoGrupo(grupo.getId());
            if (paiId != null) {
                Grupo gPai = findFirstById(paiId);
                if (gPai.subGrupos != null) {
                    gPai.subGrupos.remove(grupo);
                    grupoRepository.save(gPai);
                }
            }
        }

        grupoRepository.delete(grupo);
    }

    public List<Grupo> findAll() {
        return grupoRepository.findAll();
    }

    // verifica se o usuario está acessando a url do grupo corretamente, apartir do grupo root/master ate seu subgrupo
    public Grupo parentescoCheckGrupo(Grupo grupoRoot, String[] sequenciaNickArr) {
        Grupo finalGrupo = null;

        boolean parenteCkeckFalhou = false;
        try {
            Grupo grupoInsta = grupoRoot;
            for (int i = 0; i < sequenciaNickArr.length; i++) {
                String nicknameNow = sequenciaNickArr[i];
                if (nicknameNow == null || nicknameNow.length() == 0) {
                    continue;
                }
                if (i == 1) {
                    // ignorar o primeiro, ja verificou antes
                    continue;
                }
                Grupo sub = null;
                for (Grupo grupoNow : grupoInsta.subGrupos) {
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
        Grupo grupoCurr = findFirstById(grupoId);
        while(grupoCurr != null) {
            nickArr.add(grupoCurr.nickname);
            grupoCurr = findFirstById(findGrupoPaiDoGrupo(grupoCurr.getId()));
        }
        Collections.reverse(nickArr);
        return "/" + String.join("/", nickArr);
    }

    // pesquisar os 5 primeiros contendo a string maiusculo ou minusculo
    public Collection<Grupo> findTop5ByNomeContainingIgnoreCase(String nome){ return grupoRepository.findTop5ByNomeContainingIgnoreCase(nome); }
}
