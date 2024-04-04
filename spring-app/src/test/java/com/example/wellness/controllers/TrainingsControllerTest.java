package com.example.wellness.controllers;

import com.example.wellness.dto.common.PageableBody;
import com.example.wellness.dto.common.UserDto;
import com.example.wellness.dto.common.response.*;
import com.example.wellness.dto.exercise.ExerciseResponse;
import com.example.wellness.dto.training.TrainingBody;
import com.example.wellness.dto.training.TrainingResponse;
import com.example.wellness.dto.training.TrainingResponseWithOrderCount;
import com.example.wellness.enums.Role;
import com.example.wellness.exceptions.action.IllegalActionException;
import com.example.wellness.exceptions.action.PrivateRouteException;
import com.example.wellness.exceptions.action.SubEntityUsed;
import com.example.wellness.exceptions.common.SortingCriteriaException;
import com.example.wellness.exceptions.notFound.NotFoundEntity;
import com.example.wellness.filters.AuthFilter;
import com.example.wellness.hateos.CustomEntityModel;
import com.example.wellness.hateos.linking.impl.responses.ExerciseReactiveResponseBuilder;
import com.example.wellness.hateos.linking.impl.responses.TrainingReactiveResponseBuilder;
import com.example.wellness.models.Exercise;
import com.example.wellness.models.Order;
import com.example.wellness.models.Training;
import com.example.wellness.models.user.UserCustom;
import com.example.wellness.services.TrainingService;
import com.example.wellness.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;


import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = TrainingController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AuthFilter.class)
}
)
@ActiveProfiles("tc")
@Slf4j
public class TrainingsControllerTest {
    private final String TEST_EMAIL = "john.doe@example.com";
    @Autowired
    private WebTestClient webClient;


    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private UserService userService;

    @MockBean
    private TrainingService trainingService;
    @MockBean
    private TrainingReactiveResponseBuilder trainingReactiveResponseBuilder;
    @MockBean
    private ExerciseReactiveResponseBuilder exerciseReactiveResponseBuilder;

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

    UserDto adminDto = UserDto.builder()
            .id(2L)
            .firstName("Jane")
            .lastName("Doe")
            .email("jane.doe@example.com")
            .role(Role.ROLE_ADMIN)
            .build();

    UserDto trainerDto = UserDto.builder().id(3L)
            .firstName("Jim")
            .lastName("Doe")
            .email("jim.doe@example.com")
            .role(Role.ROLE_TRAINER)
            .build();

    UserDto userDto = UserDto.builder()
            .id(1L)
            .firstName("John")
            .lastName("Doe")
            .email("john.doe@example.com")
            .role(Role.ROLE_USER)
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
            .userLikes(new ArrayList<>())
            .userDislikes(new ArrayList<>())
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

    TrainingResponse trainingAdminResponse = TrainingResponse.builder()
            .id(1L)
            .approved(true)
            .body("Training body 1")
            .title("Training Title 1")
            .userId(2L)
            .userLikes(new ArrayList<>())
            .userDislikes(new ArrayList<>())
            .price(19.99)
            .exercises(List.of(exerciseAdmin1.getId()))
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

    ExerciseResponse exerciseTrainer1Response = ExerciseResponse.builder()
            .id(3L)
            .muscleGroups(List.of("chest", "back"))
            .approved(true)
            .body("Exercise body 2")
            .title("Exercise Title 2")
            .userId(3L)
            .build();
    ExerciseResponse exerciseTrainer2Response = ExerciseResponse.builder().muscleGroups(List.of("chest", "legs"))
            .id(4L)
            .approved(true)
            .body("Exercise body 3")
            .title("Exercise Title 3")
            .userId(3L)
            .build();

    ResponseWithUserDto<ExerciseResponse> exerciseTrainer1WithUser = ResponseWithUserDto.<ExerciseResponse>builder()
            .model(exerciseTrainer1Response)
            .user(trainerDto)
            .build();


    ResponseWithUserDto<ExerciseResponse> exerciseTrainer2WithUser = ResponseWithUserDto.<ExerciseResponse>builder()
            .model(exerciseTrainer2Response)
            .user(trainerDto)
            .build();


    ResponseWithUserDtoEntity<ExerciseResponse> exerciseTrainer1WithUserEntity = ResponseWithUserDtoEntity.<ExerciseResponse>builder()
            .model(CustomEntityModel.of(exerciseTrainer1Response))
            .user(CustomEntityModel.of(trainerDto))
            .build();
    ResponseWithUserDtoEntity<ExerciseResponse> exerciseTrainer2WithUserEntity = ResponseWithUserDtoEntity.<ExerciseResponse>builder()
            .model(CustomEntityModel.of(exerciseTrainer2Response))
            .user(CustomEntityModel.of(trainerDto))
            .build();

    ResponseWithChildList<TrainingResponse, ResponseWithUserDto<ExerciseResponse>> trainingTrainer1WithExercises =
            new ResponseWithChildList<>(trainingTrainer1Response, List.of(
                    exerciseTrainer1WithUser, exerciseTrainer2WithUser
            ));

    ResponseWithChildListEntity<TrainingResponse, ResponseWithUserDtoEntity<ExerciseResponse>> trainingTrainer1WithExercisesEntity =
            new ResponseWithChildListEntity<>(CustomEntityModel.of(trainingTrainer1Response),
                    List.of(
                            exerciseTrainer1WithUserEntity, exerciseTrainer2WithUserEntity
                    ));

    CustomEntityModel<TrainingResponse> trainingTrainer1Model = CustomEntityModel.of(trainingTrainer1Response);
    CustomEntityModel<TrainingResponse> trainingTrainer2Model = CustomEntityModel.of(trainingTrainer2Response);
    CustomEntityModel<TrainingResponse> trainingAdminModel = CustomEntityModel.of(trainingAdminResponse);

