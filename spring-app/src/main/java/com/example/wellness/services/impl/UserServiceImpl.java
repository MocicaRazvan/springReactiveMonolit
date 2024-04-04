package com.example.wellness.services.impl;

import com.example.wellness.dto.common.PageableBody;
import com.example.wellness.dto.common.response.PageableResponse;
import com.example.wellness.dto.common.UserBody;
import com.example.wellness.dto.common.UserDto;
import com.example.wellness.enums.Role;
import com.example.wellness.exceptions.action.IllegalActionException;
import com.example.wellness.exceptions.action.PrivateRouteException;
import com.example.wellness.mappers.UserMapper;
import com.example.wellness.models.user.UserCustom;
import com.example.wellness.repositories.UserRepository;
import com.example.wellness.services.UserService;
import com.example.wellness.utils.EntitiesUtils;
import com.example.wellness.utils.PageableUtilsCustom;
import com.example.wellness.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final List<String> allowedSortingFields = List.of("firstName", "lastName", "email");

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final EntitiesUtils entitiesUtils;
    private final PageableUtilsCustom pageableUtilsCustom;
    private final UserUtils userUtils;

    @Override
    public Mono<UserDto> getUser(Long id) {
        return entitiesUtils.getEntityById(id, "user", userRepository)
                .map(userMapper::fromUserCustomToUserDto);
    }

    @Override
    public Flux<PageableResponse<UserDto>> getAllUsers(PageableBody pageableBody, Set<Role> roles, String email) {

        final String emailToSearch = email == null ? "" : email;
        if (roles == null) {
            roles = new HashSet<>();
        }

        if (roles.isEmpty()) {
            roles.add(Role.ROLE_USER);
            roles.add(Role.ROLE_ADMIN);
            roles.add(Role.ROLE_TRAINER);
        }
        log.info(pageableBody.getSortingCriteria().toString());
        log.info(allowedSortingFields.toString());

        final Set<Role> finalRoles = roles;
        return pageableUtilsCustom.isSortingCriteriaValid(pageableBody.getSortingCriteria(), allowedSortingFields)
                .then(pageableUtilsCustom.createPageRequest(pageableBody))
                .flatMapMany(pr -> pageableUtilsCustom.createPageableResponse(userRepository.findAllByRoleInAndEmailContainingIgnoreCase(finalRoles, email, pr).map(userMapper::fromUserCustomToUserDto),
                        userRepository.countAllByRoleInAndEmailContainingIgnoreCase(finalRoles, email), pr)
                );


    }


    @Override
    public Mono<UserDto> makeTrainer(Long id) {
        return entitiesUtils.getEntityById(id, "user", userRepository)
                .flatMap(user -> {
                    if (user.getRole().equals(Role.ROLE_ADMIN)) {
                        return Mono.error(new IllegalActionException("User is admin!"));
                    } else if (user.getRole().equals(Role.ROLE_TRAINER)) {
                        return Mono.error(new IllegalActionException("User is trainer!"));
                    }
                    user.setRole(Role.ROLE_TRAINER);
                    return userRepository.save(user).map(userMapper::fromUserCustomToUserDto);
                });
    }

    @Override
    public Mono<UserDto> updateUser(Long id, UserBody userBody) {
        return entitiesUtils.getEntityById(id, "user", userRepository)
                .zipWith(userUtils.getPrincipal())
                .flatMap(tuple -> {
                    UserCustom user = tuple.getT1();
                    UserCustom authUser = tuple.getT2();

                    if (!user.getId().equals(authUser.getId())) {
                        return Mono.error(new PrivateRouteException());
                    }
                    user.setLastName(userBody.getLastName());
                    user.setFirstName(userBody.getFirstName());
                    user.setImage(userBody.getImage());
                    return userRepository.save(user).map(userMapper::fromUserCustomToUserDto);
                });

    }
}
