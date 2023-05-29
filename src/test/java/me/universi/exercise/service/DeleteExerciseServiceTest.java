package me.universi.exercise.service;

import me.universi.exercise.ExerciseRepository;
import me.universi.exercise.builder.ExerciseBuilder;
import me.universi.exercise.entities.Exercise;
import me.universi.exercise.exception.ExerciseNotFoundException;
import me.universi.exercise.services.DeleteExerciseServiceImpl;
import me.universi.group.builder.GroupBuilder;
import me.universi.group.entities.Group;
import me.universi.group.exceptions.GroupNotFoundException;
import me.universi.group.repositories.GroupRepository;
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

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("Service")
public class DeleteExerciseServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    private DeleteExerciseServiceImpl deleteExerciseService;

    @BeforeEach
    public void setup() {
        this.deleteExerciseService = new DeleteExerciseServiceImpl(userService, groupRepository, exerciseRepository);
    }

    @Test
    public void testDeleteExerciseDelete() {
        Long groupId = 1L;
        Long exerciseId = 1L;

        User user = UserBuilder.createUser();

        Group group = GroupBuilder.createGroup();
        group.setId(1L);
        group.setAdmin(user.getProfile());

        Exercise exercise = ExerciseBuilder.createExercise();

        when(userService.getUserInSession()).thenReturn(user);
        when(groupRepository.findByIdAndAdminId(groupId, user.getProfile().getId())).thenReturn(Optional.of(group));
        when(exerciseRepository.findByIdAndGroupId(exerciseId, groupId)).thenReturn(Optional.of(exercise));

        deleteExerciseService.deleteExercise(groupId, exerciseId);

        verify(userService).getUserInSession();
        verify(groupRepository).findByIdAndAdminId(groupId, user.getProfile().getId());
        verify(exerciseRepository).findByIdAndGroupId(exerciseId, groupId);
        verify(exerciseRepository).delete(exercise);
    }

    @Test
    public void testDeleteExerciseInactivate() {
        Long groupId = 1L;
        Long exerciseId = 2L;

        User user = UserBuilder.createUser();

        Group group = new Group();

        Exercise exercise = ExerciseBuilder.createExercise();
        exercise.setQuestions(Arrays.asList(QuestionBuilder.createQuestion(1L),
                QuestionBuilder.createQuestion(2L),
                QuestionBuilder.createQuestion(3L),
                QuestionBuilder.createQuestion(4L)));

        when(userService.getUserInSession()).thenReturn(user);
        when(groupRepository.findByIdAndAdminId(groupId, user.getProfile().getId())).thenReturn(Optional.of(group));
        when(exerciseRepository.findByIdAndGroupId(exerciseId, groupId)).thenReturn(Optional.of(exercise));

        deleteExerciseService.deleteExercise(groupId, exerciseId);

        verify(userService).getUserInSession();
        verify(groupRepository).findByIdAndAdminId(groupId, user.getProfile().getId());
        verify(exerciseRepository).findByIdAndGroupId(exerciseId, groupId);
        verify(exerciseRepository).save(exercise);
    }

    @Test
    public void testDeleteExerciseGroupNotFound() {
        Long groupId = 1L;
        Long exerciseId = 2L;

        User user = new User();

        when(userService.getUserInSession()).thenReturn(user);
        when(groupRepository.findByIdAndAdminId(groupId, user.getProfile().getId())).thenReturn(Optional.empty());

        try {
            deleteExerciseService.deleteExercise(groupId, exerciseId);
        } catch (GroupNotFoundException e) {
            assertEquals(GroupNotFoundException.class, e.getClass());
        }

        verify(userService).getUserInSession();
        verify(groupRepository).findByIdAndAdminId(groupId, user.getProfile().getId());
        verify(exerciseRepository, never()).findByIdAndGroupId(anyLong(), anyLong());
        verify(exerciseRepository, never()).delete(any(Exercise.class));
    }

    @Test
    public void testDeleteExerciseExerciseNotFound() {
        Long groupId = 1L;
        Long exerciseId = 2L;

        User user = UserBuilder.createUser();

        Group group = new Group();

        when(userService.getUserInSession()).thenReturn(user);
        when(groupRepository.findByIdAndAdminId(groupId, user.getProfile().getId())).thenReturn(Optional.of(group));
        when(exerciseRepository.findByIdAndGroupId(exerciseId, groupId)).thenReturn(Optional.empty());

        try {
            deleteExerciseService.deleteExercise(groupId, exerciseId);
        } catch (ExerciseNotFoundException e) {
            assertEquals(ExerciseNotFoundException.class, e.getClass());
        }

        verify(userService).getUserInSession();
        verify(groupRepository).findByIdAndAdminId(groupId, user.getProfile().getId());
        verify(exerciseRepository).findByIdAndGroupId(exerciseId, groupId);
        verify(exerciseRepository, never()).delete(any(Exercise.class));
    }
}
