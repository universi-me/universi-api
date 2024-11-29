package me.universi.question.services;

import me.universi.group.entities.Group;
import me.universi.group.exceptions.GroupNotFoundException;
import me.universi.group.repositories.GroupRepository;
import me.universi.question.QuestionRepository;
import me.universi.question.dto.QuestionUpdateDTO;
import me.universi.question.entities.Question;
import me.universi.question.exceptions.QuestionNotfoundException;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import me.universi.util.ExerciseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
public class UpdateQuestionServiceImpl implements UpdateQuestionService {
    private final QuestionRepository questionRepository;
    private final UserService userService;
    private final GroupRepository groupRepository;

    @Autowired
    public UpdateQuestionServiceImpl(QuestionRepository questionRepository, UserService userService, GroupRepository groupRepository) {
        this.questionRepository = questionRepository;
        this.userService = userService;
        this.groupRepository = groupRepository;
    }

    @Override
    @Transactional
    public Question updateQuestion(UUID exerciseId, UUID groupId, UUID questionId, QuestionUpdateDTO questionUpdateDto) {
        User user = this.userService.getUserInSession();
        Group group = this.groupRepository.findFirstByIdAndAdminId(groupId,user.getProfile().getId()).orElseThrow(GroupNotFoundException::new);

        ExerciseUtil.checkPermissionExercise(user,group);

        Question question = questionRepository.findFirstByIdAndExercisesId(questionId,exerciseId).orElseThrow(QuestionNotfoundException::new);
        questionUpdate(question, questionUpdateDto);

        return questionRepository.save(question);
    }

    protected void questionUpdate(Question question, QuestionUpdateDTO questionUpdateDto){

        if(!questionUpdateDto.getTitle().equals(question.getTitle())){
            question.setTitle(questionUpdateDto.getTitle());
        }
    }
}
