package me.universi.question.services;

import me.universi.feedback.FeedbackRepository;
import me.universi.feedback.entities.Feedback;
import me.universi.question.QuestionRepository;
import me.universi.question.dto.QuestionCreateDTO;
import me.universi.question.entities.Question;
import me.universi.usuario.entities.User;
import me.universi.usuario.exceptions.UserNotFoundException;
import me.universi.usuario.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CreateQuestionServiceImpl implements CreateQuestionService {
    private final QuestionRepository questionRepository;
    private final UsuarioRepository userRepository;
    private final FeedbackRepository feedbackRepository;

    @Autowired
    public CreateQuestionServiceImpl(QuestionRepository questionRepository, UsuarioRepository userRepository, FeedbackRepository feedbackRepository) {
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.feedbackRepository = feedbackRepository;
    }

    public Question createQuestion(Long userId, QuestionCreateDTO questionCreateDTO){
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Feedback feedback = feedbackRepository.save(questionCreateDTO.getFeedback());

        questionCreateDTO.setUserCreate(user);
        questionCreateDTO.setFeedback(feedback);

        return questionRepository.save(Question.from(questionCreateDTO));
    }
}
