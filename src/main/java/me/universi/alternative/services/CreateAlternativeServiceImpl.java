package me.universi.alternative.services;


import me.universi.alternative.AlternativeRepository;
import me.universi.alternative.dto.AlternativeCreateDTO;
import me.universi.alternative.entities.Alternative;
import me.universi.alternative.exceptions.MaxAlternativeException;
import me.universi.exercise.ExerciseRepository;
import me.universi.question.QuestionRepository;
import me.universi.question.entities.Question;
import me.universi.question.exceptions.QuestionNotfoundException;
import me.universi.user.entities.User;
import me.universi.user.exceptions.UnauthorizedException;
import me.universi.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateAlternativeServiceImpl implements CreateAlternativeService {

    private final AlternativeRepository alternativeRepository;
    private final QuestionRepository questionRepository;
    private final UserService userService;
    private final ExerciseRepository  exerciseRepository;

    private static final Integer MAX_ALTERNATIVES = 5;

    @Autowired
    public CreateAlternativeServiceImpl(AlternativeRepository alternativeRepository, QuestionRepository questionRepository, UserService userService, ExerciseRepository exerciseRepository) {
        this.alternativeRepository = alternativeRepository;
        this.questionRepository = questionRepository;
        this.userService = userService;
        this.exerciseRepository = exerciseRepository;
    }

    public Alternative createAlternative(Long groupId, Long exerciseId, Long questionId, AlternativeCreateDTO alternative){
        User user = this.userService.obterUsuarioNaSessao();
        Boolean exerciseExist = this.exerciseRepository.existsByIdAndGroupAdminId(exerciseId, user.getProfile().getId());
         if (!exerciseExist){
             throw new UnauthorizedException();
         }
        Question question = questionRepository.findByIdAndExercisesId(questionId, exerciseId).orElseThrow(QuestionNotfoundException::new);
        Integer amountAlternatives = this.alternativeRepository.countAlternativeByQuestionId(questionId);
        if (amountAlternatives + 1 > MAX_ALTERNATIVES ){
            throw new MaxAlternativeException();
        }

        alternative.setQuestion(question);
        return alternativeRepository.save(Alternative.from(alternative));
    }
}
