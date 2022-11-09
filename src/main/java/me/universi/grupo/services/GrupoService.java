package me.universi.grupo.services;

import me.universi.grupo.entities.Grupo;
import me.universi.grupo.repositories.GrupoRepository;
import me.universi.perfil.entities.Perfil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class GrupoService {
    @Autowired
    private GrupoRepository grupoRepository;

    public Grupo findById(Long id) {
        Optional<Grupo> grupoOptional = grupoRepository.findById(id);
        if(grupoOptional.isPresent()){
            return grupoOptional.get();
        }else{
            return null;
        }
    }

    public Grupo findByNickname(String nickname) {
        Optional<Grupo> grupoOptional = grupoRepository.findByNickname(nickname);
        if(grupoOptional.isPresent()){
            return grupoOptional.get();
        }else{
            return null;
        }
    }

    public void adicionarSubgrupo(Grupo grupo, Grupo sub) {
        Collection<Grupo> grupoArr = grupo.getSubGrupos();
        if(grupoArr == null) {
            grupoArr = new ArrayList<Grupo>();
        }
        grupoArr.add(sub);
        grupo.setSubGrupos(grupoArr);
        this.save(grupo);
    }

    public void removerSubgrupo(Grupo grupo, Grupo sub) {
        Collection<Grupo> grupoArr = grupo.getSubGrupos();
        if(grupoArr == null) {
            grupoArr = new ArrayList<Grupo>();
        }
        grupoArr.remove(sub);
        grupo.setSubGrupos(grupoArr);
        this.save(grupo);
    }

    public void adicionarParticipante(Grupo grupo, Perfil perfil) {
        Collection<Perfil> participantesArr = grupo.getParticipantes();
        if(participantesArr == null) {
            participantesArr = new ArrayList<Perfil>();
        }
        participantesArr.add(perfil);
        grupo.setParticipantes(participantesArr);
        this.save(grupo);
    }

    public void removerParticipante(Grupo grupo, Perfil perfil) {
        Collection<Perfil> participantesArr = grupo.getParticipantes();
        if(participantesArr == null) {
            participantesArr = new ArrayList<Perfil>();
        }
        participantesArr.remove(perfil);
        grupo.setParticipantes(participantesArr);
        this.save(grupo);
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

}
