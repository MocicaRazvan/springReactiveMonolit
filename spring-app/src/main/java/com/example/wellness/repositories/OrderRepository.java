package com.example.wellness.repositories;


import com.example.wellness.models.Order;
import com.example.wellness.repositories.generic.ManyToOneUserRepository;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderRepository extends ManyToOneUserRepository<Order> {

    Flux<Order> findAllByPayed(boolean payed, PageRequest request);

    Flux<Order> findAllBy(PageRequest request);

    Flux<Order> findAllByUserIdAndPayed(Long userId, boolean payed, PageRequest request);

    Mono<Long> countAllByPayed(boolean payed);

    Mono<Long> countAllByUserId(Long userId);

    Mono<Long> countAllByUserIdAndPayed(Long userId, boolean payed);


}
