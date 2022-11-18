package me.universi.grupo.services;

import me.universi.grupo.entities.Grupo;
import me.universi.grupo.exceptions.GrupoException;
import me.universi.grupo.repositories.GrupoRepository;
import me.universi.perfil.entities.Perfil;
import me.universi.usuario.entities.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GrupoService {
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

    public boolean verificarPermissaoParaGrupo(Grupo grupo, Usuario usuario) throws GrupoException {

        if (grupo == null) {
            throw new GrupoException("Grupo não encontrado.");
        }

        if (usuario == null) {
            throw new GrupoException("Usuário não encontrado.");
        }

        Perfil perfil = usuario.getPerfil();
        if (perfil == null) {
            //throw new GrupoException("Você precisa criar um Perfil.");
        } else if(perfil.getId()!=0 && grupo.getAdmin().getId() != perfil.getId()) {
            //throw new GrupoException("Apenas administradores podem editar seus grupos!");
        }

        return true;
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
        }
    }

    public void adicionarParticipante(Grupo grupo, Perfil perfil) {
        Collection<Perfil> participantesArr = grupo.getParticipantes();
        if(participantesArr == null) {
            participantesArr = new ArrayList<Perfil>();
        }
        if(!participantesArr.contains(perfil)) {
            participantesArr.add(perfil);
            grupo.setParticipantes(participantesArr);
            this.save(grupo);
        }
    }

    public void removerParticipante(Grupo grupo, Perfil perfil) {
        Collection<Perfil> participantesArr = grupo.getParticipantes();
        if(participantesArr == null) {
            participantesArr = new ArrayList<Perfil>();
        }
        if(participantesArr.contains(perfil)) {
            participantesArr.remove(perfil);
            grupo.setParticipantes(participantesArr);
            this.save(grupo);
        }
    }

    public boolean nicknameDisponivelParaGrupo(Grupo grupo, String nickname) {
        boolean disponivel = true;
        try {
            String nicknameLower = nickname.toLowerCase();

            String[] palavrasReservadas = new String[] {
                    "admin",
                    "adicionar",
                    "remover",
                    "conta",
                    "grupo",
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
        }catch (Exception e) {
            disponivel = false;
        }
        return disponivel;
    }

    public void save(Grupo grupo) {
        grupoRepository.save(grupo);
    }

    public void delete(Grupo grupo) {
        grupoRepository.delete(grupo);
    }

    public List<Grupo> findAll() {
        return grupoRepository.findAll();
    }

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

}
