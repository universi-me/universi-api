package me.universi.alternative;

import me.universi.alternative.entities.Alternative;
import me.universi.question.builder.QuestionBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AlternativeBuilder {

    public static Alternative createAlternative(){
        Alternative alternative = new Alternative();
        alternative.setCorrect(true);
        UUID uuid_1 = UUID.fromString("47e2cc9e-69be-4482-bd90-1832ec403018");
        alternative.setId(uuid_1);
        alternative.setTitle("Alternative 1");
        alternative.setQuestion(QuestionBuilder.createQuestion());

        return alternative;
    }

    public static List<Alternative> createListAlternatives(){
        List<Alternative> alternatives = new ArrayList<>();

        UUID uuid_2 = UUID.fromString("626370e9-b1ff-4b2d-baf8-b6b8ba04f603");
        UUID uuid_3 = UUID.fromString("1fade783-e4b9-4e22-87a0-e5ca2f3c51fd");
        UUID uuid_4 = UUID.fromString("bdef7e08-e4c2-49f4-b446-3f252800229c");
        UUID uuid_5 = UUID.fromString("b1f7768d-0141-4f20-8b8b-9a479fbf2247");

        Alternative alternative2 = AlternativeBuilder.createAlternative();
        alternative2.setId(uuid_2);
        alternative2.setCorrect(false);

        Alternative alternative3 = AlternativeBuilder.createAlternative();
        alternative3.setId(uuid_3);
        alternative3.setCorrect(false);

        Alternative alternative4 = AlternativeBuilder.createAlternative();
        alternative4.setId(uuid_4);
        alternative4.setCorrect(false);

        Alternative alternative5 = AlternativeBuilder.createAlternative();
        alternative5.setId(uuid_5);
        alternative5.setCorrect(false);

        alternatives.add(AlternativeBuilder.createAlternative());
        alternatives.add(alternative2);
        alternatives.add(alternative3);
        alternatives.add(alternative4);
        alternatives.add(alternative5);

        return alternatives;
    }
}
