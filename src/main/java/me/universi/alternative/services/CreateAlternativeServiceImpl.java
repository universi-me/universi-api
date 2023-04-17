package me.universi.alternative.services;


import me.universi.alternative.AlternativeRepository;
import me.universi.alternative.dto.AlternativeCreateDTO;
import me.universi.alternative.entities.Alternative;
import me.universi.question.QuestionRepository;
import me.universi.question.entities.Question;
import me.universi.question.exceptions.QuestionNotfoundException;
import me.universi.user.entities.User;
import me.universi.user.exceptions.UserNotFoundException;
import me.universi.user.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateAlternativeServiceImpl implements CreateAlternativeService {

    private final AlternativeRepository alternativeRepository;
    private final QuestionRepository questionRepository;
    private final UsuarioRepository userRepository;

    @Autowired
    public CreateAlternativeServiceImpl(AlternativeRepository alternativeRepository, QuestionRepository questionRepository, UsuarioRepository userRepository) {
        this.alternativeRepository = alternativeRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
    }

    public Alternative createAlternative(Long userId, Long questionId, AlternativeCreateDTO alternative){
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Question question = questionRepository.findById(questionId).orElseThrow(QuestionNotfoundException::new);

        alternative.setQuestion(question);
        return alternativeRepository.save(Alternative.from(alternative));
    }
}
