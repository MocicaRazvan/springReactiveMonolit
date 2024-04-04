package com.example.wellness.services;

import com.example.wellness.dto.common.PageableBody;
import com.example.wellness.dto.common.UserBody;
import com.example.wellness.dto.common.UserDto;
import com.example.wellness.dto.common.response.PageableResponse;
import com.example.wellness.enums.Role;
import com.example.wellness.exceptions.action.IllegalActionException;
import com.example.wellness.exceptions.action.PrivateRouteException;
import com.example.wellness.mappers.UserMapper;
import com.example.wellness.models.user.UserCustom;
import com.example.wellness.repositories.UserRepository;
import com.example.wellness.services.impl.UserServiceImpl;
import com.example.wellness.utils.EntitiesUtils;
import com.example.wellness.utils.PageableUtilsCustom;
import com.example.wellness.utils.UserUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("tc")
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EntitiesUtils entitiesUtils;

    @Mock
    private PageableUtilsCustom pageableUtilsCustom;

    @Mock
    private UserUtils userUtils;

    @InjectMocks
    private UserServiceImpl userService;

    UserCustom user;

    UserCustom admin;
    UserCustom trainer;

    UserDto userDto;

    UserDto adminDto;

    UserDto trainerDto;

    PageableBody pageableBody = PageableBody.builder()
            .page(0)
            .size(10)
            .sortingCriteria(Map.of("id", "asc"))
            .build();

    PageRequest pageRequest = PageRequest.of(0, 10);

    List<String> allowedSortingFields = List.of("firstName", "lastName", "email");

    @BeforeEach
    void setUp() {

        user = UserCustom.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password")
                .role(Role.ROLE_USER)
                .build();

        admin = UserCustom.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .password("password")
                .role(Role.ROLE_ADMIN)
                .build();

        trainer = UserCustom.builder()
                .id(3L)
                .firstName("Jim")
                .lastName("Doe")
                .email("jim.doe@example.com")
                .password("password")
                .role(Role.ROLE_TRAINER)
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .role(Role.ROLE_USER)
                .build();

        adminDto = UserDto.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")

                .role(Role.ROLE_ADMIN)
                .build();

        trainerDto = UserDto.builder()
                .id(3L)
                .firstName("Jim")
                .lastName("Doe")
                .email("jim.doe@example.com")
                .role(Role.ROLE_TRAINER)
                .build();
    }


    @Test
    void getUser() {
        when(entitiesUtils.getEntityById(1L, "user", userRepository)).thenReturn(Mono.just(user));
        when(userMapper.fromUserCustomToUserDto(user)).thenReturn(userDto);
        Mono<UserDto> userDtoMono = userService.getUser(1L);
        StepVerifier.create(userDtoMono)
                .expectNext(userDto)
                .verifyComplete();

        verify(entitiesUtils).getEntityById(1L, "user", userRepository);
        verify(userMapper).fromUserCustomToUserDto(user);
    }

    @Test
    public void getAllUsers_Role_Admin() {
        when(pageableUtilsCustom.isSortingCriteriaValid(pageableBody.getSortingCriteria(), allowedSortingFields)).thenReturn(Mono.empty());
        when(pageableUtilsCustom.createPageRequest(pageableBody)).thenReturn(Mono.just(pageRequest));
        when(userMapper.fromUserCustomToUserDto(admin)).thenReturn(adminDto);
        when(userRepository.countAllByRoleInAndEmailContainingIgnoreCase(Set.of(Role.ROLE_ADMIN), admin.getEmail())).thenReturn(Mono.just(1L));
        when(userRepository.findAllByRoleInAndEmailContainingIgnoreCase(Set.of(Role.ROLE_ADMIN), admin.getEmail(), pageRequest)).thenReturn(Flux.just(admin));
        when(pageableUtilsCustom.createPageableResponse(
                any(Flux.class), any(Mono.class), any(PageRequest.class)
        )).thenReturn(Flux.just(PageableResponse.<UserDto>builder()
                .content(adminDto)
                .build())
        );

        StepVerifier.create(userService.getAllUsers(pageableBody, Set.of(Role.ROLE_ADMIN), admin.getEmail()))
                .assertNext(pageableResponse -> {
                    assertNotNull(pageableResponse);
                    assertEquals(adminDto, pageableResponse.getContent());
                })
                .verifyComplete();

        verify(pageableUtilsCustom).isSortingCriteriaValid(pageableBody.getSortingCriteria(), allowedSortingFields);
        verify(pageableUtilsCustom).createPageRequest(pageableBody);
        verify(userRepository).countAllByRoleInAndEmailContainingIgnoreCase(Set.of(Role.ROLE_ADMIN), admin.getEmail());
        verify(userRepository).findAllByRoleInAndEmailContainingIgnoreCase(Set.of(Role.ROLE_ADMIN), admin.getEmail(), pageRequest);

        ArgumentCaptor<Flux<UserDto>> fluxArgumentCaptor = ArgumentCaptor.forClass(Flux.class);


        verify(pageableUtilsCustom).createPageableResponse(
                fluxArgumentCaptor.capture(),
                any(Mono.class),
                any(PageRequest.class)
        );
        Flux<UserDto> capturedFlux = fluxArgumentCaptor.getValue();
        StepVerifier.create(capturedFlux)
                .expectNext(adminDto)
                .verifyComplete();
    }

    static Stream<Set<Role>> roleProvider() {
        return Stream.of(null, new HashSet<>());
    }

    @ParameterizedTest
    @MethodSource("roleProvider")
    public void getAllUser_RoleEmpty(Set<Role> roles) {
        when(pageableUtilsCustom.isSortingCriteriaValid(pageableBody.getSortingCriteria(), allowedSortingFields)).thenReturn(Mono.empty());
        when(pageableUtilsCustom.createPageRequest(pageableBody)).thenReturn(Mono.just(pageRequest));
        when(userMapper.fromUserCustomToUserDto(admin)).thenReturn(adminDto);
        when(userRepository.countAllByRoleInAndEmailContainingIgnoreCase(any(), any(String.class))).thenReturn(Mono.just(1L));
        when(userRepository.findAllByRoleInAndEmailContainingIgnoreCase(any(), any(String.class), eq(pageRequest))).thenReturn(Flux.just(admin));
        when(pageableUtilsCustom.createPageableResponse(
                any(Flux.class), any(Mono.class), any(PageRequest.class)
        )).thenReturn(Flux.just(PageableResponse.<UserDto>builder()
                .content(adminDto)
                .build())
        );

        StepVerifier.create(userService.getAllUsers(pageableBody, roles, ""))
                .assertNext(pageableResponse -> {
                    assertNotNull(pageableResponse);
                    assertEquals(adminDto, pageableResponse.getContent());
                })
                .verifyComplete();

        verify(pageableUtilsCustom).isSortingCriteriaValid(pageableBody.getSortingCriteria(), allowedSortingFields);
        verify(pageableUtilsCustom).createPageRequest(pageableBody);

        ArgumentCaptor<Set<Role>> setArgumentCaptor = ArgumentCaptor.forClass(Set.class);
        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);

        verify(userRepository).countAllByRoleInAndEmailContainingIgnoreCase(setArgumentCaptor.capture(), emailCaptor.capture());

        assertTrue(setArgumentCaptor.getValue().contains(Role.ROLE_USER));
        assertTrue(emailCaptor.getValue().isEmpty());

        verify(userRepository).findAllByRoleInAndEmailContainingIgnoreCase(any(), eq(""), eq(pageRequest));

        ArgumentCaptor<Flux<UserDto>> fluxArgumentCaptor = ArgumentCaptor.forClass(Flux.class);


        verify(pageableUtilsCustom).createPageableResponse(
                fluxArgumentCaptor.capture(),
                any(Mono.class),
                any(PageRequest.class)
        );
        Flux<UserDto> capturedFlux = fluxArgumentCaptor.getValue();
        StepVerifier.create(capturedFlux)
                .expectNext(adminDto)
                .verifyComplete();


    }

    @Test
    public void makeTrainer_Success() {
        when(entitiesUtils.getEntityById(1L, "user", userRepository)).thenReturn(Mono.just(user));
        when(userRepository.save(user)).thenReturn(Mono.just(user));
        when(userMapper.fromUserCustomToUserDto(user)).then(u -> UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .build());

        userDto.setRole(Role.ROLE_TRAINER);
        StepVerifier.create(userService.makeTrainer(1L))
                .assertNext(u -> {
                    assertEquals(userDto, u);
                })
                .verifyComplete();

        verify(entitiesUtils).getEntityById(1L, "user", userRepository);
        verify(userRepository).save(user);
        verify(userMapper).fromUserCustomToUserDto(user);
    }

    @Test
    public void makeTrainer_UserAlreadyTrainer() {
        when(entitiesUtils.getEntityById(1L, "user", userRepository)).thenReturn(Mono.just(trainer));
        StepVerifier.create(userService.makeTrainer(1L))
                .expectErrorMatches(e -> {
                    assertTrue(e instanceof IllegalActionException);
                    assertEquals("User is trainer!", e.getMessage());
                    return true;
                })
                .verify();

        verify(entitiesUtils).getEntityById(1L, "user", userRepository);
        verifyNoInteractions(userRepository);
        verifyNoInteractions(userMapper);
    }

    @Test
    public void makeTrainer_UserIsAdmin() {
        when(entitiesUtils.getEntityById(1L, "user", userRepository)).thenReturn(Mono.just(admin));
        StepVerifier.create(userService.makeTrainer(1L))
                .expectErrorMatches(e -> {
                    assertTrue(e instanceof IllegalActionException);
                    assertEquals("User is admin!", e.getMessage());
                    return true;
                })
                .verify();

        verify(entitiesUtils).getEntityById(1L, "user", userRepository);
        verifyNoInteractions(userRepository);
        verifyNoInteractions(userMapper);
    }

    @Test
    public void updateUser_Success() {
        when(entitiesUtils.getEntityById(1L, "user", userRepository)).thenReturn(Mono.just(user));
        when(userUtils.getPrincipal()).thenReturn(Mono.just(user));
        when(userRepository.save(any(UserCustom.class))).then(u -> Mono.just(u.getArgument(0)));
        when(userMapper.fromUserCustomToUserDto(user)).then(u -> UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .build());


        UserBody userBody = UserBody.builder()
                .firstName("NEW FS")
                .lastName("NEW LN")
                .build();

        UserDto updatedUserDto = UserDto.builder()
                .id(1L)
                .firstName("NEW FS")
                .lastName("NEW LN")
                .email(user.getEmail())
                .role(user.getRole())
                .build();


        StepVerifier.create(userService.updateUser(1L, userBody))
                .assertNext(u -> {
                    assertEquals(updatedUserDto, u);
                })
                .verifyComplete();

        verify(entitiesUtils).getEntityById(1L, "user", userRepository);
        verify(userUtils).getPrincipal();
        ArgumentCaptor<UserCustom> userCaptor = ArgumentCaptor.forClass(UserCustom.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("NEW FS", userCaptor.getValue().getFirstName());
        assertEquals("NEW LN", userCaptor.getValue().getLastName());
        verify(userMapper).fromUserCustomToUserDto(userCaptor.getValue());
        assertEquals("NEW FS", userCaptor.getValue().getFirstName());
        assertEquals("NEW LN", userCaptor.getValue().getLastName());

    }

    @Test
    public void updateUser_NotOwner() {
        when(entitiesUtils.getEntityById(1L, "user", userRepository)).thenReturn(Mono.just(user));
        when(userUtils.getPrincipal()).thenReturn(Mono.just(admin));

        UserBody userBody = UserBody.builder()
                .firstName("NEW FS")
                .lastName("NEW LN")
                .build();

        StepVerifier.create(userService.updateUser(1L, userBody))
                .expectError(PrivateRouteException.class)
                .verify();

        verify(entitiesUtils).getEntityById(1L, "user", userRepository);
        verify(userUtils).getPrincipal();
        verifyNoInteractions(userRepository);
        verifyNoInteractions(userMapper);
    }


}