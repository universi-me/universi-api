package me.universi.question.services;

import me.universi.question.QuestionRepository;
import me.universi.user.entities.User;
import me.universi.user.exceptions.UserNotFoundException;
import me.universi.user.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DeleteQuestionServiceImpl implements DeleteQuestionService {
    private final QuestionRepository questionRepository;
    private final UsuarioRepository userRepository;

    @Autowired
    public DeleteQuestionServiceImpl(QuestionRepository questionRepository, UsuarioRepository userRepository) {
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void deleteQuestion(Long userId, Long questionId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        questionRepository.deleteById(questionId);
    }
}
