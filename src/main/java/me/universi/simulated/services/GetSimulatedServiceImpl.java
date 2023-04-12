package me.universi.simulated.services;

import me.universi.alternative.AlternativeRepository;
import me.universi.alternative.entities.Alternative;
import me.universi.question.QuestionRepository;
import me.universi.question.entities.Question;
import me.universi.simulated.dto.QuestionWithAlternativesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GetSimulatedServiceImpl implements GetSimulatedService {

    private final AlternativeRepository alternativeRepository;
    private final QuestionRepository questionRepository;

    @Autowired
    public GetSimulatedServiceImpl(AlternativeRepository alternativeRepository, QuestionRepository questionRepository) {
        this.alternativeRepository = alternativeRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public List<QuestionWithAlternativesDTO> getQuestionsWithAlternatives(int amount) {
        List<Question> questions = questionRepository.findAllRandonAndLimited(amount);
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
