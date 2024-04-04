package com.example.wellness.services;

import com.example.wellness.dto.common.PageableBody;
import com.example.wellness.dto.common.UserDto;
import com.example.wellness.dto.common.response.PageableResponse;
import com.example.wellness.dto.common.response.ResponseWithUserDto;
import com.example.wellness.dto.common.response.ResponseWithUserLikesAndDislikes;
import com.example.wellness.dto.exercise.ExerciseResponse;
import com.example.wellness.dto.training.TrainingBody;
import com.example.wellness.dto.training.TrainingResponse;
import com.example.wellness.dto.training.TrainingResponseWithOrderCount;
import com.example.wellness.dto.training.TrainingWithOrderCount;
import com.example.wellness.enums.Role;
import com.example.wellness.exceptions.action.IllegalActionException;
import com.example.wellness.exceptions.action.PrivateRouteException;
import com.example.wellness.exceptions.action.SubEntityUsed;
import com.example.wellness.exceptions.notFound.NotFoundEntity;
import com.example.wellness.mappers.TrainingMapper;
import com.example.wellness.mappers.UserMapper;
import com.example.wellness.models.Exercise;
import com.example.wellness.models.Order;
import com.example.wellness.models.Training;
import com.example.wellness.models.user.UserCustom;
import com.example.wellness.repositories.TrainingRepository;
import com.example.wellness.repositories.UserRepository;
import com.example.wellness.services.impl.TrainingServiceImpl;
import com.example.wellness.utils.EntitiesUtils;
import com.example.wellness.utils.PageableUtilsCustom;
import com.example.wellness.utils.UserUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("tc")
class TrainingServiceImplTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TrainingMapper trainingMapper;

    @Mock
    private PageableUtilsCustom pageableUtilsCustom;

    @Mock
    private UserUtils userUtils;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private EntitiesUtils entitiesUtils;

    @Mock
    private ExerciseService exerciseService;

    @Spy
    @InjectMocks
    private TrainingServiceImpl trainingServiceImpl;


    private final String modelName = "training";

    private final List<String> allowedSortingFields = List.of("id", "userId", "title", "createdAt");

    UserCustom user = UserCustom.builder()
            .id(1L)
            .firstName("John")
            .lastName("Doe")
            .email("john.doe@example.com")
            .password("password")
            .role(Role.ROLE_USER)
            .build();
    UserCustom admin = UserCustom.builder()
            .id(2L)
            .firstName("Jane")
            .lastName("Doe")
            .email("jane.doe@example.com")
            .password("password")
            .role(Role.ROLE_ADMIN)
            .build();

    UserCustom trainer = UserCustom.builder()
            .id(3L)
            .firstName("Jim")
            .lastName("Doe")
            .email("jim.doe@example.com")
            .password("password")
            .role(Role.ROLE_TRAINER)
            .build();

    Exercise exerciseAdmin1 = Exercise.builder()
            .id(1L)
            .muscleGroups(List.of("arms", "legs"))
            .approved(true)
            .body("Exercise body 1")
            .title("Exercise Title 1")
            .userId(2L)
            .build();
    Exercise exerciseAdmin2 = Exercise.builder()
            .id(2L)
            .muscleGroups(List.of("arms", "legs"))
            .approved(true)
            .body("Exercise body 2")
            .title("Exercise Title 2")
            .userId(2L)
            .build();

    Exercise exerciseTrainer1 = Exercise.builder()
            .id(3L)
            .muscleGroups(List.of("chest", "back"))
            .approved(true)
            .body("Exercise body 2")
            .title("Exercise Title 2")
            .userId(3L)
            .build();

    Exercise exerciseTrainer2 = Exercise.builder().muscleGroups(List.of("chest", "legs"))
            .id(4L)
            .approved(true)
            .body("Exercise body 3")
            .title("Exercise Title 3")
            .userId(3L)
            .build();

    Training trainingAdmin = Training.builder()
            .id(1L)
            .approved(true)
            .body("Training body 1")
            .title("Training Title 1")
            .userId(2L)
            .price(19.99)
            .exercises(List.of(exerciseAdmin1.getId()))
            .build();

    Training trainingTrainer1 = Training.builder()
            .id(2L)
            .approved(true)
            .body("Training body 2")
            .title("Training Title 2")
            .userId(3L)
            .price(29.99)
            .userLikes(List.of(1L))
            .userDislikes(List.of())
            .exercises(List.of(2L, 3L))
            .build();
    Training trainingTrainer2 = Training.builder()
            .id(3L)
            .approved(false)
            .body("Training body 3")
            .title("Training Title 3")
            .userId(3L)
            .price(39.99)
            .exercises(List.of(2L, 3L))
            .build();
    TrainingResponse trainingTrainer1Response = TrainingResponse.builder()
            .id(2L)
            .approved(true)
            .body("Training body 2")
            .title("Training Title 2")
            .userId(3L)
            .price(29.99)
            .userLikes(List.of(1L))
            .userDislikes(List.of())
            .exercises(List.of(2L, 3L))
            .build();

    TrainingResponse trainingTrainer2Response = TrainingResponse.builder()
            .id(3L)
            .approved(false)
            .body("Training body 3")
            .title("Training Title 3")
            .userId(3L)
            .price(39.99)
            .exercises(List.of(2L, 3L))
            .userLikes(List.of(1L))
            .userDislikes(List.of())
            .build();

    Order order = Order.builder()
            .id(1L)
            .shippingAddress("123 Fake St.")
            .payed(true)
            .trainings(List.of(1L, 2L))
            .userId(1L)
            .build();

    UserDto trainerDto = UserDto.builder()
            .id(3L)
            .firstName("Jim")
            .lastName("Doe")
            .email("jim.doe@example.com")
            .role(Role.ROLE_TRAINER)
            .build();

    ResponseWithUserDto<TrainingResponse> responseWithUserDto = ResponseWithUserDto.<TrainingResponse>builder()
            .model(trainingTrainer1Response)
            .user(trainerDto)
            .build();

    ResponseWithUserDto<ExerciseResponse> exercise2WithUserDto = ResponseWithUserDto.<ExerciseResponse>builder()
            .model(ExerciseResponse.builder()
                    .id(2L)
                    .body("Exercise body 2")
                    .title("Exercise Title 2")
                    .userId(2L)
                    .muscleGroups(List.of("arms", "legs"))
                    .build())
            .user(trainerDto)
            .build();

    ResponseWithUserDto<ExerciseResponse> exercise3WithUserDto = ResponseWithUserDto.<ExerciseResponse>builder()
            .model(ExerciseResponse.builder()
                    .id(3L)
                    .body("Exercise body 3")
                    .title("Exercise Title 3")
                    .userId(3L)
                    .muscleGroups(List.of("chest", "legs"))
                    .build())
            .user(trainerDto)
            .build();
    ResponseWithUserLikesAndDislikes<TrainingResponse> responseWithUserLikesAndDislikes = ResponseWithUserLikesAndDislikes.<TrainingResponse>builder()
            .model(trainingTrainer1Response)
            .user(trainerDto)
            .userLikes(List.of(trainerDto))
            .userDislikes(List.of())
            .build();

    PageableBody pageableBody = PageableBody.builder()
            .page(0)
            .size(10)
            .sortingCriteria(Map.of("id", "asc"))
            .build();
    PageRequest pageRequest = PageRequest.of(0, 10);

    TrainingBody trainingBody = TrainingBody.builder()
            .title("NEW TITLE")
            .body("NEW BODY")
            .price(111.11)
            .exercises(List.of(1L))
            .build();

    PageableResponse<TrainingResponse> pageableResponse = PageableResponse.<TrainingResponse>builder()
            .content(trainingTrainer1Response)
            .build();


    @Test
    void get_model_success() {

        when(trainingRepository.findById(1L))
                .thenReturn(Mono.just(trainingAdmin));

        StepVerifier.create(trainingServiceImpl.getModel(1L))
                .assertNext(training -> assertEquals(trainingAdmin, training))
                .verifyComplete();

        verify(trainingRepository).findById(1L);
    }

    @Test
    void get_model_error() {
        when(trainingRepository.findById(1L))
                .thenReturn(Mono.empty());

        StepVerifier.create(trainingServiceImpl.getModel(1L))
                .expectErrorMatches(e -> {
                    assertInstanceOf(NotFoundEntity.class, e);
                    assert e instanceof NotFoundEntity;
                    NotFoundEntity n = (NotFoundEntity) e;

                    assertEquals("training", n.getName());
                    assertEquals(1L, n.getId());

                    return true;

                })
                .verify();

        verify(trainingRepository).findById(1L);
    }

    @Test
    void is_not_author_true() {
        StepVerifier.create(trainingServiceImpl.isNotAuthor(trainingAdmin, user))
                .assertNext(Assertions::assertTrue)
                .verifyComplete();
    }

    @Test
    void is_not_author_false() {
        StepVerifier.create(trainingServiceImpl.isNotAuthor(trainingAdmin, admin))
                .assertNext(Assertions::assertFalse)
                .verifyComplete();
    }

    @Test
    void private_route_success() {

        when(userUtils.hasPermissionToModifyEntity(trainer, 2L))
                .thenReturn(Mono.just(true));

        StepVerifier.create(trainingServiceImpl.privateRoute(true, trainer, 2L))
                .verifyComplete();

        verify(userUtils).hasPermissionToModifyEntity(trainer, 2L);
    }

    @Test
    void private_route_error() {

        when(userUtils.hasPermissionToModifyEntity(user, 2L))
                .thenReturn(Mono.just(false));

        StepVerifier.create(trainingServiceImpl.privateRoute(true, user, 2L))
                .expectErrorMatches(e -> {
                    assertInstanceOf(PrivateRouteException.class, e);
                    assertEquals("Not allowed!", e.getMessage());

                    return true;

                })
                .verify();

        verify(userUtils).hasPermissionToModifyEntity(user, 2L);
    }


    @Test
    void get_response_guard_success() {
        when(userUtils.hasPermissionToModifyEntity(eq(trainer), any(Long.class)))
                .thenReturn(Mono.just(true));

        when(trainingMapper.fromModelToResponse(trainingTrainer1))
                .thenReturn(trainingTrainer1Response);

        StepVerifier.create(trainingServiceImpl.getResponseGuard(trainer, trainingTrainer1, true))
                .assertNext(trainingResponse -> assertEquals(trainingTrainer1Response, trainingResponse))
                .verifyComplete();


        ArgumentCaptor<Long> longArgumentCaptor = ArgumentCaptor.forClass(Long.class);

        verify(userUtils).hasPermissionToModifyEntity(eq(trainer), longArgumentCaptor.capture());

        assertEquals(trainingTrainer1.getUserId(), longArgumentCaptor.getValue());

        verify(trainingMapper).fromModelToResponse(trainingTrainer1);
    }


    @Test
    void get_response_guard_error() {
        when(userUtils.hasPermissionToModifyEntity(any(), any(Long.class)))
                .thenReturn(Mono.just(false));
        when(trainingMapper.fromModelToResponse(any(Training.class)))
                .thenReturn(trainingTrainer1Response);

        StepVerifier.create(trainingServiceImpl.getResponseGuard(user, trainingTrainer1, true))
                .expectErrorMatches(e -> {
                    assertInstanceOf(PrivateRouteException.class, e);
                    assertEquals("Not allowed!", e.getMessage());
                    return true;
                })
                .verify();

        verify(userUtils).hasPermissionToModifyEntity(eq(user), any(Long.class));
        verifyNoMoreInteractions(trainingMapper);
    }

    @Test
    void delete_training_owner_success() {
        when(trainingRepository.countOrdersByTrainingId(1L))
                .thenReturn(Mono.just(0L));
        when(userUtils.getPrincipal()).thenReturn(Mono.just(trainer));
        when(userUtils.hasPermissionToModifyEntity(trainer, 3L))
                .thenReturn(Mono.just(true));
        when(trainingRepository.findById(1L)).thenReturn(Mono.just(trainingTrainer1));
        when(trainingRepository.delete(trainingTrainer1)).thenReturn(Mono.empty());
        when(trainingMapper.fromModelToResponse(any(Training.class)))
                .then(i -> {
                            Training t = i.getArgument(0);
                            return TrainingResponse
                                    .builder()
                                    .id(t.getId())
                                    .title(t.getTitle())
                                    .body(t.getBody())
                                    .price(t.getPrice())
                                    .approved(t.isApproved())
                                    .userId(t.getUserId())
                                    .exercises(t.getExercises())
                                    .userLikes(t.getUserLikes())
                                    .userDislikes(t.getUserDislikes())
                                    .build();
                        }
                );

        StepVerifier.create(trainingServiceImpl.deleteModel(1L))
                .assertNext(training -> {
                    assertEquals(trainingTrainer1Response, training);
                })
                .verifyComplete();

        verify(trainingRepository).countOrdersByTrainingId(1L);
        verify(trainingRepository).findById(1L);
        verify(userUtils).getPrincipal();
        verify(userUtils).hasPermissionToModifyEntity(trainer, 3L);
        verify(trainingRepository).delete(trainingTrainer1);

        ArgumentCaptor<Training> trainingArgumentCaptor = ArgumentCaptor.forClass(Training.class);
        verify(trainingMapper).fromModelToResponse(trainingArgumentCaptor.capture());
        assertEquals(trainingTrainer1, trainingArgumentCaptor.getValue());


    }

    @Test
    void get_model_guard_with_user_success() {
        when(userUtils.hasPermissionToModifyEntity(trainer, 3L))
                .thenReturn(Mono.just(true));
        when(userUtils.getUser(trainingTrainer1.getUserId()))
                .thenReturn(Mono.just(trainer));
        when(trainingMapper.fromModelToResponse(trainingTrainer1))
                .thenReturn(trainingTrainer1Response);
        when(userMapper.fromUserCustomToUserDto(trainer))
                .thenReturn(trainerDto);

        StepVerifier.create(trainingServiceImpl.getModelGuardWithUser(trainer, trainingTrainer1, true))
                .assertNext(responseWithUserDto -> {
                    assertEquals(trainingTrainer1Response, responseWithUserDto.getModel());
                    assertEquals(trainerDto, responseWithUserDto.getUser());
                })
                .verifyComplete();

        verify(userUtils).hasPermissionToModifyEntity(trainer, 3L);
        verify(userUtils).getUser(trainingTrainer1.getUserId());
        verify(trainingMapper).fromModelToResponse(trainingTrainer1);
        verify(userMapper).fromUserCustomToUserDto(trainer);
    }

    @Test
    void delete_training_orders_subEntityUsedException() {
        when(trainingRepository.countOrdersByTrainingId(1L))
                .thenReturn(Mono.just(1L));

        StepVerifier.create(trainingServiceImpl.deleteModel(1L))
                .expectErrorMatches(e -> {
                    assertInstanceOf(SubEntityUsed.class, e);

                    SubEntityUsed s = (SubEntityUsed) e;

                    assertEquals("training", s.getName());
                    assertEquals(1L, s.getId());

                    return true;

                })
                .verify();

        verify(trainingRepository).countOrdersByTrainingId(1L);
        verifyNoMoreInteractions(trainingRepository);
        verifyNoInteractions(userUtils);
        verifyNoInteractions(trainingMapper);

    }

    @Test
    void delete_training_not_found() {
        when(trainingRepository.countOrdersByTrainingId(1L))
                .thenReturn(Mono.just(0L));
        when(userUtils.getPrincipal()).thenReturn(Mono.just(user));
        when(trainingRepository.findById(1L)).thenReturn(Mono.empty());
        StepVerifier.create(trainingServiceImpl.deleteModel(1L))
                .expectErrorMatches(e -> {
                    assertInstanceOf(NotFoundEntity.class, e);

                    NotFoundEntity n = (NotFoundEntity) e;

                    assertEquals("training", n.getName());
                    assertEquals(1L, n.getId());

                    return true;
                })
                .verify();
        verify(trainingRepository).countOrdersByTrainingId(1L);
        verify(userUtils).getPrincipal();
        verify(trainingRepository).findById(1L);
        verifyNoMoreInteractions(trainingRepository);
        verifyNoMoreInteractions(userUtils);
        verifyNoMoreInteractions(trainingMapper);
    }

    @Test
    void delete_training_not_author() {
        when(trainingRepository.countOrdersByTrainingId(1L))
                .thenReturn(Mono.just(0L));
        when(userUtils.getPrincipal()).thenReturn(Mono.just(user));
        when(trainingRepository.findById(1L)).thenReturn(Mono.just(trainingTrainer1));
        when(userUtils.hasPermissionToModifyEntity(user, 3L))
                .thenReturn(Mono.just(false));
        when(trainingRepository.delete(any())).thenReturn(Mono.empty());

        StepVerifier.create(trainingServiceImpl.deleteModel(1L))
                .expectErrorMatches(e -> {
                    assertInstanceOf(PrivateRouteException.class, e);
                    assertEquals("Not allowed!", e.getMessage());
                    return true;
                })
                .verify();

        verify(trainingRepository).countOrdersByTrainingId(1L);
        verify(userUtils).getPrincipal();
        verify(trainingRepository).findById(1L);
        verify(userUtils).hasPermissionToModifyEntity(user, 3L);
        verifyNoMoreInteractions(trainingRepository);
        verifyNoMoreInteractions(userUtils);
        verifyNoMoreInteractions(trainingMapper);
    }


    @Test
    void get_model_by_id_withUser_success() {
        when(userUtils.getPrincipal()).thenReturn(Mono.just(trainer));
        when(trainingRepository.findById(2L)).thenReturn(Mono.just(trainingTrainer1));
        when(trainingMapper.fromModelToResponse(trainingTrainer1)).thenReturn(trainingTrainer1Response);
        when(userUtils.hasPermissionToModifyEntity(trainer, 3L)).thenReturn(Mono.just(true));
        when(userUtils.getUser(3L)).thenReturn(Mono.just(trainer));
        when(userMapper.fromUserCustomToUserDto(trainer)).thenReturn(trainerDto);

        StepVerifier.create(trainingServiceImpl.getModelByIdWithUser(2L))
                .assertNext(responseWithUserDto -> {
                    assertEquals(trainingTrainer1Response, responseWithUserDto.getModel());
                    assertEquals(trainerDto, responseWithUserDto.getUser());
                })
                .verifyComplete();

        verify(userUtils).getPrincipal();
        verify(trainingRepository).findById(2L);
        verify(trainingMapper).fromModelToResponse(trainingTrainer1);
        verify(userUtils).hasPermissionToModifyEntity(trainer, 3L);
        verify(userUtils).getUser(3L);
        verify(userMapper).fromUserCustomToUserDto(trainer);
    }

    @Test
    void get_model_by_id_success() {
        when(userUtils.getPrincipal()).thenReturn(Mono.just(trainer));
        when(trainingRepository.findById(2L)).thenReturn(Mono.just(trainingTrainer1));
        when(trainingMapper.fromModelToResponse(trainingTrainer1)).thenReturn(trainingTrainer1Response);
        when(userUtils.hasPermissionToModifyEntity(trainer, 3L)).thenReturn(Mono.just(true));

        StepVerifier.create(trainingServiceImpl.getModelById(2L))
                .assertNext(trainingResponse -> assertEquals(trainingTrainer1Response, trainingResponse))
                .verifyComplete();

        verify(userUtils).getPrincipal();
        verify(trainingRepository).findById(2L);
        verify(trainingMapper).fromModelToResponse(trainingTrainer1);
        verify(userUtils).hasPermissionToModifyEntity(trainer, 3L);

    }

    @Test
    void get_all_models_success() {
        when(pageableUtilsCustom.isSortingCriteriaValid(pageableBody.getSortingCriteria(), allowedSortingFields))
                .thenReturn(Mono.empty());
        when(pageableUtilsCustom.createPageRequest(pageableBody))
                .thenReturn(Mono.just(pageRequest));
        when(trainingRepository.findAllBy(pageRequest)).thenReturn(Flux.just(trainingTrainer1));
        when(trainingRepository.count()).thenReturn(Mono.just(1L));
        when(pageableUtilsCustom.createPageableResponse(any(Flux.class), any(Mono.class), any(PageRequest.class)))
                .thenReturn(
                        Flux.just(
                                PageableResponse.<TrainingResponse>builder()
                                        .content(trainingTrainer1Response)
                                        .build()
                        )
                );


        StepVerifier.create(trainingServiceImpl.getAllModels(pageableBody))
                .assertNext(trainingResponse -> {
                    assertEquals(trainingTrainer1Response, trainingResponse.getContent());
                })
                .verifyComplete();

        verify(pageableUtilsCustom).isSortingCriteriaValid(pageableBody.getSortingCriteria(), allowedSortingFields);
        verify(pageableUtilsCustom).createPageRequest(pageableBody);
        verify(trainingRepository).findAllBy(pageRequest);

        ArgumentCaptor<PageRequest> pageRequestArgumentCaptor = ArgumentCaptor.forClass(PageRequest.class);
        verify(pageableUtilsCustom).createPageableResponse(any(Flux.class), any(Mono.class), pageRequestArgumentCaptor.capture());
        assertEquals(pageRequest, pageRequestArgumentCaptor.getValue());


    }

    @Test
    void update_training_success() {
        when(userUtils.getPrincipal()).thenReturn(Mono.just(trainer));
        when(trainingRepository.findById(2L)).thenReturn(Mono.just(trainingTrainer1));
        when(trainingMapper.updateModelFromBody(trainingBody, trainingTrainer1)).thenReturn(Mono.just(trainingTrainer1));
        when(trainingRepository.save(trainingTrainer1)).thenReturn(Mono.just(trainingTrainer1));
        when(trainingMapper.fromModelToResponse(trainingTrainer1)).thenReturn(trainingTrainer1Response);

        StepVerifier.create(trainingServiceImpl.updateModel(2L, trainingBody))
                .assertNext(trainingResponse -> assertEquals(trainingTrainer1Response, trainingResponse))
                .verifyComplete();

        verify(userUtils).getPrincipal();
        verify(trainingRepository).findById(2L);
        verify(trainingMapper).updateModelFromBody(trainingBody, trainingTrainer1);
        verify(trainingRepository).save(trainingTrainer1);
        verify(trainingMapper).fromModelToResponse(trainingTrainer1);
    }

    @Test
    void update_training_privateRouteException() {
        when(userUtils.getPrincipal()).thenReturn(Mono.just(user));
        when(trainingRepository.findById(2L)).thenReturn(Mono.just(trainingTrainer1));
        StepVerifier.create(trainingServiceImpl.updateModel(2L, trainingBody))
                .expectErrorMatches(e -> {
                    assertInstanceOf(PrivateRouteException.class, e);
                    assertEquals("Not allowed!", e.getMessage());
                    return true;
                })
                .verify();

        verify(userUtils).getPrincipal();
        verify(trainingRepository).findById(2L);
        verifyNoMoreInteractions(trainingRepository);
        verifyNoMoreInteractions(userUtils);
        verifyNoMoreInteractions(trainingMapper);
    }


    @Test
    void get_models_withUser() {
        when(userUtils.getPrincipal()).thenReturn(Mono.just(trainer));
        when(trainingRepository.findAllById(List.of(1L))).thenReturn(Flux.just(trainingTrainer1));
        when(trainingMapper.fromModelToResponse(trainingTrainer1)).thenReturn(trainingTrainer1Response);
        when(userUtils.hasPermissionToModifyEntity(trainer, 3L)).thenReturn(Mono.just(true));
        when(userUtils.getUser(3L)).thenReturn(Mono.just(trainer));
        when(userMapper.fromUserCustomToUserDto(trainer)).thenReturn(trainerDto);

        StepVerifier.create(trainingServiceImpl.getModelsWithUser(List.of(1L)))
                .assertNext(responseWithUserDto -> {
                    assertEquals(trainingTrainer1Response, responseWithUserDto.getModel());
                    assertEquals(trainerDto, responseWithUserDto.getUser());
                })
                .verifyComplete();

        verify(userUtils).getPrincipal();
        verify(trainingRepository).findAllById(List.of(1L));
        verify(trainingMapper).fromModelToResponse(trainingTrainer1);
        verify(userUtils).hasPermissionToModifyEntity(trainer, 3L);
        verify(userUtils).getUser(3L);
        verify(userMapper).fromUserCustomToUserDto(trainer);

    }

    @Test
    void get_trainings_by_id_in() {

        when(pageableUtilsCustom.isSortingCriteriaValid(pageableBody.getSortingCriteria(), allowedSortingFields))
                .thenReturn(Mono.empty());
        when(pageableUtilsCustom.createPageRequest(pageableBody))
                .thenReturn(Mono.just(pageRequest));
        when(trainingRepository.findAllByIdIn(List.of(1L), pageRequest))
                .thenReturn(Flux.just(trainingTrainer1));
        when(trainingRepository.countAllByIdIn(List.of(1L)))
                .thenReturn(Mono.just(1L));
        when(pageableUtilsCustom.createPageableResponse(any(Flux.class), any(Mono.class), any(PageRequest.class)))
                .thenReturn(Flux.just(pageableResponse));

        StepVerifier.create(trainingServiceImpl.getModelsByIdIn(List.of(1L), pageableBody))
                .assertNext(pageableResponse -> {
                    assertEquals(trainingTrainer1Response, pageableResponse.getContent());
                })
                .verifyComplete();


        verify(pageableUtilsCustom).isSortingCriteriaValid(pageableBody.getSortingCriteria(), allowedSortingFields);
        verify(pageableUtilsCustom).createPageRequest(pageableBody);
        verify(trainingRepository).findAllByIdIn(List.of(1L), pageRequest);
        verify(trainingRepository).countAllByIdIn(List.of(1L));

        ArgumentCaptor<PageRequest> pageRequestArgumentCaptor = ArgumentCaptor.forClass(PageRequest.class);

        verify(pageableUtilsCustom).createPageableResponse(any(Flux.class), any(Mono.class), pageRequestArgumentCaptor.capture());
        assertEquals(pageRequest, pageRequestArgumentCaptor.getValue());


    }

    // title body service

    @Test
    void get_training_guard_with_userLikesDislikes_success() {
        doReturn(Mono.just(responseWithUserDto)).when(trainingServiceImpl).getModelGuardWithUser(trainer, trainingTrainer1, true);
        when(userRepository.findAllById(trainingTrainer1.getUserLikes())).thenReturn(Flux.just(trainer));
        when(userRepository.findAllById(trainingTrainer1.getUserDislikes())).thenReturn(Flux.empty());
        when(userMapper.fromUserCustomToUserDto(trainer)).thenReturn(trainerDto);

        StepVerifier.create(trainingServiceImpl.getModelGuardWithLikesAndDislikes(trainer, trainingTrainer1, true))
                .assertNext(responseWithUserLikesAndDislikes -> {
                    assertEquals(trainingTrainer1Response, responseWithUserLikesAndDislikes.getModel());
                    assertEquals(trainerDto, responseWithUserLikesAndDislikes.getUser());
                    assertEquals(List.of(trainerDto), responseWithUserLikesAndDislikes.getUserLikes());
                    assertEquals(List.of(), responseWithUserLikesAndDislikes.getUserDislikes());
                })
                .verifyComplete();


    }

    @Test
    void get_training_guard_with_userLikesDislikes_error() {
        doReturn(Mono.error(new PrivateRouteException())).when(trainingServiceImpl).getModelGuardWithUser(trainer, trainingTrainer1, true);

        when(userRepository.findAllById(trainingTrainer1.getUserLikes())).thenReturn(Flux.just(trainer));
        when(userRepository.findAllById(trainingTrainer1.getUserDislikes())).thenReturn(Flux.empty());

        StepVerifier.create(trainingServiceImpl.getModelGuardWithLikesAndDislikes(trainer, trainingTrainer1, true))
                .expectErrorMatches(e -> {
                    assertInstanceOf(PrivateRouteException.class, e);
                    assertEquals("Not allowed!", e.getMessage());
                    return true;
                })
                .verify();


        verify(userRepository).findAllById(trainingTrainer1.getUserLikes());
        verify(userRepository).findAllById(trainingTrainer1.getUserDislikes());

    }

    @Test
    void react_to_model_like() {
        when(userUtils.getPrincipal()).thenReturn(Mono.just(trainer));
        when(trainingRepository.findById(2L)).thenReturn(Mono.just(trainingTrainer1))
                .thenReturn(Mono.just(trainingTrainer1));
        when(entitiesUtils.setReaction(trainingTrainer1, trainer, "like")).thenReturn(Mono.just(trainingTrainer1));
        when(trainingRepository.save(trainingTrainer1)).thenReturn(Mono.just(trainingTrainer1));
        when(trainingMapper.fromModelToResponse(trainingTrainer1)).thenReturn(trainingTrainer1Response);

        StepVerifier.create(trainingServiceImpl.reactToModel(2L, "like"))
                .assertNext(trainingResponse -> assertEquals(trainingTrainer1Response, trainingResponse))
                .verifyComplete();

        verify(userUtils).getPrincipal();
        verify(trainingRepository).findById(2L);
        verify(entitiesUtils).setReaction(trainingTrainer1, trainer, "like");
        verify(trainingRepository).save(trainingTrainer1);
        verify(trainingMapper).fromModelToResponse(trainingTrainer1);


    }

    @Test
    void react_to_model_dislike() {
        when(userUtils.getPrincipal()).thenReturn(Mono.just(trainer));
        when(trainingRepository.findById(2L)).thenReturn(Mono.just(trainingTrainer1))
                .thenReturn(Mono.just(trainingTrainer1));
        when(entitiesUtils.setReaction(trainingTrainer1, trainer, "dislike")).thenReturn(Mono.just(trainingTrainer1));
        when(trainingRepository.save(trainingTrainer1)).thenReturn(Mono.just(trainingTrainer1));
        when(trainingMapper.fromModelToResponse(trainingTrainer1)).thenReturn(trainingTrainer1Response);

        StepVerifier.create(trainingServiceImpl.reactToModel(2L, "dislike"))
                .assertNext(trainingResponse -> assertEquals(trainingTrainer1Response, trainingResponse))
                .verifyComplete();

        verify(userUtils).getPrincipal();
        verify(trainingRepository).findById(2L);
        verify(entitiesUtils).setReaction(trainingTrainer1, trainer, "dislike");
        verify(trainingRepository).save(trainingTrainer1);
        verify(trainingMapper).fromModelToResponse(trainingTrainer1);
    }

    @Test
    void get_model_by_id_with_userLikesAndDislikes_success() {
        when(userUtils.getPrincipal()).thenReturn(Mono.just(trainer));
        when(trainingRepository.findById(2L)).thenReturn(Mono.just(trainingTrainer1));
        doReturn(Mono.just(responseWithUserLikesAndDislikes)).when(trainingServiceImpl).getModelGuardWithLikesAndDislikes(trainer, trainingTrainer1, false);

        StepVerifier.create(trainingServiceImpl.getModelByIdWithUserLikesAndDislikes(2L))
                .assertNext(responseWithUserLikesAndDislikes -> {
                    assertEquals(trainingTrainer1Response, responseWithUserLikesAndDislikes.getModel());
                    assertEquals(trainerDto, responseWithUserLikesAndDislikes.getUser());
                    assertEquals(List.of(trainerDto), responseWithUserLikesAndDislikes.getUserLikes());
                    assertEquals(List.of(), responseWithUserLikesAndDislikes.getUserDislikes());
                })
                .verifyComplete();

        verify(userUtils).getPrincipal();
        verify(trainingRepository).findById(2L);
        verify(trainingServiceImpl).getModelGuardWithLikesAndDislikes(trainer, trainingTrainer1, false);


    }

    @Test
    void approve_model_success() {
        when(trainingRepository.findById(2L)).thenReturn(Mono.just(trainingTrainer2));
        when(trainingRepository.save(trainingTrainer2)).thenReturn(Mono.just(trainingTrainer2));
        when(trainingMapper.fromModelToResponse(trainingTrainer2)).then(
                i -> {
                    Training t = i.getArgument(0);
                    return TrainingResponse
                            .builder()
                            .id(t.getId())
                            .title(t.getTitle())
                            .body(t.getBody())
                            .price(t.getPrice())
                            .approved(t.isApproved())
                            .userId(t.getUserId())
                            .exercises(t.getExercises())
                            .userLikes(t.getUserLikes())
                            .userDislikes(t.getUserDislikes())
                            .build();
                }
        );

        StepVerifier.create(trainingServiceImpl.approveModel(2L))
                .assertNext(trainingResponse -> assertTrue(trainingTrainer2.isApproved()))
                .verifyComplete();

        verify(trainingRepository).findById(2L);
        verify(trainingRepository).save(trainingTrainer2);
        verify(trainingMapper).fromModelToResponse(trainingTrainer2);
    }

    @Test
    void approve_model_not_found() {
        when(trainingRepository.findById(2L)).thenReturn(Mono.empty());

        StepVerifier.create(trainingServiceImpl.approveModel(2L))
                .expectErrorMatches(e -> {
                    assertInstanceOf(NotFoundEntity.class, e);
                    assertEquals("training", ((NotFoundEntity) e).getName());
                    assertEquals(2L, ((NotFoundEntity) e).getId());
                    return true;
                })
                .verify();

        verify(trainingRepository).findById(2L);
        verifyNoMoreInteractions(trainingRepository);
        verifyNoMoreInteractions(trainingMapper);
    }

    @Test
    void approve_model_already_approved() {
        when(trainingRepository.findById(2L)).thenReturn(Mono.just(trainingTrainer1));

        StepVerifier.create(trainingServiceImpl.approveModel(2L))
                .expectErrorMatches(e -> {
                    assertInstanceOf(IllegalActionException.class, e);
                    assertEquals("training with id 2 is already approved!", e.getMessage());
                    return true;
                })
                .verify();

        verify(trainingRepository).findById(2L);
        verifyNoMoreInteractions(trainingRepository);
        verifyNoMoreInteractions(trainingMapper);
    }


    @Test
    void create_training_success() {
        when(exerciseService.validIds(trainingBody.getExercises())).thenReturn(Mono.empty());
        when(userUtils.getPrincipal()).thenReturn(Mono.just(trainer));
        when(trainingMapper.updateModelFromBody(trainingBody, new Training())).then(
                i -> {
                    Training t = i.getArgument(1);
                    t.setTitle(trainingBody.getTitle());
                    t.setBody(trainingBody.getBody());
                    t.setPrice(trainingBody.getPrice());
                    t.setExercises(trainingBody.getExercises());
                    t.setUserId(trainer.getId());
                    t.setUserDislikes(List.of());
                    t.setUserLikes(List.of());
                    return Mono.just(t);
                }
        );

        when(trainingRepository.save(any(Training.class))).then(
                i -> {
                    Training t = i.getArgument(0);
                    return Mono.just(t);
                }

        );
        when(trainingMapper.fromModelToResponse(any(Training.class))).
                then(
                        i -> {
                            Training t = i.getArgument(0);
                            return TrainingResponse
                                    .builder()
                                    .id(t.getId())
                                    .title(t.getTitle())
                                    .body(t.getBody())
                                    .price(t.getPrice())
                                    .approved(t.isApproved())
                                    .userId(t.getUserId())
                                    .exercises(t.getExercises())
                                    .userLikes(t.getUserLikes())
                                    .userDislikes(t.getUserDislikes())
                                    .build();
                        }
                );

        StepVerifier.create(trainingServiceImpl.createModel(trainingBody))
                .assertNext(trainingResponse -> {
                    assertEquals(trainingResponse.getUserId(), trainer.getId());
                    assertEquals(trainingResponse.getTitle(), trainingBody.getTitle());
                    assertEquals(trainingResponse.getBody(), trainingBody.getBody());
                    assertEquals(trainingResponse.getPrice(), trainingBody.getPrice());
                    assertEquals(trainingResponse.getExercises(), trainingBody.getExercises());
                    assertFalse(trainingResponse.isApproved());
                    assertEquals(trainingResponse.getUserLikes(), List.of());
                    assertEquals(trainingResponse.getUserDislikes(), List.of());
                })
                .verifyComplete();

        verify(exerciseService).validIds(trainingBody.getExercises());
        verify(userUtils).getPrincipal();

        ArgumentCaptor<Training> trainingArgumentCaptor = ArgumentCaptor.forClass(Training.class);

        verify(trainingRepository).save(trainingArgumentCaptor.capture());
        assertEquals(trainer.getId(), trainingArgumentCaptor.getValue().getUserId());

        verify(trainingMapper).fromModelToResponse(trainingArgumentCaptor.getValue());
        assertEquals(trainer.getId(), trainingArgumentCaptor.getValue().getUserId());


    }

    @Test
    void get_trainings_withExercises_success() {
        when(trainingRepository.findByApprovedAndId(true, 2L)).thenReturn(Mono.just(trainingTrainer1));
        when(exerciseService.getExercisesWithUserByIds(List.of(2L, 3L))).thenReturn(Flux.just(exercise2WithUserDto, exercise3WithUserDto));
        when(trainingMapper.fromModelToResponse(trainingTrainer1)).thenReturn(trainingTrainer1Response);

        StepVerifier.create(trainingServiceImpl.getTrainingWithExercises(2L, true))
                .assertNext(trainingResponse -> {
                    assertEquals(trainingTrainer1Response, trainingResponse.getEntity());
                    assertEquals(List.of(exercise2WithUserDto, exercise3WithUserDto), trainingResponse.getChildren());
                })
                .verifyComplete();

        verify(trainingRepository).findByApprovedAndId(true, 2L);
        verify(exerciseService).getExercisesWithUserByIds(List.of(2L, 3L));
        verify(trainingMapper).fromModelToResponse(trainingTrainer1);

    }

    @Test
    void get_trainings_withExercises_notFound() {
        when(trainingRepository.findByApprovedAndId(true, 2L)).thenReturn(Mono.empty());

        StepVerifier.create(trainingServiceImpl.getTrainingWithExercises(2L, true))
                .expectErrorMatches(e -> {
                    assertInstanceOf(NotFoundEntity.class, e);
                    assertEquals("training", ((NotFoundEntity) e).getName());
                    assertEquals(2L, ((NotFoundEntity) e).getId());
                    return true;
                })
                .verify();

        verify(trainingRepository).findByApprovedAndId(true, 2L);
        verifyNoMoreInteractions(trainingRepository);
        verifyNoMoreInteractions(exerciseService);
        verifyNoMoreInteractions(trainingMapper);
    }

    @Test
    void get_trainings_with_exercises() {
        when(trainingRepository.findAllByApprovedAndIdIn(true, List.of(2L)))
                .thenReturn(Flux.just(trainingTrainer1));
        when(exerciseService.getExercisesWithUserByIds(List.of(2L, 3L))).thenReturn(Flux.just(exercise2WithUserDto, exercise3WithUserDto));
        when(trainingMapper.fromModelToResponse(trainingTrainer1)).thenReturn(trainingTrainer1Response);

        StepVerifier.create(trainingServiceImpl.getTrainingsWithExercises(List.of(2L), true))
                .assertNext(trainingResponse -> {
                    assertEquals(trainingTrainer1Response, trainingResponse.getEntity());
                    assertEquals(List.of(exercise2WithUserDto, exercise3WithUserDto), trainingResponse.getChildren());
                })
                .verifyComplete();

        verify(trainingRepository).findAllByApprovedAndIdIn(true, List.of(2L));
        verify(exerciseService).getExercisesWithUserByIds(List.of(2L, 3L));
        verify(trainingMapper).fromModelToResponse(trainingTrainer1);


    }

    @Test
    void get_total_price() {
        when(trainingRepository.sumPriceByIds(List.of(1L, 2L))).thenReturn(Mono.just(100.0));

        StepVerifier.create(trainingServiceImpl.getTotalPriceById(List.of(1L, 2L)))
                .assertNext(price -> assertEquals(100.0, price))
                .verifyComplete();

        verify(trainingRepository).sumPriceByIds(List.of(1L, 2L));
    }

    @Test
    void valid_ids() {
        when(entitiesUtils.validIds(List.of(1L, 2L, 3L), trainingRepository, "training")).thenReturn(Mono.empty());

        StepVerifier.create(trainingServiceImpl.validIds(List.of(1L, 2L, 3L)))
                .verifyComplete();

        verify(entitiesUtils).validIds(List.of(1L, 2L, 3L), trainingRepository, "training");

    }

    @Test
    void get_trainings_title_admin_success() {
        String title = "         title           ";
        when(userUtils.getPrincipal()).thenReturn(Mono.just(admin));
        when(pageableUtilsCustom.isSortingCriteriaValid(pageableBody.getSortingCriteria(), allowedSortingFields))
                .thenReturn(Mono.empty());
        when(pageableUtilsCustom.createPageRequest(pageableBody)).thenReturn(Mono.just(pageRequest));
        when(trainingRepository.findAllByTitleContainingIgnoreCaseAndApproved(any(String.class),
                eq(false), eq(pageRequest)))
                .thenReturn(Flux.just(trainingTrainer2));
        when(trainingRepository.countAllByTitleContainingIgnoreCaseAndApproved((any(String.class)),
                eq(false)))
                .thenReturn(Mono.just(1L));
        when(trainingMapper.fromModelToResponse(trainingTrainer2)).thenReturn(trainingTrainer2Response);
        when(pageableUtilsCustom.createPageableResponse(any(Flux.class), any(Mono.class), any(PageRequest.class)))
                .then(i -> {
                    Flux<TrainingResponse> flux = i.getArgument(0);
                    return Flux.just(PageableResponse.<TrainingResponse>builder()
                            .content(Objects.requireNonNull(flux.collectList().block()).get(0))
                            .build());
                });

        StepVerifier.create(trainingServiceImpl.getModelsTitle(title, false, pageableBody))
                .assertNext(trainingResponse -> {
                    assertEquals(trainingTrainer2Response, trainingResponse.getContent());
                })
                .verifyComplete();

        verify(userUtils).getPrincipal();
        verify(pageableUtilsCustom).isSortingCriteriaValid(pageableBody.getSortingCriteria(), allowedSortingFields);
        verify(pageableUtilsCustom).createPageRequest(pageableBody);

        ArgumentCaptor<String> titleArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(trainingRepository).findAllByTitleContainingIgnoreCaseAndApproved(titleArgumentCaptor.capture(), eq(false), eq(pageRequest));
        assertEquals("title", titleArgumentCaptor.getValue());

        verify(trainingRepository).countAllByTitleContainingIgnoreCaseAndApproved(titleArgumentCaptor.capture(), eq(false));
        assertEquals("title", titleArgumentCaptor.getValue());

        verify(trainingMapper).fromModelToResponse(trainingTrainer2);

        ArgumentCaptor<PageRequest> pageRequestArgumentCaptor = ArgumentCaptor.forClass(PageRequest.class);
        verify(pageableUtilsCustom).createPageableResponse(any(Flux.class), any(Mono.class), pageRequestArgumentCaptor.capture());
        assertEquals(pageRequest, pageRequestArgumentCaptor.getValue());


    }

    @Test
    void get_trainings_title_admin_error() {
        String title = "         title           ";
        when(userUtils.getPrincipal()).thenReturn(Mono.just(user));
        when(pageableUtilsCustom.isSortingCriteriaValid(pageableBody.getSortingCriteria(), allowedSortingFields))
                .thenReturn(Mono.empty());
        when(pageableUtilsCustom.createPageRequest(pageableBody)).thenReturn(Mono.just(pageRequest));

        StepVerifier.create(trainingServiceImpl.getModelsTitle(title, false, pageableBody))
                .expectErrorMatches(e -> {
                    assertInstanceOf(PrivateRouteException.class, e);
                    assertEquals("Not allowed!", e.getMessage());
                    return true;
                })
                .verify();

        verify(userUtils).getPrincipal();
        verify(pageableUtilsCustom).isSortingCriteriaValid(pageableBody.getSortingCriteria(), allowedSortingFields);
        verify(pageableUtilsCustom).createPageRequest(pageableBody);
        verifyNoMoreInteractions(userUtils);
        verifyNoMoreInteractions(pageableUtilsCustom);
        verifyNoMoreInteractions(trainingRepository);
        verifyNoInteractions(trainingMapper);
    }


    @Test
    void get_trainings_title_user_success() {
        String title = "         title           ";
        when(userUtils.getPrincipal()).thenReturn(Mono.just(user));
        when(pageableUtilsCustom.isSortingCriteriaValid(pageableBody.getSortingCriteria(), allowedSortingFields))
                .thenReturn(Mono.empty());
        when(pageableUtilsCustom.createPageRequest(pageableBody)).thenReturn(Mono.just(pageRequest));
        when(trainingRepository.findAllByTitleContainingIgnoreCaseAndApproved(any(String.class),
                eq(true), eq(pageRequest)))
                .thenReturn(Flux.just(trainingTrainer1));
        when(trainingRepository.countAllByTitleContainingIgnoreCaseAndApproved((any(String.class)),
                eq(true)))
                .thenReturn(Mono.just(1L));
        when(pageableUtilsCustom.createPageableResponse(any(Flux.class), any(Mono.class), any(PageRequest.class)))
                .thenReturn(Flux.just(pageableResponse));

        StepVerifier.create(trainingServiceImpl.getModelsTitle(title, true, pageableBody))
                .assertNext(trainingResponse -> {
                    assertEquals(trainingTrainer1Response, trainingResponse.getContent());
                })
                .verifyComplete();

        verify(userUtils).getPrincipal();
        verify(pageableUtilsCustom).isSortingCriteriaValid(pageableBody.getSortingCriteria(), allowedSortingFields);
        verify(pageableUtilsCustom).createPageRequest(pageableBody);

        ArgumentCaptor<String> titleArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(trainingRepository).findAllByTitleContainingIgnoreCaseAndApproved(titleArgumentCaptor.capture(), eq(true), eq(pageRequest));
        assertEquals("title", titleArgumentCaptor.getValue());

        verify(trainingRepository).countAllByTitleContainingIgnoreCaseAndApproved(titleArgumentCaptor.capture(), eq(true));
        assertEquals("title", titleArgumentCaptor.getValue());


        ArgumentCaptor<PageRequest> pageRequestArgumentCaptor = ArgumentCaptor.forClass(PageRequest.class);
        verify(pageableUtilsCustom).createPageableResponse(any(Flux.class), any(Mono.class), pageRequestArgumentCaptor.capture());
        assertEquals(pageRequest, pageRequestArgumentCaptor.getValue());


    }

    @Test
    void get_training_with_order_count_success() {

        TrainingWithOrderCount trainingTrainer1WOC = TrainingWithOrderCount.builder()
                .id(2L)
                .approved(true)
                .body("Training body 2")
                .title("Training Title 2")
                .userId(3L)
                .price(29.99)
                .userLikes(List.of(1L))
                .userDislikes(List.of())
                .exercises(List.of(2L, 3L))
                .orderCount(1L)
                .build();

        TrainingResponseWithOrderCount trTrainer1WOCResp = TrainingResponseWithOrderCount.builder()
                .id(2L)
                .approved(true)
                .body("Training body 2")
                .title("Training Title 2")
                .userId(3L)
                .price(29.99)
                .userLikes(List.of(1L))
                .userDislikes(List.of())
                .exercises(List.of(2L, 3L))
                .orderCount(1L)
                .user(trainerDto)
                .build();
        doReturn(Mono.just(trainingTrainer1)).when(trainingServiceImpl).getModelById(2L);
        when(trainingRepository.findByIdWithOrderCount(2L)).thenReturn(Mono.just(trainingTrainer1WOC));
        when(trainingMapper.fromModelToResponseWithOrderCount(trainingTrainer1WOC)).thenReturn(trTrainer1WOCResp);
        when(userUtils.getUser(trainingTrainer1WOC.getUserId())).thenReturn(Mono.just(trainer));
        when(userMapper.fromUserCustomToUserDto(trainer)).thenReturn(trainerDto);

        StepVerifier.create(trainingServiceImpl.getTrainingWithOrderCount(2L))
                .assertNext(trainingResponse -> {
                    assertEquals(trTrainer1WOCResp, trainingResponse);
                })
                .verifyComplete();


        verify(trainingServiceImpl).getModelById(2L);
        verify(trainingRepository).findByIdWithOrderCount(2L);
        verify(trainingMapper).fromModelToResponseWithOrderCount(trainingTrainer1WOC);
        verify(userUtils).getUser(trainingTrainer1WOC.getUserId());
        verify(userMapper).fromUserCustomToUserDto(trainer);


    }

    @Test
    void get_training_with_order_count_not_found() {
        doReturn(Mono.empty()).when(trainingServiceImpl).getModelById(2L);
        when(trainingRepository.findByIdWithOrderCount(2L)).thenReturn(Mono.empty());

        StepVerifier.create(trainingServiceImpl.getTrainingWithOrderCount(2L))
                .expectErrorMatches(e -> {
                    assertInstanceOf(NotFoundEntity.class, e);
                    assertEquals("training", ((NotFoundEntity) e).getName());
                    assertEquals(2L, ((NotFoundEntity) e).getId());
                    return true;
                })
                .verify();

        verify(trainingServiceImpl).getModelById(2L);
        verify(trainingRepository).findByIdWithOrderCount(2L);
        verifyNoMoreInteractions(trainingRepository);
        verifyNoInteractions(trainingMapper);
        verifyNoInteractions(userUtils);
        verifyNoInteractions(userMapper);


    }

    @Test
    void get_models_approved_no_title_success() {
        doReturn(Flux.just(pageableResponse)).when(trainingServiceImpl).getModelsTitle(null, true, pageableBody);

        StepVerifier.create(trainingServiceImpl.getModelsApproved(pageableBody))
                .assertNext(trainingResponse -> {
                    assertEquals(trainingTrainer1Response, trainingResponse.getContent());
                })
                .verifyComplete();

        verify(trainingServiceImpl).getModelsTitle(null, true, pageableBody);
    }

    @Test
    void get_models_approved_with_title_success() {
        doReturn(Flux.just(pageableResponse)).when(trainingServiceImpl).getModelsTitle("title", true, pageableBody);

        StepVerifier.create(trainingServiceImpl.getModelsApproved("title", pageableBody))
                .assertNext(trainingResponse -> {
                    assertEquals(trainingTrainer1Response, trainingResponse.getContent());
                })
                .verifyComplete();

        verify(trainingServiceImpl).getModelsTitle("title", true, pageableBody);
    }

    @Test
    void get_models_approved_no_title_error() {
        doReturn(Flux.error(new PrivateRouteException())).when(trainingServiceImpl).getModelsTitle(null, true, pageableBody);

        StepVerifier.create(trainingServiceImpl.getModelsApproved(pageableBody))
                .expectErrorMatches(e -> {
                    assertInstanceOf(PrivateRouteException.class, e);
                    assertEquals("Not allowed!", e.getMessage());
                    return true;
                })
                .verify();

        verify(trainingServiceImpl).getModelsTitle(null, true, pageableBody);
    }

    @Test
    void get_models_approved_with_title_error() {
        doReturn(Flux.error(new PrivateRouteException())).when(trainingServiceImpl).getModelsTitle("title", true, pageableBody);

        StepVerifier.create(trainingServiceImpl.getModelsApproved("title", pageableBody))
                .expectErrorMatches(e -> {
                    assertInstanceOf(PrivateRouteException.class, e);
                    assertEquals("Not allowed!", e.getMessage());
                    return true;
                })
                .verify();

        verify(trainingServiceImpl).getModelsTitle("title", true, pageableBody);
    }

    @Test
    void get_all_models_title() {
        when(pageableUtilsCustom.isSortingCriteriaValid(pageableBody.getSortingCriteria(), allowedSortingFields))
                .thenReturn(Mono.empty());
        when(pageableUtilsCustom.createPageRequest(pageableBody))
                .thenReturn(Mono.just(pageRequest));
        when(trainingRepository.findAllByTitleContainingIgnoreCase(any(String.class), eq(pageRequest))
        ).thenReturn(Flux.just(trainingTrainer1));
        when(trainingRepository.countAllByTitleContainingIgnoreCase(any(String.class)))
                .thenReturn(Mono.just(1L));
        when(pageableUtilsCustom.createPageableResponse(any(Flux.class), any(Mono.class), any(PageRequest.class)))
                .thenReturn(
                        Flux.just(
                                PageableResponse.<TrainingResponse>builder()
                                        .content(trainingTrainer1Response)
                                        .build()
                        )
                );

        StepVerifier.create(trainingServiceImpl.getAllModels("title", pageableBody))
                .assertNext(trainingResponse -> {
                    assertEquals(trainingTrainer1Response, trainingResponse.getContent());
                })
                .verifyComplete();

        verify(pageableUtilsCustom).isSortingCriteriaValid(pageableBody.getSortingCriteria(), allowedSortingFields);
        verify(pageableUtilsCustom).createPageRequest(pageableBody);
        verify(trainingRepository).findAllByTitleContainingIgnoreCase("title", pageRequest);

        ArgumentCaptor<PageRequest> pageRequestArgumentCaptor = ArgumentCaptor.forClass(PageRequest.class);
        verify(pageableUtilsCustom).createPageableResponse(any(Flux.class), any(Mono.class), pageRequestArgumentCaptor.capture());
        assertEquals(pageRequest, pageRequestArgumentCaptor.getValue());
    }

    static Stream<String> emptyTitle() {
        return Stream.of("", null, "     ");
    }

    @ParameterizedTest
    @MethodSource("emptyTitle")
    void get_models_trainer_emptyTitle(String title) {
        when(userUtils.getPrincipal()).thenReturn(Mono.just(trainer));
        when(userUtils.existsTrainerOrAdmin(trainer.getId())).thenReturn(Mono.empty());
        when(pageableUtilsCustom.isSortingCriteriaValid(pageableBody.getSortingCriteria(), allowedSortingFields))
                .thenReturn(Mono.empty());
        when(pageableUtilsCustom.createPageRequest(pageableBody))
                .thenReturn(Mono.just(pageRequest));
        doReturn(Mono.empty()).when(trainingServiceImpl).privateRoute(true, trainer, trainer.getId());
        when(trainingRepository.findAllByUserIdAndTitleContainingIgnoreCase(eq(trainer.getId()), any(String.class),
                eq(pageRequest)))
                .thenReturn(Flux.just(trainingTrainer1));
        when(trainingRepository.countAllByUserIdAndTitleContainingIgnoreCase(eq(trainer.getId()), any(String.class)))
                .thenReturn(Mono.just(1L));
        when(pageableUtilsCustom.createPageableResponse(any(Flux.class), any(Mono.class), any(PageRequest.class)))
                .thenReturn(
                        Flux.just(
                                PageableResponse.<TrainingResponse>builder()
                                        .content(trainingTrainer1Response)
                                        .build()
                        )
                );

        StepVerifier.create(trainingServiceImpl.getModelsTrainer(title, trainer.getId(), pageableBody))
                .assertNext(trainingResponse -> {
                    assertEquals(trainingTrainer1Response, trainingResponse.getContent());
                })
                .verifyComplete();


        verify(userUtils).getPrincipal();
        verify(userUtils).existsTrainerOrAdmin(trainer.getId());
        verify(pageableUtilsCustom).isSortingCriteriaValid(pageableBody.getSortingCriteria(), allowedSortingFields);
        verify(pageableUtilsCustom).createPageRequest(pageableBody);
        verify(trainingServiceImpl).privateRoute(true, trainer, trainer.getId());
        ArgumentCaptor<String> titleArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(trainingRepository).findAllByUserIdAndTitleContainingIgnoreCase(eq(trainer.getId()), titleArgumentCaptor.capture(), eq(pageRequest));
        assertEquals("", titleArgumentCaptor.getValue());
        verify(trainingRepository).countAllByUserIdAndTitleContainingIgnoreCase(eq(trainer.getId()), titleArgumentCaptor.capture());
        assertEquals("", titleArgumentCaptor.getValue());

        ArgumentCaptor<PageRequest> pageRequestArgumentCaptor = ArgumentCaptor.forClass(PageRequest.class);
        verify(pageableUtilsCustom).createPageableResponse(any(Flux.class), any(Mono.class), pageRequestArgumentCaptor.capture());
        assertEquals(pageRequest, pageRequestArgumentCaptor.getValue());

    }


}