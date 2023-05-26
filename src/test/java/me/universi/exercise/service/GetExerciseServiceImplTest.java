package me.universi.exercise.service;

import me.universi.exercise.ExerciseRepository;
import me.universi.exercise.builder.ExerciseBuilder;
import me.universi.exercise.entities.Exercise;
import me.universi.exercise.exception.ExerciseNotFoundException;
import me.universi.exercise.services.GetExerciseServiceImpl;
import me.universi.group.builder.GroupBuilder;
import me.universi.group.entities.Group;
import me.universi.group.exceptions.GroupNotFoundException;
import me.universi.group.repositories.GroupRepository;
import me.universi.user.UserBuilder;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("service")
@ExtendWith(MockitoExtension.class)
public class GetExerciseServiceImplTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private UserService userService;

    @Mock
    private GroupRepository groupRepository;

    private GetExerciseServiceImpl getExerciseService;

    @BeforeEach
    public void setup() {
        this.getExerciseService = new GetExerciseServiceImpl(exerciseRepository, userService, groupRepository);
    }

    @Test
    @DisplayName("Test get exercise")
    public void testGetExercise() {
        Long groupId = 1L;
        Long exerciseId = 2L;

        User user = UserBuilder.createUser();

        Group group = GroupBuilder.createGroup();

        when(userService.getUserInSession()).thenReturn(user);
        when(groupRepository.findByIdAndAdminId(groupId, user.getProfile().getId())).thenReturn(Optional.of(group));
        when(exerciseRepository.findByIdAndGroupId(exerciseId, groupId)).thenReturn(Optional.of(ExerciseBuilder.createExercise()));

        Exercise result = getExerciseService.getExercise(groupId, exerciseId);

        assertEquals(ExerciseBuilder.createExercise(), result);
        verify(userService).getUserInSession();
        verify(groupRepository).findByIdAndAdminId(groupId, user.getProfile().getId());
        verify(exerciseRepository).findByIdAndGroupId(exerciseId, groupId);
    }

    @Test
    @DisplayName("Should throw exception when group is not found")
    public void testGetExerciseGroupNotFound() {
        Long groupId = 1L;
        Long exerciseId = 2L;

        User user = UserBuilder.createUser();

        when(userService.getUserInSession()).thenReturn(user);
        when(groupRepository.findByIdAndAdminId(groupId, user.getProfile().getId())).thenReturn(Optional.empty());

        try {
            getExerciseService.getExercise(groupId, exerciseId);
        } catch (GroupNotFoundException e) {
            // Verifica se a exceção GroupNotFoundException é lançada corretamente
            assertEquals(GroupNotFoundException.class, e.getClass());
        }

        verify(userService).getUserInSession();
        verify(groupRepository).findByIdAndAdminId(groupId, user.getProfile().getId());
        verify(exerciseRepository, never()).findByIdAndGroupId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Should throw exception when exercise is not found")
    public void testGetExerciseExerciseNotFound() {
        Long groupId = 1L;
        Long exerciseId = 2L;

        User user = UserBuilder.createUser();

        Group group = GroupBuilder.createGroup();

        when(userService.getUserInSession()).thenReturn(user);
        when(groupRepository.findByIdAndAdminId(groupId, user.getProfile().getId())).thenReturn(Optional.of(group));
        when(exerciseRepository.findByIdAndGroupId(exerciseId, groupId)).thenReturn(Optional.empty());

        try {
            getExerciseService.getExercise(groupId, exerciseId);
        } catch (ExerciseNotFoundException e) {
            // Verifica se a exceção ExerciseNotFoundException é lançada corretamente
            assertEquals(ExerciseNotFoundException.class, e.getClass());
        }

        verify(userService).getUserInSession();
        verify(groupRepository).findByIdAndAdminId(groupId, user.getProfile().getId());
        verify(exerciseRepository).findByIdAndGroupId(exerciseId, groupId);
    }
}

