package com.example.wellness.controllers.generics;

import com.example.wellness.dto.common.response.ResponseWithUserLikesAndDislikesEntity;
import com.example.wellness.dto.common.generic.WithUser;
import com.example.wellness.hateos.CustomEntityModel;
import com.example.wellness.mappers.template.DtoMapper;
import com.example.wellness.models.generic.TitleBody;
import com.example.wellness.repositories.generic.TitleBodyRepository;
import com.example.wellness.services.generics.TitleBodyService;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Mono;

public interface TitleBodyController<MODEL extends TitleBody, BODY, RESPONSE extends WithUser,
        S extends TitleBodyRepository<MODEL>, M extends DtoMapper<MODEL, BODY, RESPONSE>,
        G extends TitleBodyService<MODEL, BODY, RESPONSE, S, M>>
        extends ManyToOneUserController<MODEL, BODY, RESPONSE, S, M, G> {

    @PatchMapping("/like/{id}")
    Mono<ResponseEntity<CustomEntityModel<RESPONSE>>> likeModel(@PathVariable Long id);

    @PatchMapping("/dislike/{id}")
    Mono<ResponseEntity<CustomEntityModel<RESPONSE>>> dislikeModel(@PathVariable Long id);

    @GetMapping("/withUser/withReactions/{id}")
    Mono<ResponseEntity<ResponseWithUserLikesAndDislikesEntity<RESPONSE>>> getModelsWithUserAndReaction(@PathVariable Long id);
}
