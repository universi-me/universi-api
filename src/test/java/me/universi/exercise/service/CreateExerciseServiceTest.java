package me.universi.exercise.service;

import me.universi.exercise.ExerciseRepository;
import me.universi.exercise.services.CreateExerciseService;
import me.universi.exercise.services.CreateExerciseServiceImpl;
import me.universi.grupo.repositories.GroupRepository;
import me.universi.user.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static me.universi.group.builder.GroupBuilder.createGroup;
import static me.universi.user.UserBuilder.createUser;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("service")
@DisplayName("Test create exercise service")
public class CreateExerciseServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private UserRepository userRepository;

    private CreateExerciseService createExerciseService;

    @BeforeEach
    public void setUp() {
        this.createExerciseService = new CreateExerciseServiceImpl(
                groupRepository,
                exerciseRepository
        );
    }

    @DisplayName("Deve criar uma exercicio")
    void shouldCreateExercise() throws Exception {
        when(userRepository.findFirstById(anyLong())).thenReturn(Optional.of(createUser()));

        when(groupRepository.findFirstById(anyLong())).thenReturn(Optional.of(createGroup()));

//        GoogleCloudDTO googleCloudDTO = GoogleCloudDTO
//                .builder()
//                .blobName("teste" + "-" + LocalDateTime.now() + ".jpeg")
//                .blobMediaLink("https://storage.googleapis.com/download/storage/v1/b/myfastsurvey-audio/o/audio-Vida%20Loka-2021-01-17T14:31:02.mp3?generation=1613419900501830&alt=media")
//                .build();
//
//        V1AlternativeCreationDTO alternativeCreationDTO = createAlternativeCreationDTO()
//                .imageBase64(readFile("image/imagem-base64.txt"))
//                .build();
//
//        when(questionRepository.findByIdAndQuestionnaireIdAndQuestionnaireUserId(anyLong(), anyLong(), anyLong()))
//                .thenReturn(Optional.of(createQuestion().id(1L).build()));
//
//        when(googleCloudService.uploadImage(anyString(), anyString()))
//                .thenReturn(Optional.of(googleCloudDTO));
//
//        this.v1CreateAlternativeService.createAlternative(1L, 1L, 1L, alternativeCreationDTO);
//
//        ArgumentCaptor<V1Alternative> argumentCaptor = ArgumentCaptor.forClass(V1Alternative.class);
//        verify(alternativeRepository).save(argumentCaptor.capture());
//
//        V1Alternative result = argumentCaptor.getValue();
//
//        assertAll("alternative",
//                () -> assertThat(result.getTitle(), is("alternative")),
//                () -> assertThat(result.getImageSrc(), is(googleCloudDTO.getBlobMediaLink())),
//                () -> assertTrue(result.getCloudObjectId().contains(googleCloudDTO.getBlobName().substring(13)))
//        );
    }
}
