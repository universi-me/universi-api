package me.universi.alternative.services;

import me.universi.alternative.AlternativeRepository;
import me.universi.alternative.entities.Alternative;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ListAlternativeServiceImpl implements ListAlternativeService {

    private final AlternativeRepository alternativeRepository;
    private final UserService userService;

    @Autowired
    public ListAlternativeServiceImpl(AlternativeRepository alternativeRepository, UserService userService) {
        this.alternativeRepository = alternativeRepository;
        this.userService = userService;
    }

    public List<Alternative> listAlternative(UUID groupId, UUID ExerciseId, UUID questionId){
        User user = this.userService.getUserInSession();
        // To do
        return alternativeRepository.findAllByQuestionIdAndQuestionProfileCreateId(questionId, user.getId());
    }
}
