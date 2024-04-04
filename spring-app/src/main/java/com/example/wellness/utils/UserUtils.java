package com.example.wellness.utils;


import com.example.wellness.enums.Role;
import com.example.wellness.exceptions.action.PrivateRouteException;
import com.example.wellness.exceptions.notFound.NotFoundEntity;
import com.example.wellness.models.user.UserCustom;
import com.example.wellness.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserUtils {

    private final UserRepository userRepository;


    public Mono<UserCustom> getPrincipal() {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(sc -> {
                    if (sc == null || !sc.getAuthentication().isAuthenticated()) {
                        return Mono.error(new PrivateRouteException());
                    }
                    log.error(String.valueOf(sc.getAuthentication()));
                    return userRepository.findByEmail((String) sc.getAuthentication().getPrincipal()).log();
                });
    }

    public Mono<Boolean> hasPermissionToModifyEntity(UserCustom authUser, Long entityUserId) {
        return Mono.just(authUser.getRole() == Role.ROLE_ADMIN || Objects.equals(authUser.getId(), entityUserId));
    }

    public Mono<Void> existsTrainerOrAdmin(Long trainerId) {
        return userRepository.existsByRoles(trainerId, List.of(Role.ROLE_ADMIN, Role.ROLE_TRAINER))
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(new PrivateRouteException()))
                .then();
    }


    public Mono<UserCustom> getUser(Long userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new NotFoundEntity("user", userId)));

    }
}
