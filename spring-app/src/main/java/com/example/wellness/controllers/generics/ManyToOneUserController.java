package com.example.wellness.controllers.generics;

import com.example.wellness.dto.common.PageableBody;
import com.example.wellness.dto.common.response.PageableResponse;
import com.example.wellness.dto.common.response.ResponseWithUserDtoEntity;
import com.example.wellness.dto.common.generic.WithUser;
import com.example.wellness.hateos.CustomEntityModel;
import com.example.wellness.mappers.template.DtoMapper;
import com.example.wellness.models.generic.ManyToOneUser;
import com.example.wellness.repositories.generic.ManyToOneUserRepository;
import com.example.wellness.services.generics.ManyToOneUserService;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


//@RequiredArgsConstructor
public interface ManyToOneUserController<MODEL extends ManyToOneUser, BODY, RESPONSE extends WithUser,
        S extends ManyToOneUserRepository<MODEL>, M extends DtoMapper<MODEL, BODY, RESPONSE>,
        G extends ManyToOneUserService<MODEL, BODY, RESPONSE, S, M>> {

    //    protected final G modelService;
//    protected final String modelName;
//    @Value("${wellness.openapi.dev-url}")
//    protected String devUrl;
    @DeleteMapping("/delete/{id}")
    Mono<ResponseEntity<CustomEntityModel<RESPONSE>>> deleteModel(@PathVariable Long id);

    @GetMapping("/{id}")
    Mono<ResponseEntity<CustomEntityModel<RESPONSE>>> getModelById(@PathVariable Long id);

    @GetMapping("/withUser/{id}")
    Mono<ResponseEntity<ResponseWithUserDtoEntity<RESPONSE>>> getModelByIdWithUser(@PathVariable Long id);

    @PutMapping("/update/{id}")
    Mono<ResponseEntity<CustomEntityModel<RESPONSE>>> updateModel(@Valid @RequestBody BODY body,
                                                                  @PathVariable Long id);

    @GetMapping("/byIds")
    @ResponseStatus(HttpStatus.OK)
    Flux<PageableResponse<CustomEntityModel<RESPONSE>>> getModelsByIdIn(@Valid @RequestBody PageableBody pageableBody,
                                                                        @RequestParam List<Long> ids);
}
