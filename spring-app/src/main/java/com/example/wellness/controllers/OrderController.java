package com.example.wellness.controllers;


import com.example.wellness.controllers.generics.ManyToOneUserController;
import com.example.wellness.dto.common.PageableBody;
import com.example.wellness.dto.common.response.PageableResponse;
import com.example.wellness.dto.common.response.ResponseWithUserDtoEntity;
import com.example.wellness.dto.order.OrderBody;
import com.example.wellness.dto.order.OrderResponse;
import com.example.wellness.dto.order.PriceDto;
import com.example.wellness.enums.OrderType;
import com.example.wellness.hateos.CustomEntityModel;
import com.example.wellness.hateos.linking.impl.responses.OrderReactiveResponseBuilder;
import com.example.wellness.mappers.OrderMapper;
import com.example.wellness.models.Order;
import com.example.wellness.repositories.OrderRepository;
import com.example.wellness.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController implements ManyToOneUserController<Order, OrderBody, OrderResponse,
        OrderRepository, OrderMapper, OrderService> {

    private final OrderService orderService;
    private final OrderReactiveResponseBuilder orderReactiveResponseBuilder;

    @Override
    @DeleteMapping(value = "/delete/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<OrderResponse>>> deleteModel(@PathVariable Long id) {
        return orderService.deleteModel(id)
                .flatMap(o -> orderReactiveResponseBuilder.toModel(o, OrderController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<OrderResponse>>> getModelById(@PathVariable Long id) {
        return orderService.getModelById(id)
                .flatMap(o -> orderReactiveResponseBuilder.toModel(o, OrderController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @GetMapping(value = "/withUser/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<ResponseWithUserDtoEntity<OrderResponse>>> getModelByIdWithUser(@PathVariable Long id) {
        return orderService.getModelByIdWithUser(id)
                .flatMap(o -> orderReactiveResponseBuilder.toModelWithUser(o, OrderController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @PutMapping(value = "/update/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<OrderResponse>>> updateModel(@Valid @RequestBody OrderBody orderBody,
                                                                              @PathVariable Long id) {
        return orderService.updateModel(id, orderBody)
                .flatMap(o -> orderReactiveResponseBuilder.toModel(o, OrderController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @GetMapping(value = "/byIds", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Flux<PageableResponse<CustomEntityModel<OrderResponse>>> getModelsByIdIn(@Valid @RequestBody PageableBody pageableBody,
                                                                                    @RequestParam List<Long> ids) {
        return orderService.getModelsByIdIn(ids, pageableBody)
                .flatMap(m -> orderReactiveResponseBuilder.toModelPageable(m, OrderController.class));
    }

    @GetMapping(value = "/types", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<List<OrderType>>> getOrderTypes() {
        return Mono.just(ResponseEntity.ok(List.of(OrderType.ALL, OrderType.PAYED, OrderType.NOT_PAYED)));
    }

    @PatchMapping(value = "/admin", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public Flux<PageableResponse<CustomEntityModel<OrderResponse>>> getAllOrdersAdmin(@Valid @RequestBody PageableBody pageableBody,
                                                                                      @RequestParam(required = false) OrderType orderType) {
        return orderService.getAllModels(pageableBody, orderType)
                .flatMap(m -> orderReactiveResponseBuilder.toModelPageable(m, OrderController.class));
    }

    @PatchMapping(value = "/pay/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<OrderResponse>>> payOrder(@PathVariable Long id, @Valid @RequestBody PriceDto priceDto) {
        return orderService.payOrder(id, priceDto)
                .flatMap(o -> orderReactiveResponseBuilder.toModel(o, OrderController.class))
                .map(ResponseEntity::ok);
    }

    @PatchMapping(value = "/user/{userId}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Flux<PageableResponse<CustomEntityModel<OrderResponse>>> getOrdersByUser(
            @PathVariable Long userId,
            @Valid @RequestBody PageableBody pageableBody,
            @RequestParam(required = false) OrderType orderType
    ) {
        return orderService.getModelsByUser(userId, pageableBody, orderType)
                .flatMap(m -> orderReactiveResponseBuilder.toModelPageable(m, OrderController.class));
    }

    @PostMapping(produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<OrderResponse>>> createOrder(@Valid @RequestBody OrderBody orderBody) {
        return orderService.createOrder(orderBody)
                .flatMap(o -> orderReactiveResponseBuilder.toModel(o, OrderController.class))
                .map(ResponseEntity::ok);
    }


}
