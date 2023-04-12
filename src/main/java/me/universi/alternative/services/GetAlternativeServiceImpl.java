package me.universi.alternative.services;

import me.universi.alternative.AlternativeRepository;
import me.universi.alternative.entities.Alternative;
import me.universi.alternative.exceptions.AlternativeNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service

public class GetAlternativeServiceImpl implements GetAlternativeService {

    private final AlternativeRepository alternativeRepository;

    @Autowired
    public GetAlternativeServiceImpl(AlternativeRepository alternativeRepository) {
        this.alternativeRepository = alternativeRepository;
    }

    public Alternative getAlternative(Long userId, Long questionId, Long alternativeId){
        return alternativeRepository.findAlternativeByIdAndQuestionIdAndQuestionUserCreateId(alternativeId, questionId, userId)
                .orElseThrow(AlternativeNotFoundException::new);
    }
}
