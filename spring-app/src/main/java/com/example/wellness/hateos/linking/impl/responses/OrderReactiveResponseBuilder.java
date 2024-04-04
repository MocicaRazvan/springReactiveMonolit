package com.example.wellness.hateos.linking.impl.responses;

import com.example.wellness.controllers.OrderController;
import com.example.wellness.dto.order.OrderResponse;
import com.example.wellness.hateos.linking.ReactiveResponseBuilder;
import com.example.wellness.hateos.linking.impl.links.OrderReactiveLinkBuilder;
import com.example.wellness.hateos.user.UserDtoAssembler;
import org.springframework.stereotype.Component;

@Component
public class OrderReactiveResponseBuilder extends ReactiveResponseBuilder<OrderResponse, OrderController> {
    public OrderReactiveResponseBuilder(UserDtoAssembler userDtoAssembler) {
        super(userDtoAssembler, new OrderReactiveLinkBuilder());
    }
}
