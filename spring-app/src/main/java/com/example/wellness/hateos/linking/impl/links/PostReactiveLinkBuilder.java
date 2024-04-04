package com.example.wellness.hateos.linking.impl.links;

import com.example.wellness.controllers.PostController;
import com.example.wellness.dto.post.PostBody;
import com.example.wellness.dto.post.PostResponse;
import com.example.wellness.hateos.linking.generics.ApproveReactiveLinkBuilder;
import com.example.wellness.mappers.PostMapper;
import com.example.wellness.models.Post;
import com.example.wellness.repositories.PostRepository;
import com.example.wellness.services.PostService;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;

import java.util.List;

public class PostReactiveLinkBuilder extends ApproveReactiveLinkBuilder<Post, PostBody, PostResponse, PostRepository, PostMapper, PostService, PostController> {


    @Override
    public List<WebFluxLinkBuilder.WebFluxLink> createModelLinks(PostResponse postResponse, Class<PostController> c) {
        List<WebFluxLinkBuilder.WebFluxLink> links = super.createModelLinks(postResponse, c);
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).getPostWithComments(postResponse.getId())).withRel("getWithComments"));
        return links;
    }
}
