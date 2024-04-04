package com.example.wellness.hateos.linking.generics;

import com.example.wellness.controllers.generics.TitleBodyController;
import com.example.wellness.dto.common.generic.WithUser;
import com.example.wellness.hateos.linking.ReactiveLinkBuilder;
import com.example.wellness.mappers.template.DtoMapper;
import com.example.wellness.models.generic.TitleBody;
import com.example.wellness.repositories.generic.TitleBodyRepository;
import com.example.wellness.services.generics.TitleBodyService;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;

import java.util.List;

public abstract class TitleBodyReactiveLinkBuilder<MODEL extends TitleBody, BODY, RESPONSE extends WithUser,
        S extends TitleBodyRepository<MODEL>, M extends DtoMapper<MODEL, BODY, RESPONSE>,
        G extends TitleBodyService<MODEL, BODY, RESPONSE, S, M>,
        C extends TitleBodyController<MODEL, BODY, RESPONSE, S, M, G>>
        extends ManyToOneUserReactiveLinkBuilder<MODEL, BODY, RESPONSE, S, M, G, C>
        implements ReactiveLinkBuilder<RESPONSE, C> {

    @Override
    public List<WebFluxLinkBuilder.WebFluxLink> createModelLinks(RESPONSE response, Class<C> c) {
        List<WebFluxLinkBuilder.WebFluxLink> links = super.createModelLinks(response, c);
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).likeModel(response.getId())).withRel("like"));
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).dislikeModel(response.getId())).withRel("dislike"));
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).getModelsWithUserAndReaction(response.getId())).withRel("withUser/withReactions"));
        return links;
    }
}
