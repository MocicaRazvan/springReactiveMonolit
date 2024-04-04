package com.example.wellness.services.generics;

import reactor.core.publisher.Mono;

import java.util.List;

public interface ValidIds {

    Mono<Void> validIds(List<Long> ids);
}