    ResponseWithUserLikesAndDislikes<TrainingResponse> trainingTrainer1WithULUD = ResponseWithUserLikesAndDislikes.<TrainingResponse>builder()
            .model(trainingTrainer1Response)
            .user(adminDto)
            .userLikes(List.of(adminDto))
            .userDislikes(new ArrayList<>())
            .build();

    ResponseWithUserLikesAndDislikesEntity<TrainingResponse> trainingTrainer1WithULUDEntity =
            new ResponseWithUserLikesAndDislikesEntity<>(trainingTrainer1Model, CustomEntityModel.of(adminDto),
                    List.of(CustomEntityModel.of(userDto)), new ArrayList<>());
    PageableResponse<TrainingResponse> trainer1PR =
            PageableResponse.<TrainingResponse>builder()
                    .content(trainingTrainer1Response)
                    .pageInfo(PageInfo.builder()
                            .totalPages(1)
                            .totalElements(1)
                            .pageSize(10)
                            .totalPages(1)
                            .build())
                    .build();

    //    PageableResponse<TrainingResponse> allTrainingsPR =
//            PageableResponse.<TrainingResponse>builder()
//                            .content(trainingTrainer1Response)
//                            .pageInfo(PageInfo.builder()
//                                    .totalPages(1)
//                                    .totalElements(1)
//                                    .pageSize(10)
//                                    .totalPages(1)
//                                    .build())
//                            .build(),
    PageableResponse<TrainingResponse> trainer2PR = PageableResponse.<TrainingResponse>builder()
            .content(trainingTrainer2Response)
            .pageInfo(PageInfo.builder()
                    .totalPages(1)
                    .totalElements(1)
                    .pageSize(10)
                    .totalPages(1)
                    .build())
            .build();
    PageableResponse<TrainingResponse> adminPR =
            PageableResponse.<TrainingResponse>builder()
                    .content(trainingAdminResponse)
                    .pageInfo(PageInfo.builder()
                            .totalPages(1)
                            .totalElements(1)
                            .pageSize(10)
                            .totalPages(1)
                            .build())
                    .build();
    PageableResponse<CustomEntityModel<TrainingResponse>> trainer1CustomPR =
            PageableResponse.<CustomEntityModel<TrainingResponse>>builder()
                    .content(trainingTrainer1Model)
                    .pageInfo(PageInfo.builder()
                            .totalPages(1)
                            .totalElements(1)
                            .pageSize(10)
                            .totalPages(1)
                            .build())
                    .build();

    //    Flux<PageableResponse<CustomEntityModel<TrainingResponse>>> allTrainingsCustomPR =
//            Flux.just(PageableResponse.<CustomEntityModel<TrainingResponse>>builder()
//                            .content(trainingTrainer1Model)
//                            .pageInfo(PageInfo.builder()
//                                    .totalPages(1)
//                                    .totalElements(1)
//                                    .pageSize(10)
//                                    .totalPages(1)
//                                    .build())
//                            .build(),
    PageableResponse<CustomEntityModel<TrainingResponse>> trainer2CustomPR = PageableResponse.<CustomEntityModel<TrainingResponse>>builder()
            .content(trainingTrainer2Model)
            .pageInfo(PageInfo.builder()
                    .totalPages(1)
                    .totalElements(1)
                    .pageSize(10)
                    .totalPages(1)
                    .build())
            .build();
    PageableResponse<CustomEntityModel<TrainingResponse>> adminCustomPR = PageableResponse.<CustomEntityModel<TrainingResponse>>builder()
            .content(trainingAdminModel)
            .pageInfo(PageInfo.builder()
                    .totalPages(1)
                    .totalElements(1)
                    .pageSize(10)
                    .totalPages(1)
                    .build())
            .build();
    PageableBody pageableBody = PageableBody.builder()
            .page(0)
            .size(10)
            .sortingCriteria(Map.of("createdAt", "asc"))
            .build();
    TrainingBody trainingBody = TrainingBody.builder()
            .title("Training Title 1")
            .price(19.99)
            .body("Training body 1")
            .exercises(List.of(1L))
            .build();

    TrainingResponseWithOrderCount trainingTrainer1ResponseWithOrderCount = TrainingResponseWithOrderCount.builder()
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


    private static Stream<String> emptyTitle() {
        return Stream.of("", " ", "  ", null);
    }

    @ParameterizedTest
    @MethodSource("emptyTitle")
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_USER")
    void get_trainings_approved_success_title_empty(String title) {

        when(trainingService.getModelsApproved(title, pageableBody)).thenReturn(Flux.just(trainer1PR));
        when(trainingReactiveResponseBuilder.toModelPageable(trainer1PR, TrainingController.class))
                .thenReturn(Mono.just(trainer1CustomPR));


        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .patch()
                .uri(uriBuilder -> {
                    uriBuilder.path("/trainings/approved");
                    if (title != null) {
                        uriBuilder.queryParam("title", title);
                    }
                    return uriBuilder.build();
                })
                .accept(MediaType.APPLICATION_NDJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(pageableBody)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<PageableResponse<CustomEntityModel<TrainingResponse>>>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(1, responseBody.size());
                    var first = responseBody.get(0);
                    assertEquals(trainingTrainer1Model, first.getContent());
                });

        verify(trainingService).getModelsApproved(title, pageableBody);
        verify(trainingReactiveResponseBuilder).toModelPageable(trainer1PR, TrainingController.class);
    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_USER")
    void get_trainings_approved_success_title_not_empty() {
        String title = "Training Title 2";
        when(trainingService.getModelsApproved(title, pageableBody)).thenReturn(Flux.just(trainer1PR));
        when(trainingReactiveResponseBuilder.toModelPageable(trainer1PR, TrainingController.class))
                .thenReturn(Mono.just(trainer1CustomPR));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .patch()
                .uri(uriBuilder -> uriBuilder.queryParam("title", title).path("/trainings/approved").build()
                )
                .accept(MediaType.APPLICATION_NDJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(pageableBody)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<PageableResponse<CustomEntityModel<TrainingResponse>>>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(1, responseBody.size());
                    var first = responseBody.get(0);
                    assertEquals(trainingTrainer1Model, first.getContent());
                });

