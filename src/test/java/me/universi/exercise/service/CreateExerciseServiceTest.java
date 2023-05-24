package me.universi.exercise.service;

import me.universi.exercise.ExerciseRepository;
import me.universi.exercise.builder.ExerciseBuilder;
import me.universi.exercise.dto.ExerciseCreateDTO;
import me.universi.exercise.entities.Exercise;
import me.universi.exercise.services.CreateExerciseServiceImpl;
import me.universi.group.builder.GroupBuilder;
import me.universi.group.entities.Group;
import me.universi.group.exceptions.GroupNotFoundException;
import me.universi.group.repositories.GroupRepository;
import me.universi.profile.entities.Profile;
import me.universi.question.exceptions.QuestionNotfoundException;
import me.universi.user.UserBuilder;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import profile.builder.ProfileBuilder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


@Tag("service")
@ExtendWith(MockitoExtension.class)
public class CreateExerciseServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private UserService userService;

    private CreateExerciseServiceImpl createExerciseService;

    @BeforeEach
    public void setup() {
        this.createExerciseService = new CreateExerciseServiceImpl(groupRepository, exerciseRepository, userService);
    }

    @Test
    @DisplayName("Must test the registration of an exercise")
    public void testCreateExercise() {
        ExerciseCreateDTO exerciseDTO = new ExerciseCreateDTO("Exercise Test");

        Long groupId = 1L;

        User user = UserBuilder.createUser();
        Profile profile = ProfileBuilder.createProfile();
        user.setProfile(profile);
        profile.setUser(user);
        Group group = GroupBuilder.createGroup();

        when(userService.getUserInSession()).thenReturn(user);
        when(groupRepository.findByIdAndAdminId(groupId, user.getProfile().getId())).thenReturn(Optional.of(group));
        when(exerciseRepository.save(any(Exercise.class))).thenReturn(ExerciseBuilder.createExercise());

        Exercise result = createExerciseService.createExercise(groupId, exerciseDTO);

        assertEquals(ExerciseBuilder.createExercise(), result);
        verify(groupRepository).findByIdAndAdminId(anyLong(), anyLong());
        verify(exerciseRepository).save(any(Exercise.class));
    }

    @Test
    @DisplayName("Should return exception when logged in user is not group admin")
    void shouldThrowQuestionNotFoundException() {
        when(userService.getUserInSession()).thenReturn(UserBuilder.createUserSecondary());
        when(groupRepository.findByIdAndAdminId(1L, 1L)).thenReturn(Optional.of(GroupBuilder.createGroup()));

        ExerciseCreateDTO exerciseDTO = new ExerciseCreateDTO("Exercise Test");

        assertThrows(GroupNotFoundException.class,
                () -> this.createExerciseService.createExercise(1L, exerciseDTO)
        );
    }
}
