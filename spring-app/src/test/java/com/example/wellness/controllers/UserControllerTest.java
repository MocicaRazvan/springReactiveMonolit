package com.example.wellness.controllers;

import com.example.wellness.dto.common.PageableBody;
import com.example.wellness.dto.common.UserBody;
import com.example.wellness.dto.common.UserDto;
import com.example.wellness.dto.common.response.PageInfo;
import com.example.wellness.dto.common.response.PageableResponse;
import com.example.wellness.enums.Role;
import com.example.wellness.exceptions.action.IllegalActionException;
import com.example.wellness.exceptions.action.PrivateRouteException;
import com.example.wellness.exceptions.common.SortingCriteriaException;
import com.example.wellness.exceptions.notFound.NotFoundEntity;
import com.example.wellness.filters.AuthFilter;
import com.example.wellness.hateos.CustomEntityModel;
import com.example.wellness.hateos.user.PageableUserAssembler;
import com.example.wellness.hateos.user.UserDtoAssembler;
import com.example.wellness.models.user.UserCustom;
import com.example.wellness.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import com.fasterxml.jackson.databind.ObjectMapper;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = UserController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AuthFilter.class)
}
)
@ActiveProfiles("tc")
@Slf4j
class UserControllerTest {
    private final String TEST_EMAIL = "john.doe@example.com";

    @Autowired
    private WebTestClient webClient;


    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private PageableUserAssembler pageableUserAssembler;

    @MockBean
    private UserDtoAssembler userDtoAssembler;

    UserCustom user = UserCustom.builder()
            .id(1L)
            .firstName("John")
            .lastName("Doe")
            .email("john.doe@example.com")
            .password("password")
            .role(Role.ROLE_USER)
            .build();

    UserDto userDto = UserDto.builder()
            .id(1L)
            .firstName("John")
            .lastName("Doe")
            .email("john.doe@example.com")
            .role(Role.ROLE_USER)
            .build();


    CustomEntityModel<UserDto> userEntityModel = CustomEntityModel.of(userDto);

    PageableBody pageableBody = PageableBody.builder()
            .page(0)
            .size(10)
            .sortingCriteria(Map.of("firstName", "asc"))
            .build();

    PageableResponse<UserDto> pageableResponse = PageableResponse.<UserDto>builder()
            .content(userDto)
            .pageInfo(
                    PageInfo.builder()
                            .totalElements(1)
                            .totalPages(1)
                            .pageSize(10)
                            .currentPage(0)
                            .build()
            )
            .build();

    PageableResponse<CustomEntityModel<UserDto>> pageableEntityModel = PageableResponse.<CustomEntityModel<UserDto>>builder()
            .content(userEntityModel)
            .pageInfo(
                    PageInfo.builder()
                            .totalElements(1)
                            .totalPages(1)
                            .pageSize(10)
                            .currentPage(0)
                            .build()
            )
            .build();


    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_USER")
    public void get_roles() {
        webClient.get()
                .uri("/users/roles")
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<Role>>() {
                })
                .consumeWith(response -> {

                    List<Role> roles = response.getResponseBody();
                    assertEquals(List.of(Role.values()), roles);
                });


    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_USER")
    public void get_user() {
        when(userService.getUser(1L)).thenReturn(Mono.just(userDto));
        when(pageableUserAssembler.getItemAssembler()).thenReturn(userDtoAssembler);
        when(userDtoAssembler.toModel(userDto)).thenReturn(Mono.just(userEntityModel));

        webClient.get()
                .uri("/users/1")
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<CustomEntityModel<UserDto>>() {
                })
                .consumeWith(response -> {
                    CustomEntityModel<UserDto> user = response.getResponseBody();
                    assertEquals(userEntityModel, user);
                });


        verify(userService).getUser(1L);
        verify(pageableUserAssembler).getItemAssembler();
        verify(userDtoAssembler).toModel(userDto);
    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_USER")
    public void get_user_json() {
        when(userService.getUser(1L)).thenReturn(Mono.just(userDto));
        when(pageableUserAssembler.getItemAssembler()).thenReturn(userDtoAssembler);
        when(userDtoAssembler.toModel(userDto)).thenReturn(Mono.just(userEntityModel));

        webClient.get()
                .uri("/users/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<CustomEntityModel<UserDto>>() {
                })
                .consumeWith(response -> {
                    CustomEntityModel<UserDto> user = response.getResponseBody();
                    assertEquals(userEntityModel, user);
                });


