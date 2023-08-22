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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
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

        UUID uuid_1 = UUID.fromString("47e2cc9e-69be-4482-bd90-1832ec403018");
        UUID groupId = uuid_1;
        UUID exerciseId = uuid_1;

        User user = UserBuilder.createUser();

        Group group = GroupBuilder.createGroup();
        group.setId(uuid_1);
        group.setAdmin(user.getProfile());

        Exercise exercise = ExerciseBuilder.createExercise();

        when(userService.getUserInSession()).thenReturn(user);
        when(groupRepository.findFirstByIdAndAdminId(groupId, user.getProfile().getId())).thenReturn(Optional.of(group));
        when(exerciseRepository.findFirstByIdAndGroupId(exerciseId, groupId)).thenReturn(Optional.of(exercise));

        deleteExerciseService.deleteExercise(groupId, exerciseId);

        verify(userService).getUserInSession();
        verify(groupRepository).findFirstByIdAndAdminId(groupId, user.getProfile().getId());
        verify(exerciseRepository).findFirstByIdAndGroupId(exerciseId, groupId);
    }

    @Test
    public void testDeleteExerciseInactivate() {

        UUID uuid_1 = UUID.fromString("47e2cc9e-69be-4482-bd90-1832ec403018");
        UUID uuid_2 = UUID.fromString("626370e9-b1ff-4b2d-baf8-b6b8ba04f603");
        UUID uuid_3 = UUID.fromString("1fade783-e4b9-4e22-87a0-e5ca2f3c51fd");
        UUID uuid_4 = UUID.fromString("bdef7e08-e4c2-49f4-b446-3f252800229c");

        UUID groupId = uuid_1;
        UUID exerciseId = uuid_2;

        User user = UserBuilder.createUser();

        Group group = GroupBuilder.createGroup();

        Exercise exercise = ExerciseBuilder.createExercise();
        exercise.setQuestions(Arrays.asList(QuestionBuilder.createQuestion(uuid_1),
                QuestionBuilder.createQuestion(uuid_2),
                QuestionBuilder.createQuestion(uuid_3),
                QuestionBuilder.createQuestion(uuid_4)));

        when(userService.getUserInSession()).thenReturn(user);
        when(groupRepository.findFirstByIdAndAdminId(groupId, user.getProfile().getId())).thenReturn(Optional.of(group));
        when(exerciseRepository.findFirstByIdAndGroupId(exerciseId, groupId)).thenReturn(Optional.of(exercise));

        deleteExerciseService.deleteExercise(groupId, exerciseId);

        verify(userService).getUserInSession();
        verify(groupRepository).findFirstByIdAndAdminId(groupId, user.getProfile().getId());
        verify(exerciseRepository).findFirstByIdAndGroupId(exerciseId, groupId);
    }

    @Test
    public void testDeleteExerciseGroupNotFound() {

        UUID uuid_1 = UUID.fromString("47e2cc9e-69be-4482-bd90-1832ec403018");
        UUID uuid_2 = UUID.fromString("626370e9-b1ff-4b2d-baf8-b6b8ba04f603");
        UUID groupId = uuid_1;
        UUID exerciseId = uuid_2;

        User user = UserBuilder.createUser();

        when(userService.getUserInSession()).thenReturn(user);
        when(groupRepository.findFirstByIdAndAdminId(groupId, user.getProfile().getId())).thenReturn(Optional.empty());

        try {
            deleteExerciseService.deleteExercise(groupId, exerciseId);
        } catch (GroupNotFoundException e) {
            assertEquals(GroupNotFoundException.class, e.getClass());
        }

        verify(userService).getUserInSession();
        verify(groupRepository).findFirstByIdAndAdminId(groupId, user.getProfile().getId());
        verify(exerciseRepository, never()).findFirstByIdAndGroupId(any(UUID.class), any(UUID.class));
        verify(exerciseRepository, never()).delete(any(Exercise.class));
    }

    @Test
    public void testDeleteExerciseExerciseNotFound() {

        UUID uuid_1 = UUID.fromString("47e2cc9e-69be-4482-bd90-1832ec403018");
        UUID uuid_2 = UUID.fromString("626370e9-b1ff-4b2d-baf8-b6b8ba04f603");
        UUID groupId = uuid_1;
        UUID exerciseId = uuid_2;

        User user = UserBuilder.createUser();

        Group group = GroupBuilder.createGroup();
        when(userService.getUserInSession()).thenReturn(user);
        when(groupRepository.findFirstByIdAndAdminId(groupId, user.getProfile().getId())).thenReturn(Optional.of(group));
        when(exerciseRepository.findFirstByIdAndGroupId(exerciseId, groupId)).thenReturn(Optional.empty());

        try {
            deleteExerciseService.deleteExercise(groupId, exerciseId);
        } catch (ExerciseNotFoundException e) {
            assertEquals(ExerciseNotFoundException.class, e.getClass());
        }

        verify(userService).getUserInSession();
        verify(groupRepository).findFirstByIdAndAdminId(groupId, user.getProfile().getId());
        verify(exerciseRepository).findFirstByIdAndGroupId(exerciseId, groupId);
        verify(exerciseRepository, never()).delete(any(Exercise.class));
    }
}
