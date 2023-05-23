package me.universi.question.services;

import me.universi.group.builder.GroupBuilder;
import me.universi.group.entities.Group;
import me.universi.group.repositories.GroupRepository;
import me.universi.profile.entities.Profile;
import me.universi.question.QuestionRepository;
import me.universi.question.builder.QuestionBuilder;
import me.universi.question.entities.Question;
import me.universi.user.UserBuilder;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import profile.builder.ProfileBuilder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetQuestionServiceImplTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private UserService userService;

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private GetQuestionServiceImpl getQuestionService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetQuestion() {
        Long groupId = 1L;
        Long exerciseId = 1L;
        Long questionId = 1L;

        User user = UserBuilder.createUser();
        Profile profile = ProfileBuilder.createProfile();
        user.setProfile(profile);
        profile.setUser(user);
        Group group = GroupBuilder.createGroup();

        when(userService.obterUsuarioNaSessao()).thenReturn(user);
        when(groupRepository.findByIdAndAdminId(groupId, user.getProfile().getId())).thenReturn(Optional.of(group));
        when(questionRepository.findByIdAndExercisesId(questionId, exerciseId)).thenReturn(Optional.of(QuestionBuilder.createQuestion()));

        Question result = getQuestionService.getQuestion(groupId, exerciseId, questionId);

        assertEquals(QuestionBuilder.createQuestion(), result);
        verify(userService).obterUsuarioNaSessao();
        verify(groupRepository).findByIdAndAdminId(groupId, user.getProfile().getId());
        verify(questionRepository).findByIdAndExercisesId(questionId, exerciseId);
    }
}