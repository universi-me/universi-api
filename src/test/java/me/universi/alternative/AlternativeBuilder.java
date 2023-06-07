package me.universi.alternative;

import me.universi.alternative.entities.Alternative;
import me.universi.question.builder.QuestionBuilder;

import java.util.ArrayList;
import java.util.List;

public class AlternativeBuilder {

    public static Alternative createAlternative(){
        Alternative alternative = new Alternative();
        alternative.setCorrect(true);
        alternative.setId(1L);
        alternative.setTitle("Alternative 1");
        alternative.setQuestion(QuestionBuilder.createQuestion());

        return alternative;
    }

    public static List<Alternative> createListAlternatives(){
        List<Alternative> alternatives = new ArrayList<>();

        Alternative alternative2 = AlternativeBuilder.createAlternative();
        alternative2.setId(2L);
        alternative2.setCorrect(false);

        Alternative alternative3 = AlternativeBuilder.createAlternative();
        alternative3.setId(3L);
        alternative3.setCorrect(false);

        Alternative alternative4 = AlternativeBuilder.createAlternative();
        alternative4.setId(4L);
        alternative4.setCorrect(false);

        Alternative alternative5 = AlternativeBuilder.createAlternative();
        alternative5.setId(5L);
        alternative5.setCorrect(false);

        alternatives.add(AlternativeBuilder.createAlternative());
        alternatives.add(alternative2);
        alternatives.add(alternative3);
        alternatives.add(alternative4);
        alternatives.add(alternative5);

        return alternatives;
    }
}
