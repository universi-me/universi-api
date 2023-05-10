package me.universi.exercise.services;

import me.universi.alternative.AlternativeRepository;
import me.universi.alternative.entities.Alternative;
import me.universi.group.entities.Group;
import me.universi.group.exceptions.GroupNotFoundException;
import me.universi.group.repositories.GroupRepository;
import me.universi.question.QuestionRepository;
import me.universi.question.entities.Question;
import me.universi.exercise.dto.QuestionWithAlternativesDTO;
import me.universi.user.entities.User;
import me.universi.user.exceptions.UnauthorizedException;
import me.universi.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ListQuestionsWithAlternativesServiceImpl implements ListQuestionsWithAlternativesService {

    private final AlternativeRepository alternativeRepository;
    private final QuestionRepository questionRepository;
    private final GroupRepository groupRepository;
    private final UserService userService;

    @Autowired
    public ListQuestionsWithAlternativesServiceImpl(AlternativeRepository alternativeRepository, QuestionRepository questionRepository, GroupRepository groupRepository, UserService userService) {
        this.alternativeRepository = alternativeRepository;
        this.questionRepository = questionRepository;
        this.groupRepository = groupRepository;
        this.userService = userService;
    }

    @Override
    public List<QuestionWithAlternativesDTO> getQuestionsWithAlternatives(Long groupId, Long exerciseId, int amount) {

        User user = this.userService.obterUsuarioNaSessao();
        Group group = this.groupRepository.findById(groupId).orElseThrow(GroupNotFoundException::new);
        Boolean isParticipant = this.groupRepository.existsByIdAndParticipantsId(groupId, user.getProfile().getId());

        // Somente participantes do grupo e administradores podem visualizar e listar
        if(!group.getAdmin().getId().equals(user.getProfile().getId()) || !isParticipant){
            throw new UnauthorizedException();
        }

        List<Question> questions = questionRepository.findAllRandonAndLimited(exerciseId, amount);
        List <Long> ids = questions.stream().map(Question::getId).toList();
        List<Alternative> alternatives = alternativeRepository.findAllByQuestionWithAlternatives(ids);

        List<QuestionWithAlternativesDTO> questionWithAlternatives = new ArrayList<>();

        for (Question question : questions){
            QuestionWithAlternativesDTO questionComplete = new QuestionWithAlternativesDTO();
            questionComplete.setQuestion(question);

            // TODO   aprimorar código
            for (Alternative alternative : alternatives){
                if (alternative.getQuestion().getId().equals(question.getId())){
                    questionComplete.getAlternatives().add(alternative);
                }
            }
            questionWithAlternatives.add(questionComplete);
        }

        return questionWithAlternatives;
    }
}
