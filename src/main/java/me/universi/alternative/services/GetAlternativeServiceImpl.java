package me.universi.alternative.services;

import me.universi.alternative.AlternativeRepository;
import me.universi.alternative.entities.Alternative;
import me.universi.alternative.exceptions.AlternativeNotFoundException;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service

public class GetAlternativeServiceImpl implements GetAlternativeService {

    private final AlternativeRepository alternativeRepository;
    private final UserService userService;

    @Autowired
    public GetAlternativeServiceImpl(AlternativeRepository alternativeRepository, UserService userService) {
        this.alternativeRepository = alternativeRepository;
        this.userService = userService;
    }

    public Alternative getAlternative(Long groupId, Long questionId, Long alternativeId){
       User user = this.userService.obterUsuarioNaSessao();

        return alternativeRepository.findAlternativeByIdAndQuestionIdAndQuestionUserCreateId(alternativeId, questionId, user.getId())
                .orElseThrow(AlternativeNotFoundException::new);
    }
}
