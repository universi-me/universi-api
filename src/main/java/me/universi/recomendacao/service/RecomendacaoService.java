package me.universi.recomendacao.service;

import me.universi.recomendacao.entities.Recomendacao;
import me.universi.recomendacao.repositories.RecomendacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecomendacaoService {
    @Autowired
    private RecomendacaoRepository recomendacaoRepository;
    public Recomendacao findById(Long id) {
        Optional <Recomendacao> recomendacaoOptional = recomendacaoRepository.findById(id);
        if(recomendacaoOptional.isPresent()){
            return recomendacaoOptional.get();
        }else{
            return null;
            //TODO - IMPLEMENTAR EXCEPTION AO NÃO ENCONTRAR
        }
    }

    public void save(Recomendacao recomendacao) {
        //TODO - VALIDAÇÃO
        if(this.validar()){
            recomendacaoRepository.save(recomendacao);
        }
        //TODO - IMPLEMENTAR EXCEPTIONS
    }

    public List<Recomendacao> findAll() {
        return recomendacaoRepository.findAll();
    }

    public void delete(Recomendacao recomendacao) {
        recomendacaoRepository.delete(recomendacao);
    }

    public boolean validar(){
        //TODO - VALIDAR ATRIBUTOS
        return true;
    }

    public void update(Recomendacao recomendacao) {
        //TODO - VALIDAÇÃO
        if(this.validar()){
            recomendacaoRepository.save(recomendacao);
        }
        //TODO - IMPLEMENTAR EXCEPTIONS
    }
}
