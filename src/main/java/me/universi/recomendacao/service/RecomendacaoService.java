package me.universi.recomendacao.service;

import me.universi.competencia.entities.Competencia;
import me.universi.recomendacao.entities.Recomendacao;
import me.universi.recomendacao.exceptions.RecomendacaoInvalidaException;
import me.universi.recomendacao.repositories.RecomendacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecomendacaoService {
    @Autowired
    private RecomendacaoRepository recomendacaoRepository;

    public Recomendacao findFirstById(Long id) {
        Optional <Recomendacao> recomendacaoOptional = recomendacaoRepository.findFirstById(id);
        if(recomendacaoOptional.isPresent()){
            return recomendacaoOptional.get();
        }else{
            return null;
        }
    }
    public void save(Recomendacao recomendacao) throws RecomendacaoInvalidaException {
        if(this.validar(recomendacao)){
            recomendacaoRepository.saveAndFlush(recomendacao);
        }
    }
    public List<Recomendacao> findAll() {
        return recomendacaoRepository.findAll();
    }

    public void delete(Recomendacao recomendacao) {
        recomendacaoRepository.delete(recomendacao);
    }

    public long count() {
        try {
            return recomendacaoRepository.count();
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean validarPerfilOrigem(Recomendacao recomendacao) throws RecomendacaoInvalidaException {
        if(recomendacao.getOrigem() == null){
            throw new RecomendacaoInvalidaException("Perfil de origem inválido");
        }else{
            recomendacao.getOrigem().getId();
            return true;
        }
    }
    public boolean validarPerfilDestino(Recomendacao recomendacao) throws RecomendacaoInvalidaException {
        if(recomendacao.getDestino() == null){
            throw new RecomendacaoInvalidaException("O Perfil de destino da recomendação encontra-se vazio");
        }else{
            recomendacao.getDestino().getId();
            return true;
        }
    }
    public boolean validarCompetenciaValida(Recomendacao recomendacao) throws RecomendacaoInvalidaException {
        if(recomendacao.getCompetenciaTipo() == null) {
            throw new RecomendacaoInvalidaException("A competência da recomendação não foi escolhida");
        }return true;
    }
    public void update(Recomendacao recomendacao) throws RecomendacaoInvalidaException {
        if(this.validar(recomendacao)){
            recomendacaoRepository.saveAndFlush(recomendacao);
        }
    }
    private boolean validar(Recomendacao recomendacao) throws RecomendacaoInvalidaException {
        if(!validarPerfilOrigem(recomendacao) || !validarPerfilDestino(recomendacao) || !validarCompetenciaValida(recomendacao)){
            return false;
        }return true;
    }
}
