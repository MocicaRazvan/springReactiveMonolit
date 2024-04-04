package com.example.wellness.hateos.linking.impl.links;

import com.example.wellness.controllers.CommentController;
import com.example.wellness.dto.comment.CommentBody;
import com.example.wellness.dto.comment.CommentResponse;
import com.example.wellness.hateos.linking.generics.TitleBodyReactiveLinkBuilder;
import com.example.wellness.mappers.CommentMapper;
import com.example.wellness.models.Comment;
import com.example.wellness.repositories.CommentRepository;
import com.example.wellness.services.CommentService;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;

import java.util.List;


public class CommentReactiveLinkBuilder extends TitleBodyReactiveLinkBuilder<Comment, CommentBody, CommentResponse,
        CommentRepository, CommentMapper, CommentService, CommentController> {

    @Override
    public List<WebFluxLinkBuilder.WebFluxLink> createModelLinks(CommentResponse commentResponse, Class<CommentController> c) {
        List<WebFluxLinkBuilder.WebFluxLink> links = super.createModelLinks(commentResponse, c);
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).createComment(commentResponse.getPostId(), null)).withRel("create"));
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).getCommentsByPost(commentResponse.getPostId(), null)).withRel("getByPost"));
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).getCommentsByUser(commentResponse.getUserId(), null)).withRel("getByUser"));
        return links;
    }
}
