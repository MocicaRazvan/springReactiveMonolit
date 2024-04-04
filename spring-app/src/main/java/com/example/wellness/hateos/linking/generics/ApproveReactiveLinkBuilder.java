package com.example.wellness.hateos.linking.generics;

import com.example.wellness.controllers.generics.ApproveController;
import com.example.wellness.dto.common.PageableBody;
import com.example.wellness.dto.common.generic.TitleBody;
import com.example.wellness.dto.common.generic.TitleBodyUser;
import com.example.wellness.hateos.linking.ReactiveLinkBuilder;
import com.example.wellness.mappers.template.DtoMapper;
import com.example.wellness.models.generic.Approve;
import com.example.wellness.repositories.generic.ApprovedRepository;
import com.example.wellness.services.generics.ApprovedService;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;

import java.util.List;

public abstract class ApproveReactiveLinkBuilder<MODEL extends Approve, BODY extends TitleBody, RESPONSE extends TitleBodyUser,
        S extends ApprovedRepository<MODEL>, M extends DtoMapper<MODEL, BODY, RESPONSE>,
        G extends ApprovedService<MODEL, BODY, RESPONSE, S, M>,
        C extends ApproveController<MODEL, BODY, RESPONSE, S, M, G>
        >
        extends TitleBodyReactiveLinkBuilder<MODEL, BODY, RESPONSE, S, M, G, C>
        implements ReactiveLinkBuilder<RESPONSE, C> {

    @Override
    public List<WebFluxLinkBuilder.WebFluxLink> createModelLinks(RESPONSE response, Class<C> c) {
        List<WebFluxLinkBuilder.WebFluxLink> links = super.createModelLinks(response, c);
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).approveModel(response.getId())).withRel("approve"));
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).getModelsTrainer(response.getTitle(),
                PageableBody.builder().page(0).size(10).build(), response.getUserId())).withRel("models by trainer"));
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).createModel(null)).withRel("create"));
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).getModelsApproved(response.getTitle(),
                PageableBody.builder().page(0).size(10).build())).withRel("approved models"));
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).getAllModelsAdmin(response.getTitle(),
                PageableBody.builder().page(0).size(10).build())).withRel("all models admin"));
        return links;
    }
}
