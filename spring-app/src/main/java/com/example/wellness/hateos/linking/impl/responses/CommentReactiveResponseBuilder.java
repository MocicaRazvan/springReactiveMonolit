package com.example.wellness.hateos.linking.impl.responses;

import com.example.wellness.controllers.CommentController;
import com.example.wellness.dto.comment.CommentResponse;
import com.example.wellness.hateos.linking.ReactiveResponseBuilder;
import com.example.wellness.hateos.linking.impl.links.CommentReactiveLinkBuilder;
import com.example.wellness.hateos.user.UserDtoAssembler;
import org.springframework.stereotype.Component;

@Component
public class CommentReactiveResponseBuilder extends ReactiveResponseBuilder<CommentResponse, CommentController> {
    public CommentReactiveResponseBuilder(UserDtoAssembler userDtoAssembler) {
        super(userDtoAssembler, new CommentReactiveLinkBuilder());
    }
}
