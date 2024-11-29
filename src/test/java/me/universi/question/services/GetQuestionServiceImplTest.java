package me.universi.question.services;

import me.universi.group.builder.GroupBuilder;
import me.universi.group.entities.Group;
import me.universi.group.repositories.GroupRepository;
import me.universi.profile.entities.Profile;
import me.universi.question.QuestionRepository;
import me.universi.question.builder.QuestionBuilder;
import me.universi.question.entities.Question;
import me.universi.question.exceptions.QuestionNotfoundException;
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
import profile.builder.ProfileBuilder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("service")
@ExtendWith(MockitoExtension.class)
public class GetQuestionServiceImplTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private UserService userService;

    @Mock
    private GroupRepository groupRepository;

    private GetQuestionServiceImpl getQuestionService;

    @BeforeEach
    public void setup() {
        this.getQuestionService = new GetQuestionServiceImpl(this.questionRepository, userService, groupRepository);
    }

    @Test
    @DisplayName("Must test get a question")
    public void testGetQuestion() {
        UUID uuid_1 = UUID.fromString("47e2cc9e-69be-4482-bd90-1832ec403018");
        UUID groupId = uuid_1;
        UUID exerciseId = uuid_1;
        UUID questionId = uuid_1;

        User user = UserBuilder.createUser();
        Profile profile = ProfileBuilder.createProfile();
        user.setProfile(profile);
        profile.setUser(user);
        Group group = GroupBuilder.createGroup();

        when(userService.getUserInSession()).thenReturn(user);
        when(groupRepository.findFirstByIdAndAdminId(groupId, user.getProfile().getId())).thenReturn(Optional.of(group));
        when(questionRepository.findFirstByIdAndExercisesId(questionId, exerciseId)).thenReturn(Optional.of(QuestionBuilder.createQuestion()));

        Question result = getQuestionService.getQuestion(groupId, exerciseId, questionId);

        assertEquals(QuestionBuilder.createQuestion(), result);
        verify(userService).getUserInSession();
        verify(groupRepository).findFirstByIdAndAdminId(groupId, user.getProfile().getId());
        verify(questionRepository).findFirstByIdAndExercisesId(questionId, exerciseId);
    }

    @Test
    @DisplayName("Should throw exception when question is not found")
    void shouldThrowQuestionNotFoundException() {
        when(this.questionRepository.findFirstByIdAndExercisesId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.empty());
        when(userService.getUserInSession()).thenReturn(UserBuilder.createUser());
        when(groupRepository.findFirstByIdAndAdminId(any(UUID.class), any(UUID.class))).thenReturn(Optional.of(GroupBuilder.createGroup()));

        UUID uuid_1 = UUID.fromString("47e2cc9e-69be-4482-bd90-1832ec403018");
        assertThrows(QuestionNotfoundException.class,
                () -> this.getQuestionService.getQuestion(uuid_1, uuid_1, uuid_1)
        );
    }
}