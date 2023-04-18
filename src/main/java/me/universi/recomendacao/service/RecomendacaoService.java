package me.universi.recomendacao.service;

import me.universi.recomendacao.entities.Recommendation;
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

    public Recommendation findFirstById(Long id) {
        Optional <Recommendation> recomendacaoOptional = recomendacaoRepository.findFirstById(id);
        if(recomendacaoOptional.isPresent()){
            return recomendacaoOptional.get();
        }else{
            return null;
        }
    }
    public void save(Recommendation recommendation) throws RecomendacaoInvalidaException {
        if(this.validar(recommendation)){
            recomendacaoRepository.saveAndFlush(recommendation);
        }
    }
    public List<Recommendation> findAll() {
        return recomendacaoRepository.findAll();
    }

    public void delete(Recommendation recommendation) {
        recomendacaoRepository.delete(recommendation);
    }

    public long count() {
        try {
            return recomendacaoRepository.count();
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean validarPerfilOrigem(Recommendation recommendation) throws RecomendacaoInvalidaException {
        if(recommendation.getOrigin() == null){
            throw new RecomendacaoInvalidaException("Perfil de origem inválido");
        }else{
            recommendation.getOrigin().getId();
            return true;
        }
    }
    public boolean validarPerfilDestino(Recommendation recommendation) throws RecomendacaoInvalidaException {
        if(recommendation.getDestiny() == null){
            throw new RecomendacaoInvalidaException("O Perfil de destino da recomendação encontra-se vazio");
        }else{
            recommendation.getDestiny().getId();
            return true;
        }
    }
    public boolean validarCompetenciaValida(Recommendation recommendation) throws RecomendacaoInvalidaException {
        if(recommendation.getCompetenceType() == null) {
            throw new RecomendacaoInvalidaException("A competência da recomendação não foi escolhida");
        }return true;
    }
    public void update(Recommendation recommendation) throws RecomendacaoInvalidaException {
        if(this.validar(recommendation)){
            recomendacaoRepository.saveAndFlush(recommendation);
        }
    }
    private boolean validar(Recommendation recommendation) throws RecomendacaoInvalidaException {
        if(!validarPerfilOrigem(recommendation) || !validarPerfilDestino(recommendation) || !validarCompetenciaValida(recommendation)){
            return false;
        }return true;
    }
}