        verify(trainingService).getModelsApproved(title, pageableBody);
        verify(trainingReactiveResponseBuilder).toModelPageable(trainer1PR, TrainingController.class);
    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_USER")
    void get_trainings_approved_success_resp_empty() {
        when(trainingService.getModelsApproved(any(String.class), any(PageableBody.class))).thenReturn(Flux.empty());

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .patch()
                .uri(uriBuilder -> uriBuilder.queryParam("title", "ana").path("/trainings/approved").build()
                )
                .accept(MediaType.APPLICATION_NDJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(pageableBody)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<PageableResponse<CustomEntityModel<TrainingResponse>>>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(0, responseBody.size());
                });

        ArgumentCaptor<String> titleCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<PageableBody> pageableBodyCaptor = ArgumentCaptor.forClass(PageableBody.class);

        verify(trainingService).getModelsApproved(titleCaptor.capture(), pageableBodyCaptor.capture());
        assertEquals("ana", titleCaptor.getValue());
        assertEquals(pageableBody, pageableBodyCaptor.getValue());


    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_USER")
    void get_trainings_approved_body_not_valid() {
        PageableBody badPB = PageableBody.builder().build();

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .patch()
                .uri(uriBuilder -> uriBuilder.path("/trainings/approved").build()
                )
                .accept(MediaType.APPLICATION_NDJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(badPB)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(HttpStatus.BAD_REQUEST.value(), responseBody.get("status"));
                    assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), responseBody.get("error"));
                });

        verifyNoInteractions(trainingService);
        verifyNoInteractions(trainingReactiveResponseBuilder);
    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_USER")
    void get_model_approved_sorting_criteria_invalid() {
        PageableBody badPB = PageableBody.builder()
                .page(0)
                .size(10)
                .sortingCriteria(Map.of("createdAt", "smth"))
                .build();
        when(trainingService.getModelsApproved(any(String.class), eq(badPB))).thenReturn(Flux.error(
                new SortingCriteriaException("Invalid sorting criteria provided.", Map.of("createdAt", "smth"))
        ));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .patch()
                .uri(uriBuilder -> uriBuilder.path("/trainings/approved").queryParam("title", "t").build()
                )
                .accept(MediaType.APPLICATION_NDJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(badPB)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .consumeWith(
                        response -> {
                            var responseBody = response.getResponseBody();
                            assertNotNull(responseBody);
                            assertEquals("Invalid sorting criteria provided.", responseBody.get("message"));
                            assertEquals(HttpStatus.BAD_REQUEST.value(), responseBody.get("status"));
                            assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), responseBody.get("error"));
                            assertEquals("smth", responseBody.get("createdAt"));
                        }

                );

        ArgumentCaptor<String> titleCaptor = ArgumentCaptor.forClass(String.class);
        verify(trainingService).getModelsApproved(titleCaptor.capture(), eq(badPB));
        assertEquals("t", titleCaptor.getValue());
        verifyNoInteractions(trainingReactiveResponseBuilder);

    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_ADMIN")
    void get_trainings_trainer_success() {
        String title = "t";
        when(trainingService.getModelsTrainer(title, trainerDto.getId(), pageableBody)).thenReturn(Flux.just(trainer1PR, trainer2PR));
        when(trainingReactiveResponseBuilder.toModelPageable(trainer1PR, TrainingController.class))
                .thenReturn(Mono.just(trainer1CustomPR));
        when(trainingReactiveResponseBuilder.toModelPageable(trainer2PR, TrainingController.class))
                .thenReturn(Mono.just(trainer2CustomPR));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .patch()
                .uri(uriBuilder -> uriBuilder.queryParam("title", title).path("/trainings/trainer/" + trainerDto.getId()).build()
                )
                .accept(MediaType.APPLICATION_NDJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(pageableBody)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<PageableResponse<CustomEntityModel<TrainingResponse>>>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(2, responseBody.size());
                    var first = responseBody.get(0);
                    assertEquals(trainingTrainer1Model, first.getContent());
                    var second = responseBody.get(1);
                    assertEquals(trainingTrainer2Model, second.getContent());
                });

        verify(trainingService).getModelsTrainer(title, trainerDto.getId(), pageableBody);
        verify(trainingReactiveResponseBuilder).toModelPageable(trainer1PR, TrainingController.class);
    }


    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_ADMIN")
    void get_trainings_trainer_trainer_not_exists() {
        String title = "t";
        when(trainingService.getModelsTrainer(title, trainerDto.getId(), pageableBody))
                .thenReturn(Flux.error(new PrivateRouteException()));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .patch()
                .uri(uriBuilder -> uriBuilder.queryParam("title", title).path("/trainings/trainer/" + trainerDto.getId()).build()
                )
                .accept(MediaType.APPLICATION_NDJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(pageableBody)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals("Not allowed!", responseBody.get("message"));
                    assertEquals(HttpStatus.FORBIDDEN.value(), responseBody.get("status"));
                    assertEquals(HttpStatus.FORBIDDEN.getReasonPhrase(), responseBody.get("error"));
                });

    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_ADMIN")
    void create_training_success() {
        when(trainingService.createModel(trainingBody)).thenReturn(Mono.just(trainingAdminResponse));
        when(trainingReactiveResponseBuilder.toModel(trainingAdminResponse, TrainingController.class))
                .thenReturn(Mono.just(trainingAdminModel));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri("/trainings/create")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(trainingBody)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<CustomEntityModel<TrainingResponse>>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(trainingAdminModel, responseBody);
                });

        verify(trainingService).createModel(trainingBody);
        verify(trainingReactiveResponseBuilder).toModel(trainingAdminResponse, TrainingController.class);
    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_ADMIN")
    void create_training_body_not_valid() {
        TrainingBody badTB = TrainingBody.builder().build();
        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri("/trainings/create")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(badTB)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(HttpStatus.BAD_REQUEST.value(), responseBody.get("status"));
                    assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), responseBody.get("error"));
                });

        verifyNoInteractions(trainingService);
        verifyNoInteractions(trainingReactiveResponseBuilder);
    }


    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_ADMIN")
    void create_training_exercises_not_valid() {

        when(trainingService.createModel(trainingBody)).thenReturn(Mono.error(new
                IllegalActionException("exercises " + trainingBody.getExercises() + " are not valid")));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri("/trainings/create")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(trainingBody)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(HttpStatus.BAD_REQUEST.value(), responseBody.get("status"));
                    assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), responseBody.get("error"));
                    assertEquals("exercises " + trainingBody.getExercises() + " are not valid", responseBody.get("message"));
                });

        verify(trainingService).createModel(trainingBody);
        verifyNoInteractions(trainingReactiveResponseBuilder);
    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_ADMIN")
    void approve_model_success() {
        Long id = 1L;
        when(trainingService.approveModel(id)).thenReturn(Mono.just(trainingAdminResponse));
        when(trainingReactiveResponseBuilder.toModel(trainingAdminResponse, TrainingController.class))
                .thenReturn(Mono.just(trainingAdminModel));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .patch()
                .uri("/trainings/admin/approve/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<CustomEntityModel<TrainingResponse>>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(trainingAdminModel, responseBody);
                });

        verify(trainingService).approveModel(id);
        verify(trainingReactiveResponseBuilder).toModel(trainingAdminResponse, TrainingController.class);
    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_ADMIN")
    void approve_model_not_exists() {
        Long id = 1L;
        when(trainingService.approveModel(id)).thenReturn(Mono.error(new NotFoundEntity("training", id)));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .patch()
                .uri("/trainings/admin/approve/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(HttpStatus.NOT_FOUND.value(), responseBody.get("status"));
                    assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), responseBody.get("error"));
                    assertEquals("Entity training with id 1 was not found!", responseBody.get("message"));
                });

        verify(trainingService).approveModel(id);
        verifyNoInteractions(trainingReactiveResponseBuilder);
    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_ADMIN")
    void approve_model_already_approved() {
        Long id = 1L;
        when(trainingService.approveModel(id)).thenReturn(Mono.error(new IllegalActionException("training with id " + id + " is already approved.")));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .patch()
                .uri("/trainings/admin/approve/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(HttpStatus.BAD_REQUEST.value(), responseBody.get("status"));
                    assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), responseBody.get("error"));
                    assertEquals("training with id 1 is already approved.", responseBody.get("message"));
                });

        verify(trainingService).approveModel(id);
        verifyNoInteractions(trainingReactiveResponseBuilder);
    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_ADMIN")
    void get_all_models_admin() {
        when(trainingService.getAllModels("t", pageableBody)).thenReturn(Flux.just(adminPR, trainer1PR, trainer2PR));
        when(trainingReactiveResponseBuilder.toModelPageable(adminPR, TrainingController.class))
                .thenReturn(Mono.just(adminCustomPR));
        when(trainingReactiveResponseBuilder.toModelPageable(trainer1PR, TrainingController.class))
                .thenReturn(Mono.just(trainer1CustomPR));
        when(trainingReactiveResponseBuilder.toModelPageable(trainer2PR, TrainingController.class))
                .thenReturn(Mono.just(trainer2CustomPR));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .patch()
                .uri(uriBuilder -> uriBuilder.queryParam("title", "t").path("/trainings/admin").build()
                )
                .accept(MediaType.APPLICATION_NDJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(pageableBody)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<PageableResponse<CustomEntityModel<TrainingResponse>>>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(3, responseBody.size());
                    var first = responseBody.get(0);
                    assertEquals(adminCustomPR, first);
                    var second = responseBody.get(1);
                    assertEquals(trainer1CustomPR, second);
                    var third = responseBody.get(2);
                    assertEquals(trainer2CustomPR, third);
                });

        verify(trainingService).getAllModels("t", pageableBody);
        verify(trainingReactiveResponseBuilder).toModelPageable(adminPR, TrainingController.class);
        verify(trainingReactiveResponseBuilder).toModelPageable(trainer1PR, TrainingController.class);
        verify(trainingReactiveResponseBuilder).toModelPageable(trainer2PR, TrainingController.class);
    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_ADMIN")
    void get_all_models_admin_empty() {
        when(trainingService.getAllModels(any(String.class), eq(pageableBody))).thenReturn(Flux.empty());

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .patch()
                .uri(uriBuilder -> uriBuilder.queryParam("title", "t").path("/trainings/admin").build()
                )
                .accept(MediaType.APPLICATION_NDJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(pageableBody)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<PageableResponse<CustomEntityModel<TrainingResponse>>>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(0, responseBody.size());
                });

        ArgumentCaptor<String> titleCaptor = ArgumentCaptor.forClass(String.class);
        verify(trainingService).getAllModels(titleCaptor.capture(), eq(pageableBody));
        assertEquals("t", titleCaptor.getValue());
    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_ADMIN")
    void get_all_models_admin_sorting_criteria_invalid() {
        PageableBody badPB = PageableBody.builder()
                .page(0)
                .size(10)
                .sortingCriteria(Map.of("createdAt", "smth"))
                .build();
        when(trainingService.getAllModels(any(String.class), eq(badPB))).thenReturn(Flux.error(
                new SortingCriteriaException("Invalid sorting criteria provided.", Map.of("createdAt", "smth"))
        ));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .patch()
                .uri(uriBuilder -> uriBuilder.queryParam("title", "t").path("/trainings/admin").build()
                )
                .accept(MediaType.APPLICATION_NDJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(badPB)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .consumeWith(
                        response -> {
                            var responseBody = response.getResponseBody();
                            assertNotNull(responseBody);
                            assertEquals("Invalid sorting criteria provided.", responseBody.get("message"));
                            assertEquals(HttpStatus.BAD_REQUEST.value(), responseBody.get("status"));
                            assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), responseBody.get("error"));
                            assertEquals("smth", responseBody.get("createdAt"));
                        }

                );

        ArgumentCaptor<String> titleCaptor = ArgumentCaptor.forClass(String.class);
        verify(trainingService).getAllModels(titleCaptor.capture(), eq(badPB));
        assertEquals("t", titleCaptor.getValue());
        verifyNoInteractions(trainingReactiveResponseBuilder);

    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_ADMIN")
    void delete_model_success() {
        Long id = 1L;
        when(trainingService.deleteModel(id)).thenReturn(Mono.just(trainingAdminResponse));
        when(trainingReactiveResponseBuilder.toModel(trainingAdminResponse, TrainingController.class))
                .thenReturn(Mono.just(trainingAdminModel));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .delete()
                .uri("/trainings/delete/" + id)
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<CustomEntityModel<TrainingResponse>>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(trainingAdminModel, responseBody);
                });

        verify(trainingService).deleteModel(id);
        verify(trainingReactiveResponseBuilder).toModel(trainingAdminResponse, TrainingController.class);
    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_ADMIN")
    void delete_training_training_in_orders() {
        Long id = 1L;
        when(trainingService.deleteModel(id)).thenReturn(Mono.error(new SubEntityUsed("training", id)));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .delete()
                .uri("/trainings/delete/" + id)
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(HttpStatus.BAD_REQUEST.value(), responseBody.get("status"));
                    assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), responseBody.get("error"));
                    assertEquals("Entity training with id 1 is used!", responseBody.get("message"));
                    assertEquals(responseBody.get("name"), "training");
                    assertEquals(responseBody.get("id"), 1);
                });

    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_TRAINER")
    void delete_training_private_route() {
        Long id = 1L;
        when(trainingService.deleteModel(id)).thenReturn(Mono.error(new PrivateRouteException()));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .delete()
                .uri("/trainings/delete/" + id)
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(HttpStatus.FORBIDDEN.value(), responseBody.get("status"));
                    assertEquals(HttpStatus.FORBIDDEN.getReasonPhrase(), responseBody.get("error"));
                    assertEquals("Not allowed!", responseBody.get("message"));
                });
    }


    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_ADMIN")
    void get_model_by_id_success() {
        Long id = 2L;
        when(trainingService.getModelById(id)).thenReturn(Mono.just(trainingTrainer1Response));
        when(trainingReactiveResponseBuilder.toModel(trainingTrainer1Response, TrainingController.class))
                .thenReturn(Mono.just(trainingTrainer1Model));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri("/trainings/" + id)
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<CustomEntityModel<TrainingResponse>>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(trainingTrainer1Model, responseBody);
                });

        verify(trainingService).getModelById(id);
        verify(trainingReactiveResponseBuilder).toModel(trainingTrainer1Response, TrainingController.class);
    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_ADMIN")
    void get_model_by_id_not_exists() {
        Long id = 2L;
        when(trainingService.getModelById(id)).thenReturn(Mono.error(new NotFoundEntity("training", id)));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri("/trainings/" + id)
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(HttpStatus.NOT_FOUND.value(), responseBody.get("status"));
                    assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), responseBody.get("error"));
                    assertEquals("Entity training with id 2 was not found!", responseBody.get("message"));
                });

        verify(trainingService).getModelById(id);
        verifyNoInteractions(trainingReactiveResponseBuilder);
    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_USER")
    void get_model_by_id_private_route() {
        Long id = 2L;
        when(trainingService.getModelById(id)).thenReturn(Mono.error(new PrivateRouteException()));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri("/trainings/" + id)
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(HttpStatus.FORBIDDEN.value(), responseBody.get("status"));
                    assertEquals(HttpStatus.FORBIDDEN.getReasonPhrase(), responseBody.get("error"));
                    assertEquals("Not allowed!", responseBody.get("message"));
                });


        verify(trainingService).getModelById(id);
        verifyNoInteractions(trainingReactiveResponseBuilder);

    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_ADMIN")
    void get_model_by_id_with_user() {
        ResponseWithUserDto<TrainingResponse> responseWithUserDto = ResponseWithUserDto.<TrainingResponse>builder()
                .user(trainerDto)
                .model(trainingTrainer1Response)
                .build();
        ResponseWithUserDtoEntity<TrainingResponse> responseWithUserDtoEntity = ResponseWithUserDtoEntity.<TrainingResponse>builder()
                .user(CustomEntityModel.of(trainerDto))
                .model(trainingTrainer1Model)
                .build();

        Long id = 2L;
        when(trainingService.getModelByIdWithUser(id)).thenReturn(Mono.just(responseWithUserDto));
        when(trainingReactiveResponseBuilder.toModelWithUser(responseWithUserDto, TrainingController.class))
                .thenReturn(Mono.just(responseWithUserDtoEntity));


        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri("/trainings/withUser/" + id)
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<ResponseWithUserDtoEntity<TrainingResponse>>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(responseWithUserDtoEntity, responseBody);
                });

        verify(trainingService).getModelByIdWithUser(id);
        verify(trainingReactiveResponseBuilder).toModelWithUser(responseWithUserDto, TrainingController.class);


    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_ADMIN")
    void get_model_by_id_with_user_not_exists() {
        Long id = 2L;
        when(trainingService.getModelByIdWithUser(id)).thenReturn(Mono.error(new NotFoundEntity("training", id)));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri("/trainings/withUser/" + id)
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(HttpStatus.NOT_FOUND.value(), responseBody.get("status"));
                    assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), responseBody.get("error"));
                    assertEquals("Entity training with id 2 was not found!", responseBody.get("message"));
                });

        verify(trainingService).getModelByIdWithUser(id);
        verifyNoInteractions(trainingReactiveResponseBuilder);
    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_USER")
    void get_model_by_id_with_user_private_route() {
        Long id = 2L;
        when(trainingService.getModelByIdWithUser(id)).thenReturn(Mono.error(new PrivateRouteException()));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri("/trainings/withUser/" + id)
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(HttpStatus.FORBIDDEN.value(), responseBody.get("status"));
                    assertEquals(HttpStatus.FORBIDDEN.getReasonPhrase(), responseBody.get("error"));
                    assertEquals("Not allowed!", responseBody.get("message"));
                });

        verify(trainingService).getModelByIdWithUser(id);
        verifyNoInteractions(trainingReactiveResponseBuilder);
    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_ADMIN")
    void update_model_success() {
        Long id = 1L;
        when(trainingService.updateModel(id, trainingBody)).thenReturn(Mono.just(trainingAdminResponse));
        when(trainingReactiveResponseBuilder.toModel(trainingAdminResponse, TrainingController.class))
                .thenReturn(Mono.just(trainingAdminModel));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .put()
                .uri("/trainings/update/" + id)
                .accept(MediaType.APPLICATION_NDJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(trainingBody)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<CustomEntityModel<TrainingResponse>>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(trainingAdminModel, responseBody);
                });

        verify(trainingService).updateModel(id, trainingBody);
        verify(trainingReactiveResponseBuilder).toModel(trainingAdminResponse, TrainingController.class);
    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_ADMIN")
    void update_model_not_exists() {
        Long id = 1L;
        when(trainingService.updateModel(id, trainingBody)).thenReturn(Mono.error(new NotFoundEntity("training", id)));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .put()
                .uri("/trainings/update/" + id)
                .accept(MediaType.APPLICATION_NDJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(trainingBody)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(HttpStatus.NOT_FOUND.value(), responseBody.get("status"));
                    assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), responseBody.get("error"));
                    assertEquals("Entity training with id 1 was not found!", responseBody.get("message"));
                });

        verify(trainingService).updateModel(id, trainingBody);
        verifyNoInteractions(trainingReactiveResponseBuilder);
    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_ADMIN")
    void update_model_exercises_not_valid() {
        Long id = 1L;
        when(trainingService.updateModel(id, trainingBody)).thenReturn(Mono.error(new
                IllegalActionException("exercises " + trainingBody.getExercises() + " are not valid")));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .put()
                .uri("/trainings/update/" + id)
                .accept(MediaType.APPLICATION_NDJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(trainingBody)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(HttpStatus.BAD_REQUEST.value(), responseBody.get("status"));
                    assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), responseBody.get("error"));
                    assertEquals("exercises " + trainingBody.getExercises() + " are not valid", responseBody.get("message"));
                });

        verify(trainingService).updateModel(id, trainingBody);
        verifyNoInteractions(trainingReactiveResponseBuilder);
    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_ADMIN")
    void update_training_private_route() {
        Long id = 1L;
        when(trainingService.updateModel(id, trainingBody)).thenReturn(Mono.error(new PrivateRouteException()));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .put()
                .uri("/trainings/update/" + id)
                .accept(MediaType.APPLICATION_NDJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(trainingBody)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(HttpStatus.FORBIDDEN.value(), responseBody.get("status"));
                    assertEquals(HttpStatus.FORBIDDEN.getReasonPhrase(), responseBody.get("error"));
                    assertEquals("Not allowed!", responseBody.get("message"));
                });

        verify(trainingService).updateModel(id, trainingBody);
        verifyNoInteractions(trainingReactiveResponseBuilder);
    }

    // get models by id in
    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_ADMIN")
    void get_models_by_ids_success() {
        List<Long> ids = List.of(1L, 2L, 3L);
        when(trainingService.getModelsByIdIn(ids, pageableBody)).thenReturn(Flux.just(adminPR, trainer1PR, trainer2PR));
        when(trainingReactiveResponseBuilder.toModelPageable(adminPR, TrainingController.class))
                .thenReturn(Mono.just(adminCustomPR));
        when(trainingReactiveResponseBuilder.toModelPageable(trainer1PR, TrainingController.class))
                .thenReturn(Mono.just(trainer1CustomPR));
        when(trainingReactiveResponseBuilder.toModelPageable(trainer2PR, TrainingController.class))
                .thenReturn(Mono.just(trainer2CustomPR));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .patch()
                .uri(uriBuilder -> uriBuilder.queryParam("ids", ids).path("/trainings/byIds").build()
                )
                .accept(MediaType.APPLICATION_NDJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(pageableBody)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<PageableResponse<CustomEntityModel<TrainingResponse>>>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(3, responseBody.size());
                    var first = responseBody.get(0);
                    assertEquals(adminCustomPR, first);
                    var second = responseBody.get(1);
                    assertEquals(trainer1CustomPR, second);
                    var third = responseBody.get(2);
                    assertEquals(trainer2CustomPR, third);
                });


        verify(trainingService).getModelsByIdIn(ids, pageableBody);
        verify(trainingReactiveResponseBuilder).toModelPageable(adminPR, TrainingController.class);
        verify(trainingReactiveResponseBuilder).toModelPageable(trainer1PR, TrainingController.class);
        verify(trainingReactiveResponseBuilder).toModelPageable(trainer2PR, TrainingController.class);


    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_ADMIN")
    void get_models_by_ids_empty() {
        List<Long> ids = List.of(1L, 2L, 3L);
        when(trainingService.getModelsByIdIn(ids, pageableBody)).thenReturn(Flux.empty());

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .patch()
                .uri(uriBuilder -> uriBuilder.queryParam("ids", ids).path("/trainings/byIds").build()
                )
                .accept(MediaType.APPLICATION_NDJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(pageableBody)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<PageableResponse<CustomEntityModel<TrainingResponse>>>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(0, responseBody.size());
                });

        ArgumentCaptor<List<Long>> idsCaptor = ArgumentCaptor.forClass(List.class);
        verify(trainingService).getModelsByIdIn(idsCaptor.capture(), eq(pageableBody));
        assertEquals(ids, idsCaptor.getValue());
    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_ADMIN")
    void get_models_by_ids_sorting_criteria_invalid() {
        List<Long> ids = List.of(1L, 2L, 3L);
        PageableBody badPB = PageableBody.builder()
                .page(0)
                .size(10)
                .sortingCriteria(Map.of("createdAt", "smth"))
                .build();
        when(trainingService.getModelsByIdIn(ids, badPB)).thenReturn(Flux.error(
                new SortingCriteriaException("Invalid sorting criteria provided.", Map.of("createdAt", "smth"))
        ));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .patch()
                .uri(uriBuilder -> uriBuilder.queryParam("ids", ids).path("/trainings/byIds").build()
                )
                .accept(MediaType.APPLICATION_NDJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(badPB)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .consumeWith(
                        response -> {
                            var responseBody = response.getResponseBody();
                            assertNotNull(responseBody);
                            assertEquals("Invalid sorting criteria provided.", responseBody.get("message"));
                            assertEquals(HttpStatus.BAD_REQUEST.value(), responseBody.get("status"));
                            assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), responseBody.get("error"));
                            assertEquals("smth", responseBody.get("createdAt"));
                        }

                );

        ArgumentCaptor<List<Long>> idsCaptor = ArgumentCaptor.forClass(List.class);
        verify(trainingService).getModelsByIdIn(idsCaptor.capture(), eq(badPB));
        assertEquals(ids, idsCaptor.getValue());
        verifyNoInteractions(trainingReactiveResponseBuilder);
    }

    @ParameterizedTest
    @ValueSource(strings = {"like", "dislike"})
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_ADMIN")
    void react_training_success(String type) {
        Long id = 1L;
        when(trainingService.reactToModel(id, type)).thenReturn(Mono.just(trainingAdminResponse));
        when(trainingReactiveResponseBuilder.toModel(trainingAdminResponse, TrainingController.class))
                .thenReturn(Mono.just(trainingAdminModel));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .patch()
                .uri(uriBuilder -> uriBuilder.path("/trainings/{type}/{id}").queryParam("type", type).build(
                                type, id
                        )
                )
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<CustomEntityModel<TrainingResponse>>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(trainingAdminModel, responseBody);
                });

        verify(trainingService).reactToModel(id, type);
        verify(trainingReactiveResponseBuilder).toModel(trainingAdminResponse, TrainingController.class);

    }

    @ParameterizedTest
    @ValueSource(strings = {"like", "dislike"})
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_ADMIN")
    void react_training_not_exists(String type) {
        Long id = 1L;
        when(trainingService.reactToModel(id, type)).thenReturn(Mono.error(new NotFoundEntity("training", id)));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .patch()
                .uri(uriBuilder -> uriBuilder.path("/trainings/{type}/{id}").queryParam("type", type).build(
                                type, id
                        )
                )
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(HttpStatus.NOT_FOUND.value(), responseBody.get("status"));
                    assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), responseBody.get("error"));
                    assertEquals("Entity training with id 1 was not found!", responseBody.get("message"));
                });

        verify(trainingService).reactToModel(id, type);
        verifyNoInteractions(trainingReactiveResponseBuilder);
    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_ADMIN")
    void get_training_with_user_and_reactions() {
        Long id = 1L;
        when(trainingService.getModelByIdWithUserLikesAndDislikes(id)).thenReturn(Mono.just(trainingTrainer1WithULUD));
        when(trainingReactiveResponseBuilder.toModelWithUserLikesAndDislikes(trainingTrainer1WithULUD, TrainingController.class))
                .thenReturn(Mono.just(trainingTrainer1WithULUDEntity));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri("/trainings/withUser/withReactions/" + id)
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<ResponseWithUserLikesAndDislikesEntity<TrainingResponse>>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(trainingTrainer1WithULUDEntity, responseBody);
                });

        verify(trainingService).getModelByIdWithUserLikesAndDislikes(id);
        verify(trainingReactiveResponseBuilder).toModelWithUserLikesAndDislikes(trainingTrainer1WithULUD, TrainingController.class);
    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_USER")
    void get_training_with_user_and_reactions_not_found() {
        Long id = 1L;
        when(trainingService.getModelByIdWithUserLikesAndDislikes(id)).thenReturn(Mono.error(new NotFoundEntity("training", id)));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri("/trainings/withUser/withReactions/" + id)
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(HttpStatus.NOT_FOUND.value(), responseBody.get("status"));
                    assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), responseBody.get("error"));
                    assertEquals("Entity training with id 1 was not found!", responseBody.get("message"));
                });

        verify(trainingService).getModelByIdWithUserLikesAndDislikes(id);
        verifyNoInteractions(trainingReactiveResponseBuilder);
    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_USER")
    void get_training_with_user_and_reactions_private_route() {
        Long id = 1L;
        when(trainingService.getModelByIdWithUserLikesAndDislikes(id)).thenReturn(Mono.error(new PrivateRouteException()));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri("/trainings/withUser/withReactions/" + id)
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(HttpStatus.FORBIDDEN.value(), responseBody.get("status"));
                    assertEquals(HttpStatus.FORBIDDEN.getReasonPhrase(), responseBody.get("error"));
                    assertEquals("Not allowed!", responseBody.get("message"));
                });

        verify(trainingService).getModelByIdWithUserLikesAndDislikes(id);
        verifyNoInteractions(trainingReactiveResponseBuilder);
    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_ADMIN")
    void get_trainings_with_exercises() {
        List<Long> ids = List.of(1L);
        when(trainingService.getTrainingsWithExercises(ids, true)).thenReturn(Flux.just(trainingTrainer1WithExercises));
        when(exerciseReactiveResponseBuilder.toModelWithUser(exerciseTrainer1WithUser, ExerciseController.class))
                .thenReturn(Mono.just(exerciseTrainer1WithUserEntity));
        when(exerciseReactiveResponseBuilder.toModelWithUser(exerciseTrainer2WithUser, ExerciseController.class))
                .thenReturn(Mono.just(exerciseTrainer2WithUserEntity));
        when(trainingReactiveResponseBuilder.toModel(trainingTrainer1WithExercises.getEntity(), TrainingController.class))
                .thenReturn(Mono.just(trainingTrainer1Model));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.queryParam("ids", ids).path("/trainings/withExercises").build()
                )
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<ResponseWithChildListEntity<TrainingResponse, ResponseWithUserDtoEntity<ExerciseResponse>>>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(1, responseBody.size());
                    var first = responseBody.get(0);
                    assertEquals(trainingTrainer1Model, first.getEntity());
                    assertEquals(List.of(exerciseTrainer1WithUserEntity, exerciseTrainer2WithUserEntity), first.getChildren());
                });

        verify(trainingService).getTrainingsWithExercises(ids, true);
        verify(exerciseReactiveResponseBuilder).toModelWithUser(exerciseTrainer1WithUser, ExerciseController.class);
        verify(exerciseReactiveResponseBuilder).toModelWithUser(exerciseTrainer2WithUser, ExerciseController.class);
        verify(trainingReactiveResponseBuilder).toModel(trainingTrainer1WithExercises.getEntity(), TrainingController.class);


    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_ADMIN")
    void get_trainings_with_exercises_empty() {
        List<Long> ids = List.of(1L);
        when(trainingService.getTrainingsWithExercises(ids, true)).thenReturn(Flux.empty());

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.queryParam("ids", ids).path("/trainings/withExercises").build()
                )
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<ResponseWithChildListEntity<TrainingResponse, ResponseWithUserDtoEntity<ExerciseResponse>>>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(0, responseBody.size());
                });

        ArgumentCaptor<List<Long>> idsCaptor = ArgumentCaptor.forClass(List.class);
        verify(trainingService).getTrainingsWithExercises(idsCaptor.capture(), eq(true));
        assertEquals(ids, idsCaptor.getValue());
    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_ADMIN")
    void get_training_with_exercises() {
        Long id = 1L;
        when(trainingService.getTrainingWithExercises(id, true)).thenReturn(Mono.just(trainingTrainer1WithExercises));
        when(exerciseReactiveResponseBuilder.toModelWithUser(exerciseTrainer1WithUser, ExerciseController.class))
                .thenReturn(Mono.just(exerciseTrainer1WithUserEntity));
        when(exerciseReactiveResponseBuilder.toModelWithUser(exerciseTrainer2WithUser, ExerciseController.class))
                .thenReturn(Mono.just(exerciseTrainer2WithUserEntity));
        when(trainingReactiveResponseBuilder.toModel(trainingTrainer1WithExercises.getEntity(), TrainingController.class))
                .thenReturn(Mono.just(trainingTrainer1Model));

        webClient.
                mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri("/trainings/withExercises/" + id)
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<ResponseWithChildListEntity<TrainingResponse, ResponseWithUserDtoEntity<ExerciseResponse>>>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(trainingTrainer1Model, responseBody.getEntity());
                    assertEquals(List.of(exerciseTrainer1WithUserEntity, exerciseTrainer2WithUserEntity), responseBody.getChildren());
                });

        verify(trainingService).getTrainingWithExercises(id, true);
        verify(exerciseReactiveResponseBuilder).toModelWithUser(exerciseTrainer1WithUser, ExerciseController.class);
        verify(exerciseReactiveResponseBuilder).toModelWithUser(exerciseTrainer2WithUser, ExerciseController.class);
        verify(trainingReactiveResponseBuilder).toModel(trainingTrainer1WithExercises.getEntity(), TrainingController.class);

    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_ADMIN")
    void get_training_with_exercises_not_found() {
        Long id = 1L;
        when(trainingService.getTrainingWithExercises(id, true)).thenReturn(Mono.error(new NotFoundEntity("training", id)));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri("/trainings/withExercises/" + id)
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(HttpStatus.NOT_FOUND.value(), responseBody.get("status"));
                    assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), responseBody.get("error"));
                    assertEquals("Entity training with id 1 was not found!", responseBody.get("message"));
                });

        verify(trainingService).getTrainingWithExercises(id, true);
        verifyNoInteractions(trainingReactiveResponseBuilder);
    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_ADMIN")
    void get_training_with_order_count() {
        Long id = 1L;
        when(trainingService.getTrainingWithOrderCount(id)).thenReturn(Mono.just(trainingTrainer1ResponseWithOrderCount));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri("/trainings/withOrderCount/" + id)
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<TrainingResponseWithOrderCount>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(trainingTrainer1ResponseWithOrderCount, responseBody);
                });

        verify(trainingService).getTrainingWithOrderCount(id);
    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_ADMIN")
    void get_training_with_order_count_not_found() {
        Long id = 1L;
        when(trainingService.getTrainingWithOrderCount(id)).thenReturn(Mono.error(new NotFoundEntity("training", id)));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri("/trainings/withOrderCount/" + id)
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals(HttpStatus.NOT_FOUND.value(), responseBody.get("status"));
                    assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), responseBody.get("error"));
                    assertEquals("Entity training with id 1 was not found!", responseBody.get("message"));
                });

        verify(trainingService).getTrainingWithOrderCount(id);
    }

}
