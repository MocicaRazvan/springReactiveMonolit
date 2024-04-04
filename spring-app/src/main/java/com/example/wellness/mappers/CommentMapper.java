package com.example.wellness.mappers;


import com.example.wellness.dto.comment.CommentBody;
import com.example.wellness.dto.comment.CommentResponse;
import com.example.wellness.mappers.template.DtoMapper;
import com.example.wellness.models.Comment;
import org.mapstruct.Mapper;
import reactor.core.publisher.Mono;

@Mapper(componentModel = "spring")
public abstract class CommentMapper extends DtoMapper<Comment, CommentBody, CommentResponse> {

    //    public abstract Comment fromBodyToModel(CommentBody body);
//
//    public abstract  CommentResponse fromModelToResponse(Comment comment);
    @Override
    public Mono<Comment> updateModelFromBody(CommentBody body, Comment comment) {
        comment.setBody(body.getBody());
        comment.setTitle(body.getTitle());
        comment.setImages(body.getImages());
        return Mono.just(comment);
    }
}
