package com.example.wellness.hateos.linking.impl.responses;

import com.example.wellness.controllers.PostController;
import com.example.wellness.dto.post.PostResponse;
import com.example.wellness.hateos.linking.ReactiveResponseBuilder;
import com.example.wellness.hateos.linking.impl.links.PostReactiveLinkBuilder;
import com.example.wellness.hateos.user.UserDtoAssembler;
import org.springframework.stereotype.Component;


@Component
public class PostReactiveResponseBuilder extends ReactiveResponseBuilder<PostResponse, PostController> {
    public PostReactiveResponseBuilder(UserDtoAssembler userDtoAssembler) {
        super(userDtoAssembler, new PostReactiveLinkBuilder());
    }
}
