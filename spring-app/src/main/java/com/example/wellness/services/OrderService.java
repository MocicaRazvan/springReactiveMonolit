package com.example.wellness.services;

import com.example.wellness.dto.common.response.ResponseWithChildList;
import com.example.wellness.dto.common.PageableBody;
import com.example.wellness.dto.common.response.PageableResponse;
import com.example.wellness.dto.common.response.ResponseWithUserDto;
import com.example.wellness.dto.exercise.ExerciseResponse;
import com.example.wellness.dto.order.OrderBody;
import com.example.wellness.dto.order.OrderResponse;
import com.example.wellness.dto.order.PriceDto;
import com.example.wellness.dto.training.TrainingResponse;
import com.example.wellness.enums.OrderType;
import com.example.wellness.mappers.OrderMapper;
import com.example.wellness.models.Order;
import com.example.wellness.repositories.OrderRepository;
import com.example.wellness.services.generics.ManyToOneUserService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderService extends ManyToOneUserService<Order, OrderBody, OrderResponse, OrderRepository, OrderMapper> {

    Flux<PageableResponse<OrderResponse>> getAllModels(PageableBody pageableBody, OrderType orderType);

    Mono<OrderResponse> payOrder(Long id, PriceDto priceDto);

    Flux<PageableResponse<OrderResponse>> getModelsByUser(Long userId, PageableBody pageableBody, OrderType orderType);

    Mono<OrderResponse> createOrder(OrderBody body);

    // training service order with trainings

    Mono<ResponseWithChildList<OrderResponse,
            ResponseWithChildList<TrainingResponse, ResponseWithUserDto<ExerciseResponse>>>>
    getOrderWithTrainings(Long id);

}
