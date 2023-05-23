package me.universi.alternative.services;

import me.universi.alternative.AlternativeRepository;
import me.universi.alternative.entities.Alternative;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListAlternativeServiceImpl implements ListAlternativeService {

    private final AlternativeRepository alternativeRepository;
    private final UserService userService;

    @Autowired
    public ListAlternativeServiceImpl(AlternativeRepository alternativeRepository, UserService userService) {
        this.alternativeRepository = alternativeRepository;
        this.userService = userService;
    }

    public List<Alternative> listAlternative(Long groupId, Long ExerciseId, Long questionId){
        User user = this.userService.obterUsuarioNaSessao();
        // To do
        return alternativeRepository.findAllByQuestionIdAndQuestionUserCreateId(questionId, user.getId());
    }
}
