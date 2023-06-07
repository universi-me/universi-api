package me.universi.exercise.service;

import me.universi.IndicatorsBuilder;
import me.universi.alternative.AlternativeBuilder;
import me.universi.exercise.ExerciseRepository;
import me.universi.exercise.builder.ExerciseBuilder;
import me.universi.exercise.dto.AnswerDTO;
import me.universi.exercise.dto.ExerciseAnswersDTO;
import me.universi.exercise.entities.Exercise;
import me.universi.exercise.exception.ExerciseNotFoundException;
import me.universi.exercise.services.ValuerExerciseServiceImpl;
import me.universi.indicators.IndicatorsRepository;
import me.universi.indicators.entities.Indicators;
import me.universi.question.builder.QuestionBuilder;
import me.universi.user.UserBuilder;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("Service")
@ExtendWith(MockitoExtension.class)
public class ValuerExerciseServiceTest {
    @Mock
    private IndicatorsRepository indicatorsRepository;

    @Mock
    private UserService userService;

    @Mock
    private ExerciseRepository exerciseRepository;

    private ValuerExerciseServiceImpl valuerExerciseService;

    @BeforeEach
    public void setup() {
        this.valuerExerciseService = new ValuerExerciseServiceImpl(indicatorsRepository, userService, exerciseRepository);
    }

    @Test
    public void testExercisesAnswers() {
        Long groupId = 1L;
        Long exerciseId = 2L;

        User user = UserBuilder.createUser();

        Exercise exercise = ExerciseBuilder.createExercise();
        exercise.setId(exerciseId);

        List<AnswerDTO> answers = new ArrayList<>();
        AnswerDTO answer1 = new AnswerDTO();
        answer1.setQuestion(QuestionBuilder.createQuestion());
        answer1.setAlternativeSelected(AlternativeBuilder.createAlternative());
        answer1.getAlternativeSelected().setCorrect(true);
        answers.add(answer1);

        when(userService.getUserInSession()).thenReturn(user);
        when(exerciseRepository.findByIdAndGroupId(exerciseId, groupId)).thenReturn(Optional.of(exercise));
        when(indicatorsRepository.findByUserId(user.getId())).thenReturn(IndicatorsBuilder.createIndicators());

        ExerciseAnswersDTO result = valuerExerciseService.exercisesAnswers(groupId, exerciseId, answers);

        assertEquals(10, result.getScore());
        verify(userService).getUserInSession();
        verify(exerciseRepository).findByIdAndGroupId(exerciseId, groupId);
        verify(indicatorsRepository).findByUserId(user.getId());
        verify(indicatorsRepository).save(any(Indicators.class));
    }

    @Test
    public void testExercisesAnswersExerciseNotFound() {
        Long groupId = 1L;
        Long exerciseId = 2L;

        User user = new User();

        List<AnswerDTO> answers = new ArrayList<>();

        when(userService.getUserInSession()).thenReturn(user);
        when(exerciseRepository.findByIdAndGroupId(exerciseId, groupId)).thenReturn(Optional.empty());

        try {
            valuerExerciseService.exercisesAnswers(groupId, exerciseId, answers);
        } catch (ExerciseNotFoundException e) {
            assertEquals(ExerciseNotFoundException.class, e.getClass());
        }

        verify(userService).getUserInSession();
        verify(exerciseRepository).findByIdAndGroupId(exerciseId, groupId);
        verify(indicatorsRepository, never()).findByUserId(anyLong());
        verify(indicatorsRepository, never()).save(any(Indicators.class));
    }
}
