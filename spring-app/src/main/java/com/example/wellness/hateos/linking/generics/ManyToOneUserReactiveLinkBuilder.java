package com.example.wellness.hateos.linking.generics;

import com.example.wellness.controllers.generics.ManyToOneUserController;
import com.example.wellness.dto.common.generic.WithUser;
import com.example.wellness.hateos.linking.ReactiveLinkBuilder;
import com.example.wellness.mappers.template.DtoMapper;
import com.example.wellness.models.generic.ManyToOneUser;
import com.example.wellness.repositories.generic.ManyToOneUserRepository;
import com.example.wellness.services.generics.ManyToOneUserService;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;

import java.util.ArrayList;
import java.util.List;

public abstract class ManyToOneUserReactiveLinkBuilder<
        MODEL extends ManyToOneUser, BODY, RESPONSE extends WithUser,
        S extends ManyToOneUserRepository<MODEL>, M extends DtoMapper<MODEL, BODY, RESPONSE>,
        G extends ManyToOneUserService<MODEL, BODY, RESPONSE, S, M>,
        C extends ManyToOneUserController<MODEL, BODY, RESPONSE, S, M, G>>
        implements ReactiveLinkBuilder<RESPONSE, C> {


    @Override
    public List<WebFluxLinkBuilder.WebFluxLink> createModelLinks(RESPONSE response, Class<C> c) {
        List<WebFluxLinkBuilder.WebFluxLink> links = new ArrayList<>();
        links.add(WebFluxLinkBuilder.linkTo(
                WebFluxLinkBuilder.methodOn(c).deleteModel(response.getId())).withRel("delete"));
        links.add(
                WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).getModelById(response.getId())).withSelfRel());
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).getModelByIdWithUser(response.getId())).withRel("withUser"));
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).updateModel(null, response.getId())).withRel("update"));

        return links;
    }


}
