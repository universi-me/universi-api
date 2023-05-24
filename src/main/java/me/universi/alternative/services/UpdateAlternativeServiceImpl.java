package me.universi.alternative.services;


import jakarta.validation.GroupDefinitionException;
import me.universi.alternative.AlternativeRepository;
import me.universi.alternative.dto.AlternativeUpdateDTO;
import me.universi.alternative.entities.Alternative;
import me.universi.alternative.exceptions.AlternativeNotFoundException;
import me.universi.group.entities.Group;
import me.universi.group.repositories.GroupRepository;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import me.universi.util.ExerciseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateAlternativeServiceImpl implements UpdateAlternativeService {

    private final AlternativeRepository alternativeRepository;
    private final UserService userService;
    private final GroupRepository groupRepository;

    @Autowired
    public UpdateAlternativeServiceImpl(AlternativeRepository alternativeRepository, UserService userService, GroupRepository groupRepository) {
        this.alternativeRepository = alternativeRepository;
        this.userService = userService;
        this.groupRepository = groupRepository;
    }


    @Override
    public Alternative updateAlternative(Long groupId, Long exerciseId, Long questionId, Long alternativeId, AlternativeUpdateDTO alternativeUpdateDTO) {
        User user = this.userService.getUserInSession();
        Group group = this.groupRepository.findByIdAndAdminId(groupId, user.getProfile().getId())
                .orElseThrow(GroupDefinitionException::new);
        ExerciseUtil.checkPermissionExercise(user, group);

        Alternative alternative = this.alternativeRepository.findAlternativeByIdAndQuestionIdAndQuestionUserCreateId(
                alternativeId,
                questionId,
                user.getId())
                .orElseThrow(AlternativeNotFoundException::new);

        updateAlternative(alternative, alternativeUpdateDTO);

        return this.alternativeRepository.save(alternative);

    }

    protected void updateAlternative(Alternative alternative, AlternativeUpdateDTO alternativeUpdateDTO){
        alternative.setTitle(alternativeUpdateDTO.getTitle());
        alternative.setCorrect(alternativeUpdateDTO.getCorrect());
    }
}
