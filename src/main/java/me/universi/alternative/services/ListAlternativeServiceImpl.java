package me.universi.alternative.services;

import me.universi.alternative.AlternativeRepository;
import me.universi.alternative.entities.Alternative;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListAlternativeServiceImpl implements ListAlternativeService {

    private final AlternativeRepository alternativeRepository;

    @Autowired
    public ListAlternativeServiceImpl(AlternativeRepository alternativeRepository) {
        this.alternativeRepository = alternativeRepository;
    }

    public List<Alternative> listAlternative(Long userId, Long questionId){
        return alternativeRepository.findAllByQuestionIdAndQuestionUserCreateId(questionId, userId);
    }
}
