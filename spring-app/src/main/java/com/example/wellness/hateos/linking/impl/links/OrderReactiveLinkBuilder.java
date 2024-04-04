package com.example.wellness.hateos.linking.impl.links;

import com.example.wellness.controllers.OrderController;
import com.example.wellness.dto.order.OrderBody;
import com.example.wellness.dto.order.OrderResponse;
import com.example.wellness.dto.order.PriceDto;
import com.example.wellness.enums.OrderType;
import com.example.wellness.hateos.linking.generics.ManyToOneUserReactiveLinkBuilder;
import com.example.wellness.mappers.OrderMapper;
import com.example.wellness.models.Order;
import com.example.wellness.repositories.OrderRepository;
import com.example.wellness.services.OrderService;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;

import java.util.List;

public class OrderReactiveLinkBuilder extends ManyToOneUserReactiveLinkBuilder<Order, OrderBody, OrderResponse, OrderRepository, OrderMapper, OrderService, OrderController> {

    @Override
    public List<WebFluxLinkBuilder.WebFluxLink> createModelLinks(OrderResponse orderResponse, Class<OrderController> c) {
        List<WebFluxLinkBuilder.WebFluxLink> links = super.createModelLinks(orderResponse, c);
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).getOrderTypes()).withRel("orderTypes"));
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).getAllOrdersAdmin(null, OrderType.ALL)).withRel("getAllOrdersAdmin"));
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).payOrder(orderResponse.getId(), new PriceDto(10))).withRel("payOrder"));
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).getOrdersByUser(orderResponse.getUserId(), null, OrderType.ALL)).withRel("getOrdersByUser"));
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).createOrder(new OrderBody())).withRel("createOrder"));
        return links;
    }
}
