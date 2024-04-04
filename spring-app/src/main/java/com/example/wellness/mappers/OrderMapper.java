package com.example.wellness.mappers;


import com.example.wellness.dto.order.OrderBody;
import com.example.wellness.dto.order.OrderResponse;
import com.example.wellness.mappers.template.DtoMapper;
import com.example.wellness.models.Order;
import com.example.wellness.utils.EntitiesUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class OrderMapper extends DtoMapper<Order, OrderBody, OrderResponse> {

    @Autowired
    protected EntitiesUtils entitiesUtils;


//    public abstract Order fromBodyToModel(OrderBody body);
//
//    public abstract OrderResponse fromModelToResponse(Order order);

    @Override
    public Mono<Order> updateModelFromBody(OrderBody body, Order order) {

        return entitiesUtils.verifyMappingTrainings(body.getTrainings())
                .then(Mono.fromCallable(
                        () -> {
                            order.setPayed(body.isPayed());
                            order.setShippingAddress(body.getShippingAddress());
                            order.setTrainings(
                                    body.getTrainings()
                                            .stream().distinct().toList()
                            );
                            return order;
                        }
                ));
    }

}
