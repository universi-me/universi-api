package me.universi.alternative.services;

import java.util.Optional;
import me.universi.alternative.AlternativeRepository;
import me.universi.alternative.entities.Alternative;
import me.universi.alternative.exceptions.AlternativeNotFoundException;
import me.universi.group.entities.Group;
import me.universi.group.exceptions.GroupNotFoundException;
import me.universi.group.repositories.GroupRepository;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service

public class DeleteAlternativeServiceImpl implements DeleteAlternativeService {

    private final AlternativeRepository alternativeRepository;
    private final UserService userService;
    private final GroupRepository groupRepository;

    @Autowired
    public DeleteAlternativeServiceImpl(AlternativeRepository alternativeRepository, UserService userService, GroupRepository groupRepository) {
        this.alternativeRepository = alternativeRepository;
        this.userService = userService;
        this.groupRepository = groupRepository;
    }

    public void deleteAlternative(UUID groupId, UUID exerciseId, UUID questionId, UUID alternativeId){
       User user = this.userService.getUserInSession();
       Group group = this.groupRepository.findFirstByIdAndAdminId(groupId, user.getProfile().getId()).orElseThrow(GroupNotFoundException::new);

       this.alternativeRepository.findAlternativeByIdAndQuestionIdAndQuestionProfileCreateId(alternativeId, questionId, user.getId())
                .orElseThrow(AlternativeNotFoundException::new);

        Optional<Alternative> alternative = alternativeRepository.findById(alternativeId);
        if(alternative.isPresent()) {
            alternative.get().setDeleted(true);
            alternativeRepository.save(alternative.get());
        }
    }
}
