package com.example.wellness.services.impl;

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
import com.example.wellness.exceptions.action.IllegalActionException;
import com.example.wellness.exceptions.action.SubEntityNotOwner;
import com.example.wellness.mappers.OrderMapper;
import com.example.wellness.mappers.UserMapper;
import com.example.wellness.models.Order;
import com.example.wellness.repositories.OrderRepository;
import com.example.wellness.repositories.UserRepository;
import com.example.wellness.services.OrderService;
import com.example.wellness.services.TrainingService;
import com.example.wellness.services.impl.generics.ManyToOneUserServiceImpl;
import com.example.wellness.utils.PageableUtilsCustom;
import com.example.wellness.utils.UserUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.util.List;

@Service
public class OrderServiceImpl extends ManyToOneUserServiceImpl<Order, OrderBody, OrderResponse, OrderRepository, OrderMapper>

        implements OrderService {

    private final TrainingService trainingService;

    public OrderServiceImpl(OrderRepository modelRepository, OrderMapper modelMapper, PageableUtilsCustom pageableUtils, UserUtils userUtils, UserRepository userRepository, UserMapper userMapper, TrainingService trainingService) {
        super(modelRepository, modelMapper, pageableUtils, userUtils, userRepository, userMapper, "order", List.of("id", "userId", "createdAt"));
        this.trainingService = trainingService;
    }

    @Override
    public Flux<PageableResponse<OrderResponse>> getAllModels(PageableBody pageableBody, OrderType orderType) {
        return pageableUtils.isSortingCriteriaValid(pageableBody.getSortingCriteria(), allowedSortingFields)
                .then(pageableUtils.createPageRequest(pageableBody))
                .map(pr -> {
                    if (orderType == null || orderType == OrderType.ALL) {
                        return Tuples.of(pr, modelRepository.findAllBy(pr), modelRepository.count());
                    } else {
                        boolean payed = orderType == OrderType.PAYED;
                        return Tuples.of(pr, modelRepository.findAllByPayed(payed, pr), modelRepository.countAllByPayed(payed));
                    }
                })
                .flatMapMany(tuple -> pageableUtils.createPageableResponse(
                        tuple.getT2().map(modelMapper::fromModelToResponse),
                        tuple.getT3(),
                        tuple.getT1()));

    }

    @Override
    public Mono<OrderResponse> payOrder(Long id, PriceDto priceDto) {
        return getModel(id)
                .flatMap(order -> {
                    if (order.isPayed()) {
                        return Mono.error(new IllegalActionException("Order with id " + id + " is already payed!"));
                    }
                    return userUtils.getPrincipal()
                            .flatMap(authUser -> {
                                if (!order.getUserId().equals(authUser.getId())) {
                                    return Mono.error(new SubEntityNotOwner(authUser.getId(), order.getUserId(), order.getId()));
                                }
                                return trainingService.getTotalPriceById(order.getTrainings())
                                        .flatMap(orderTotal -> {
                                            if (!orderTotal.equals(priceDto.getPrice())) {
                                                return Mono.error(new IllegalActionException("Expected " + orderTotal + " ,but got " + priceDto.getPrice()));
                                            }
                                            order.setPayed(true);
                                            return modelRepository.save(order)
                                                    .map(modelMapper::fromModelToResponse);
                                        });
                            });
                });
    }

    @Override
    public Flux<PageableResponse<OrderResponse>> getModelsByUser(Long userId, PageableBody pageableBody, OrderType orderType) {
        return pageableUtils.isSortingCriteriaValid(pageableBody.getSortingCriteria(), allowedSortingFields)
                .then(userUtils.getPrincipal())
                .flatMap(authUser -> privateRoute(true, authUser, userId))
                .then(pageableUtils.createPageRequest(pageableBody))
                .map(pr -> {
                    if (orderType == null || orderType == OrderType.ALL) {
                        return Tuples.of(pr, modelRepository.findAllByUserId(userId, pr), modelRepository.countAllByUserId(userId));
                    } else {
                        boolean payed = orderType == OrderType.PAYED;
                        return Tuples.of(pr, modelRepository.findAllByUserIdAndPayed(userId, payed, pr), modelRepository.countAllByUserIdAndPayed(userId, payed));
                    }
                }).flatMapMany(tuple -> pageableUtils.createPageableResponse(
                        tuple.getT2().map(modelMapper::fromModelToResponse),
                        tuple.getT3(),
                        tuple.getT1()));
    }

    @Override
    public Mono<OrderResponse> createOrder(OrderBody body) {
        return trainingService.validIds(body.getTrainings())
                .then(userUtils.getPrincipal()
                        .flatMap(authUser -> {
                            Order order = modelMapper.fromBodyToModel(body);
                            order.setUserId(authUser.getId());
                            order.setPayed(false);
                            return modelRepository.save(order).map(modelMapper::fromModelToResponse);
                        }));
    }

    @Override
    public Mono<ResponseWithChildList<OrderResponse, ResponseWithChildList<TrainingResponse, ResponseWithUserDto<ExerciseResponse>>>>
    getOrderWithTrainings(Long id) {
        return getModel(id)
                .flatMap(order -> trainingService.getTrainingsWithExercises(order.getTrainings(), true)
                        .collectList()
                        .map(trainings -> new ResponseWithChildList<>(modelMapper.fromModelToResponse(order), trainings)));
    }
}
