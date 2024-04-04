package com.example.wellness.utils;

import com.example.wellness.enums.Role;
import com.example.wellness.exceptions.action.IllegalActionException;
import com.example.wellness.exceptions.action.NotApprovedEntity;
import com.example.wellness.exceptions.action.SubEntityNotOwner;
import com.example.wellness.exceptions.notFound.NotFoundEntity;
import com.example.wellness.models.generic.Approve;
import com.example.wellness.models.generic.ManyToOneUser;
import com.example.wellness.models.generic.TitleBody;
import com.example.wellness.models.user.UserCustom;
import com.example.wellness.repositories.ExerciseRepository;
import com.example.wellness.repositories.TrainingRepository;
import com.example.wellness.repositories.generic.ApprovedRepository;
import com.example.wellness.repositories.generic.ManyToOneUserRepository;
import com.example.wellness.repositories.generic.CountIds;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class EntitiesUtils {

    private final UserUtils userUtils;
    private final ExerciseRepository exerciseRepository;
    private final TrainingRepository trainingRepository;


    public <M extends ManyToOneUser, R extends ManyToOneUserRepository<M> & CountIds> Mono<Void> validIds(List<Long> ids, R modelRepository, String name) {
        return modelRepository.countByIds(ids)
                .map(count -> count == ids.size())
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(new IllegalActionException(name + " " + ids.toString() + " are not valid")))
                .then();
    }

    public <M extends TitleBody> Mono<M> setReaction(M model, UserCustom user, String type) {
        Set<Long> likes = new HashSet<>(model.getUserLikes());
        Set<Long> dislikes = new HashSet<>(model.getUserDislikes());

        if (type.equals("like")) {
            if (likes.contains(user.getId())) {
                likes.remove(user.getId());
            } else {
                likes.add(user.getId());
                dislikes.remove(user.getId());
            }
        } else if (type.equals("dislike")) {
            if (dislikes.contains(user.getId())) {
                dislikes.remove(user.getId());
            } else {
                dislikes.add(user.getId());
                likes.remove(user.getId());
            }
        }

        model.setUserLikes(likes.stream().toList());
        model.setUserDislikes(dislikes.stream().toList());
        return Mono.just(model);
    }

    public <T> Mono<T> getEntityById(Long id, String name, R2dbcRepository<T, Long> repository) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundEntity(name, id)));
    }


    public <M extends Approve, R extends ApprovedRepository<M>> Mono<Void> verifyMapping(
            R repo, List<Long> ids, String name, boolean pub) {

        return userUtils.getPrincipal().flatMap(
                authUser ->
                        Flux.fromIterable(ids)
                                .flatMap(id ->
                                        repo.findById(id)
                                                .switchIfEmpty(Mono.error(new NotFoundEntity(name, id)))
                                                .flatMap(m -> {
                                                    log.error(String.valueOf(pub), authUser, m.getUserId());
                                                    if (!pub && !authUser.getRole().equals(Role.ROLE_ADMIN)) {
                                                        return checkSubEntityOwner(m, authUser);
                                                    }

                                                    if (!m.isApproved()) {
                                                        return checkApproved(m, name);
                                                    }
                                                    return Mono.empty();
                                                })
                                )
                                .then()
        );
    }


    public Mono<Void> verifyMappingExercises(List<Long> exercises) {
        return verifyMapping(exerciseRepository, exercises, "exercise", false);
    }

    public Mono<Void> verifyMappingTrainings(List<Long> trainings) {
        return verifyMapping(trainingRepository, trainings, "training", false);
    }

    public Mono<Void> checkSubEntityOwner(ManyToOneUser sub, UserCustom user) {
        if (!sub.getUserId().equals(user.getId())) {
            return Mono.error(new SubEntityNotOwner(user.getId(), sub.getUserId(), sub.getId()));
        }
        return Mono.empty();
    }

    public Mono<Void> checkApproved(Approve entity, String name) {
        if (!entity.isApproved()) {
            return Mono.error(new NotApprovedEntity(name, entity.getId()));
        }
        return Mono.empty();
    }


}