        verify(userService).getUser(1L);
        verify(pageableUserAssembler).getItemAssembler();
        verify(userDtoAssembler).toModel(userDto);
    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_USER")
    public void get_user_notFound() {
        when(userService.getUser(1L)).thenReturn(Mono.error(new NotFoundEntity("user", 1L)));

        webClient.get()
                .uri("/users/1")
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .consumeWith(response -> {
                    Map<String, Object> error = response.getResponseBody();
                    assertNotNull(error);
                    assertEquals(error.get("path"), "/users/1");
                    assertEquals(error.get("name"), "user");
                    assertEquals(error.get("id"), 1);
                    assertEquals(error.get("message"), "Entity user with id 1 was not found!");
                    assertEquals(error.get("error"), "Not Found");
                    assertEquals(error.get("status"), 404);
                });
        ;

        verify(userService).getUser(1L);
        verifyNoMoreInteractions(userService);
        verifyNoInteractions(pageableUserAssembler);
        verifyNoInteractions(userDtoAssembler);
    }


    public static Stream<Set<Role>> emptyRoles() {
        return Stream.of(Set.of(), null);
    }


    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_USER")
    public void get_all_users_rolesNotEmpty_EmailEmpty() {

        String email = null;
        PageableBody pageableBody = PageableBody.builder()
                .page(0)
                .size(10)
                .sortingCriteria(Map.of("firstName", "asc"))
                .build();
        Set<Role> roles = Set.of(Role.ROLE_USER, Role.ROLE_ADMIN);
        when(userService.getAllUsers(pageableBody, roles, email)).thenReturn(Flux.just(pageableResponse));
        when(pageableUserAssembler.toModel(pageableResponse)).thenReturn(Mono.just(pageableEntityModel));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .patch()
                .uri(uriBuilder -> uriBuilder.path("/users").queryParam("roles", roles).build())
                .accept(MediaType.APPLICATION_NDJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(pageableBody)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<PageableResponse<CustomEntityModel<UserDto>>>() {
                })
                .consumeWith(response -> {
                    PageableResponse<CustomEntityModel<UserDto>> users = response.getResponseBody();
                    assertEquals(pageableEntityModel, users);
                });

        verify(userService).getAllUsers(pageableBody, roles, email);
        verify(pageableUserAssembler).toModel(pageableResponse);
    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_USER")
    public void get_all_users_rolesNotEmpty_EmailNotEmpty() {

        String email = "john";
        PageableBody pageableBody = PageableBody.builder()
                .page(0)
                .size(10)
                .sortingCriteria(Map.of("firstName", "asc"))
                .build();
        Set<Role> roles = Set.of(Role.ROLE_USER, Role.ROLE_ADMIN);
        when(userService.getAllUsers(pageableBody, roles, email)).thenReturn(Flux.just(pageableResponse));
        when(pageableUserAssembler.toModel(pageableResponse)).thenReturn(Mono.just(pageableEntityModel));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .patch()
                .uri(uriBuilder -> uriBuilder.path("/users").queryParam("roles", roles).queryParam("email", email).build())
                .accept(MediaType.APPLICATION_NDJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(pageableBody)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<PageableResponse<CustomEntityModel<UserDto>>>() {
                })
                .consumeWith(response -> {
                    PageableResponse<CustomEntityModel<UserDto>> users = response.getResponseBody();
                    assertEquals(pageableEntityModel, users);
                });

        verify(userService).getAllUsers(pageableBody, roles, email);
        verify(pageableUserAssembler).toModel(pageableResponse);
    }

    
    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_ADMIN")
    public void make_trainer() {
        UserDto trainerDto = UserDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("tr@gmail.con")
                .role(Role.ROLE_TRAINER)
                .build();
        CustomEntityModel<UserDto> trainerEntityModel = CustomEntityModel.of(trainerDto);

        when(userService.makeTrainer(1L)).thenReturn(Mono.just(trainerDto));
        when(pageableUserAssembler.getItemAssembler()).thenReturn(userDtoAssembler);
        when(userDtoAssembler.toModel(any(UserDto.class))).then(invocation -> {
            UserDto userDto = invocation.getArgument(0);
            return Mono.just(CustomEntityModel.of(userDto));
        });

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .patch()
                .uri(uriBuilder -> uriBuilder.path("/users/admin/1").build())
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<CustomEntityModel<UserDto>>() {
                })
                .consumeWith(response -> {
                    CustomEntityModel<UserDto> user = response.getResponseBody();
                    assertEquals(trainerEntityModel, user);
                });

        verify(userService).makeTrainer(1L);
        verify(pageableUserAssembler).getItemAssembler();
        ArgumentCaptor<UserDto> userDtoArgumentCaptor = ArgumentCaptor.forClass(UserDto.class);
        verify(userDtoAssembler).toModel(userDtoArgumentCaptor.capture());
        assertEquals(trainerDto, userDtoArgumentCaptor.getValue());
    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_ADMIN")
    public void make_trainer_not_admin() {
        when(userService.makeTrainer(1L)).thenReturn(Mono.error(new NotFoundEntity("user", 1L)));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .patch()
                .uri(uriBuilder -> uriBuilder.path("/users/admin/1").build())
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .consumeWith(response -> {
                    Map<String, Object> error = response.getResponseBody();
                    assertNotNull(error);
                    assertEquals(error.get("path"), "/users/admin/1");
                    assertEquals(error.get("name"), "user");
                    assertEquals(error.get("id"), 1);
                    assertEquals(error.get("message"), "Entity user with id 1 was not found!");
                    assertEquals(error.get("error"), "Not Found");
                    assertEquals(error.get("status"), 404);
                });

        verify(userService).makeTrainer(1L);
        verifyNoMoreInteractions(userService);
        verifyNoInteractions(pageableUserAssembler);
        verifyNoInteractions(userDtoAssembler);
    }

    @ParameterizedTest
    @ValueSource(strings = {"admin", "trainer"})
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_ADMIN")
    public void make_trainer_user_already_has_privileges(String role) {
        when(userService.makeTrainer(1L)).thenReturn(Mono.error(new IllegalActionException("User is " + role + "!")));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .patch()
                .uri(uriBuilder -> uriBuilder.path("/users/admin/1").build())
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .consumeWith(response -> {
                    Map<String, Object> error = response.getResponseBody();
                    assertNotNull(error);
                    assertEquals(error.get("message"), "User is " + role + "!");
                    assertEquals(error.get("error"), "Bad Request");
                    assertEquals(error.get("status"), 400);
                });

        verify(userService).makeTrainer(1L);
        verifyNoMoreInteractions(userService);
        verifyNoInteractions(pageableUserAssembler);
        verifyNoInteractions(userDtoAssembler);
    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_USER")
    public void update_user_success() {
        UserBody userBody = UserBody.builder()
                .firstName("FN")
                .lastName("LN")
                .build();

        UserDto updatedUserDto = UserDto.builder()
                .id(userDto.getId())
                .firstName(userBody.getFirstName())
                .lastName(userBody.getLastName())
                .email(userDto.getEmail())
                .role(userDto.getRole())
                .build();

        CustomEntityModel<UserDto> updatedUserEntityModel = CustomEntityModel.of(updatedUserDto);

        when(userService.updateUser(1L, userBody)).thenReturn(Mono.just(updatedUserDto));
        when(pageableUserAssembler.getItemAssembler()).thenReturn(userDtoAssembler);
        when(userDtoAssembler.toModel(updatedUserDto)).thenReturn(Mono.just(updatedUserEntityModel));


        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .put()
                .uri(uriBuilder -> uriBuilder.path("/users/1").build())
                .accept(MediaType.APPLICATION_NDJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userBody)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<CustomEntityModel<UserDto>>() {
                })
                .consumeWith(response -> {
                    CustomEntityModel<UserDto> user = response.getResponseBody();
                    assertEquals(updatedUserEntityModel, user);
                });

        verify(userService).updateUser(1L, userBody);
        verify(pageableUserAssembler).getItemAssembler();
        verify(userDtoAssembler).toModel(updatedUserDto);


    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_USER")
    public void update_user_not_authorized() {
        UserBody userBody = UserBody.builder()
                .firstName("FN")
                .lastName("LN")
                .build();

        when(userService.updateUser(1L, userBody)).thenReturn(Mono.error(new PrivateRouteException()));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .put()
                .uri(uriBuilder -> uriBuilder.path("/users/1").build())
                .accept(MediaType.APPLICATION_NDJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userBody)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .consumeWith(response -> {
                    Map<String, Object> error = response.getResponseBody();
                    assertNotNull(error);
                    assertEquals(error.get("message"), "Not allowed!");
                    assertEquals(error.get("error"), "Forbidden");
                    assertEquals(error.get("status"), 403);
                });
        verify(userService).updateUser(1L, userBody);
        verifyNoMoreInteractions(userService);
        verifyNoInteractions(pageableUserAssembler);
        verifyNoInteractions(userDtoAssembler);
    }

    @Test
    @WithMockUser(username = TEST_EMAIL, authorities = "ROLE_USER")
    public void update_user_not_found() {
        UserBody userBody = UserBody.builder()
                .firstName("FN")
                .lastName("LN")
                .build();

        when(userService.updateUser(1L, userBody)).thenReturn(Mono.error(new NotFoundEntity("user", 1L)));

        webClient
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .put()
                .uri(uriBuilder -> uriBuilder.path("/users/1").build())
                .accept(MediaType.APPLICATION_NDJSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userBody)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .consumeWith(response -> {
                    Map<String, Object> error = response.getResponseBody();
                    assertNotNull(error);
                    assertEquals(error.get("path"), "/users/1");
                    assertEquals(error.get("name"), "user");
                    assertEquals(error.get("id"), 1);
                    assertEquals(error.get("message"), "Entity user with id 1 was not found!");
                    assertEquals(error.get("error"), "Not Found");
                    assertEquals(error.get("status"), 404);
                });

        verify(userService).updateUser(1L, userBody);
        verifyNoMoreInteractions(userService);
        verifyNoInteractions(pageableUserAssembler);
        verifyNoInteractions(userDtoAssembler);
    }


}