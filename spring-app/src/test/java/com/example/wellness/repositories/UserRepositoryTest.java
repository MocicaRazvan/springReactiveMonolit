package com.example.wellness.repositories;

import com.example.wellness.config.TrxStepVerifier;
import com.example.wellness.config.TrxStepVerifierTestConfig;
import com.example.wellness.enums.Role;
import com.example.wellness.models.user.UserCustom;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataR2dbcTest
@ActiveProfiles("tc")
@Import(TrxStepVerifierTestConfig.class)
@Testcontainers
public class UserRepositoryTest extends AbstractPostgresContainerBase {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TrxStepVerifier trxStepVerifier;

    UserCustom user = UserCustom.builder()
            .id(1L)
            .firstName("John")
            .lastName("Doe")
            .email("john.doe@example.com")
            .password("password")
            .role(Role.ROLE_USER)
            .createdAt(LocalDateTime.of(2021, 1, 1, 0, 0, 0))
            .updatedAt(LocalDateTime.of(2021, 1, 1, 0, 0, 0))
            .build();

    UserCustom admin = UserCustom.builder()
            .id(2L)
            .firstName("Jane")
            .lastName("Doe")
            .email("jane.doe@example.com")
            .password("password")
            .role(Role.ROLE_ADMIN)
            .createdAt(LocalDateTime.of(2021, 1, 1, 0, 0, 0))
            .updatedAt(LocalDateTime.of(2021, 1, 1, 0, 0, 0))
            .build();

    UserCustom trainer = UserCustom.builder()
            .id(3L)
            .firstName("Jim")
            .lastName("Doe")
            .email("jim.doe@example.com")
            .password("password")
            .role(Role.ROLE_TRAINER)
            .createdAt(LocalDateTime.of(2021, 1, 1, 0, 0, 0))
            .updatedAt(LocalDateTime.of(2021, 1, 1, 0, 0, 0))
            .build();


    @Test
    public void findAllUser() {
        List<UserCustom> expectedUsers = List.of(user, admin, trainer);

        trxStepVerifier.create(userRepository.findAll().collectList())
                .assertNext(users -> {
                    assertEquals(expectedUsers.size(), users.size());
                    assertTrue(users.containsAll(expectedUsers));
                })
                .verifyComplete();
    }

    @ParameterizedTest
    @ValueSource(strings = {"john.doe@example.com", "jane.doe@example.com", "jim.doe@example.com"})
    public void findByEmail_UserExists(String email) {
        trxStepVerifier.create(userRepository.findByEmail(email))
                .assertNext(user -> {
                    assertNotNull(user);
                    assertEquals(email, user.getEmail());
                })
                .verifyComplete();
    }

    @Test
    public void findByEmail_UserDoesNotExist() {
        trxStepVerifier.create(userRepository.findByEmail("not@gmai.com"))
                .expectNextCount(0)
                .verifyComplete();
    }

    @ParameterizedTest
    @ValueSource(strings = {"john.doe@example.com", "jane.doe@example.com", "jim.doe@example.com"})
    public void existsByEmail_UserExists(String email) {
        trxStepVerifier.create(userRepository.existsByEmail(email))
                .assertNext(Assertions::assertTrue)
                .verifyComplete();
    }

    @Test
    public void existsByEmail_UserDoesNotExist() {
        trxStepVerifier.create(userRepository.existsByEmail("not@gmail.com"))
                .assertNext(Assertions::assertFalse)
                .verifyComplete();
    }

    @Test
    public void findAllByRoleIn_AllRoles_AllUsersReturned_EmailEmpty() {
        List<UserCustom> expectedUsers = List.of(user, admin, trainer);


        trxStepVerifier.create(userRepository.findAllByRoleInAndEmailContainingIgnoreCase(Set.of(Role.ROLE_USER, Role.ROLE_ADMIN, Role.ROLE_TRAINER), "", PageRequest.of(0, 10)).collectList())
                .assertNext(users -> {
                    assertEquals(expectedUsers.size(), users.size());
                    assertTrue(users.containsAll(expectedUsers));
                })
                .expectNextCount(0)
                .verifyComplete();
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "1", "2"})
    public void findAllByRoleIn_AllRoles_OneUserReturned_EmailEmpty(String pn) {
        trxStepVerifier.create(userRepository.findAllByRoleInAndEmailContainingIgnoreCase(Set.of(Role.ROLE_USER, Role.ROLE_ADMIN, Role.ROLE_TRAINER), "", PageRequest.of(Integer.parseInt(pn), 1)))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void findAllByRoleIn_Role_Admin_EmailPresent() {
        trxStepVerifier.create(userRepository.findAllByRoleInAndEmailContainingIgnoreCase(Set.of(Role.ROLE_ADMIN), "ja", PageRequest.of(0, 10)))
                .expectNextMatches(admin::equals)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    public void countAllByRoleIn_AllRoles_EmailEmpty() {
        trxStepVerifier.create(userRepository.countAllByRoleInAndEmailContainingIgnoreCase(Set.of(Role.ROLE_USER, Role.ROLE_ADMIN, Role.ROLE_TRAINER), ""))
                .assertNext(count -> assertEquals(3, count))
                .verifyComplete();
    }

    @Test
    public void countAllByRoleIn_Role_Admin_EmailPresent_Empty() {
        trxStepVerifier.create(userRepository.countAllByRoleInAndEmailContainingIgnoreCase(Set.of(Role.ROLE_ADMIN), "razvan"))
                .assertNext(count -> assertEquals(0, count))
                .verifyComplete();
    }

    @Test
    public void existsByRoles() {
        trxStepVerifier.create(userRepository.existsByRoles(1L, List.of(Role.ROLE_USER, Role.ROLE_ADMIN, Role.ROLE_TRAINER)))
                .assertNext(Assertions::assertTrue)
                .verifyComplete();
    }

    @Test
    void saveUser() {
        UserCustom user = UserCustom.builder()
                .lastName("Mocica")
                .firstName("Razvan")
                .email("R@G")
                .password("123")
                .role(Role.ROLE_USER)
                .build();

        trxStepVerifier.create(userRepository.save(user))
                .assertNext(u -> {
                    user.setId(4L);
                    assertEquals(user, u);
                })
                .verifyComplete();

    }

    @Test
    public void saveUser_Error() {
        UserCustom user = UserCustom.builder().build();

        trxStepVerifier.create(userRepository.save(user))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }


    @Test
    void updateUser() {

        admin.setEmail("UPDATED");

        trxStepVerifier.create(userRepository.save(admin))
                .assertNext(u -> assertEquals(admin, u))
                .verifyComplete();


    }

}
