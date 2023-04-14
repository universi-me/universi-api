package me.universi.question.services;

import me.universi.question.QuestionRepository;
import me.universi.question.dto.QuestionUpdateDTO;
import me.universi.question.entities.Question;
import me.universi.question.exceptions.QuestionNotfoundException;
import me.universi.user.entities.User;
import me.universi.user.exceptions.UserNotFoundException;
import me.universi.user.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UpdateQuestionServiceImpl implements UpdateQuestionService {
    private final QuestionRepository questionRepository;
    private final UsuarioRepository userRepository;

    @Autowired
    public UpdateQuestionServiceImpl(QuestionRepository questionRepository, UsuarioRepository userRepository) {
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Question updateQuestion(Long userId, Long questionId, QuestionUpdateDTO questionUpdateDto) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Question question = questionRepository.findById(questionId).orElseThrow(QuestionNotfoundException::new);

        questionUpdate(question, questionUpdateDto);

        return questionRepository.save(question);
    }

    public void questionUpdate(Question question, QuestionUpdateDTO questionUpdateDto){
        question.setTitle(question.getTitle());
        question.setFeedback(questionUpdateDto.getFeedback());
    }
}
