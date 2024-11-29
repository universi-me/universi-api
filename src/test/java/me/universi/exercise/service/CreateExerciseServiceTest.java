package me.universi.exercise.service;

import me.universi.exercise.ExerciseRepository;
import me.universi.exercise.builder.ExerciseBuilder;
import me.universi.exercise.dto.ExerciseCreateDTO;
import me.universi.exercise.entities.Exercise;
import me.universi.exercise.services.CreateExerciseServiceImpl;
import me.universi.group.builder.GroupBuilder;
import me.universi.group.entities.Group;
import me.universi.group.repositories.GroupRepository;
import me.universi.profile.entities.Profile;
import me.universi.user.UserBuilder;
import me.universi.user.entities.User;
import me.universi.user.exceptions.UnauthorizedException;
import me.universi.user.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import profile.builder.ProfileBuilder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


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

        UUID uuid_1 = UUID.fromString("47e2cc9e-69be-4482-bd90-1832ec403018");
        UUID groupId = uuid_1;

        User user = UserBuilder.createUser();
        Profile profile = ProfileBuilder.createProfile();
        user.setProfile(profile);
        profile.setUser(user);
        Group group = GroupBuilder.createGroup();

        when(userService.getUserInSession()).thenReturn(user);
        when(groupRepository.findFirstByIdAndAdminId(groupId, user.getProfile().getId())).thenReturn(Optional.of(group));
        when(exerciseRepository.save(any(Exercise.class))).thenReturn(ExerciseBuilder.createExercise());

        Exercise result = createExerciseService.createExercise(groupId, exerciseDTO);

        assertEquals(ExerciseBuilder.createExercise(), result);
        verify(groupRepository).findFirstByIdAndAdminId(any(UUID.class), any(UUID.class));
        verify(exerciseRepository).save(any(Exercise.class));
    }

    @Test
    @DisplayName("Should return exception when logged in user is not group admin")
    void shouldThrowQuestionNotFoundException() {

        UUID uuid_1 = UUID.fromString("47e2cc9e-69be-4482-bd90-1832ec403018");
        UUID uuid_2 = UUID.fromString("626370e9-b1ff-4b2d-baf8-b6b8ba04f603");

        when(userService.getUserInSession()).thenReturn(UserBuilder.createUserSecondary());
        when(groupRepository.findFirstByIdAndAdminId(uuid_1, uuid_2)).thenReturn(Optional.of(GroupBuilder.createGroup()));

        ExerciseCreateDTO exerciseDTO = new ExerciseCreateDTO("Exercise Test");

        assertThrows(UnauthorizedException.class,
                () -> this.createExerciseService.createExercise(uuid_1, exerciseDTO)
        );
    }
}
