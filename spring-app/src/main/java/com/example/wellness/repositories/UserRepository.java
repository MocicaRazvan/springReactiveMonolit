package com.example.wellness.repositories;

import com.example.wellness.enums.Role;
import com.example.wellness.models.user.UserCustom;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

@Repository
public interface UserRepository extends R2dbcRepository<UserCustom, Long> {

    Mono<UserCustom> findByEmail(String email);

    Mono<Boolean> existsByEmail(String email);

    Flux<UserCustom> findAllByRoleInAndEmailContainingIgnoreCase(Set<Role> roles, String email, PageRequest pageRequest);

    Mono<Long> countAllByRoleInAndEmailContainingIgnoreCase(Set<Role> roles, String email);


    @Query("SELECT COUNT(*) > 0 FROM user_custom u WHERE u.id = :userId AND u.role IN (:roles)")
    Mono<Boolean> existsByRoles(Long userId, List<Role> roles);

}
