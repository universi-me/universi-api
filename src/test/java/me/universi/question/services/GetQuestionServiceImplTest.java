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
import me.universi.builder.ProfileBuilder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
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
        Long groupId = 1L;
        Long exerciseId = 1L;
        Long questionId = 1L;

        User user = UserBuilder.createUser();
        Profile profile = ProfileBuilder.createProfile();
        user.setProfile(profile);
        profile.setUser(user);
        Group group = GroupBuilder.createGroup();

        when(userService.getUserInSession()).thenReturn(user);
        when(groupRepository.findByIdAndAdminId(groupId, user.getProfile().getId())).thenReturn(Optional.of(group));
        when(questionRepository.findByIdAndExercisesId(questionId, exerciseId)).thenReturn(Optional.of(QuestionBuilder.createQuestion()));

        Question result = getQuestionService.getQuestion(groupId, exerciseId, questionId);

        assertEquals(QuestionBuilder.createQuestion(), result);
        verify(userService).getUserInSession();
        verify(groupRepository).findByIdAndAdminId(groupId, user.getProfile().getId());
        verify(questionRepository).findByIdAndExercisesId(questionId, exerciseId);
    }

    @Test
    @DisplayName("Should throw exception when question is not found")
    void shouldThrowQuestionNotFoundException() {
        when(this.questionRepository.findByIdAndExercisesId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        when(userService.getUserInSession()).thenReturn(UserBuilder.createUser());
        when(groupRepository.findByIdAndAdminId(anyLong(), anyLong())).thenReturn(Optional.of(GroupBuilder.createGroup()));

        assertThrows(QuestionNotfoundException.class,
                () -> this.getQuestionService.getQuestion(1L, 1L, 1L)
        );
    }
}