package com.example.wellness.controllers.generics;

import com.example.wellness.dto.common.PageableBody;
import com.example.wellness.dto.common.response.PageableResponse;
import com.example.wellness.dto.common.generic.TitleBody;
import com.example.wellness.dto.common.generic.WithUser;
import com.example.wellness.hateos.CustomEntityModel;
import com.example.wellness.mappers.template.DtoMapper;
import com.example.wellness.models.generic.Approve;
import com.example.wellness.repositories.generic.ApprovedRepository;
import com.example.wellness.services.generics.ApprovedService;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ApproveController<MODEL extends Approve, BODY extends TitleBody, RESPONSE extends WithUser,
        S extends ApprovedRepository<MODEL>, M extends DtoMapper<MODEL, BODY, RESPONSE>,
        G extends ApprovedService<MODEL, BODY, RESPONSE, S, M>>

        extends TitleBodyController<MODEL, BODY, RESPONSE, S, M, G> {


    @PatchMapping("/approved")
    @ResponseStatus(HttpStatus.OK)
    Flux<PageableResponse<CustomEntityModel<RESPONSE>>> getModelsApproved(@RequestParam(required = false) String title, @Valid @RequestBody PageableBody pageableBody);

    @PatchMapping("/trainer/{trainerId}")
    @ResponseStatus(HttpStatus.OK)
    Flux<PageableResponse<CustomEntityModel<RESPONSE>>> getModelsTrainer(@RequestParam(required = false) String title, @Valid @RequestBody PageableBody pageableBody, @PathVariable Long trainerId);

    @PostMapping("/create")
    Mono<ResponseEntity<CustomEntityModel<RESPONSE>>> createModel(@Valid @RequestBody BODY body);

    @PatchMapping("/admin/approve/{id}")
    Mono<ResponseEntity<CustomEntityModel<RESPONSE>>> approveModel(@PathVariable Long id);

    @PatchMapping("/admin")
    Flux<PageableResponse<CustomEntityModel<RESPONSE>>> getAllModelsAdmin(@RequestParam(required = false) String title, @Valid @RequestBody PageableBody pageableBody);

}
