package com.example.wellness.repositories;

import com.example.wellness.models.Post;
import com.example.wellness.repositories.generic.ApprovedRepository;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PostRepository extends ApprovedRepository<Post> {

}
