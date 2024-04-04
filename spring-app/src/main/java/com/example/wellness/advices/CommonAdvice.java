package com.example.wellness.advices;

import com.example.wellness.exceptions.action.IllegalActionException;
import com.example.wellness.exceptions.action.PrivateRouteException;
import com.example.wellness.exceptions.action.SubEntityNotOwner;
import com.example.wellness.exceptions.action.SubEntityUsed;
import com.example.wellness.exceptions.common.SortingCriteriaException;
import com.example.wellness.exceptions.notFound.IdNameException;
import com.example.wellness.exceptions.notFound.NotFoundBase;
import com.example.wellness.exceptions.notFound.TokenNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class CommonAdvice extends BaseAdvice {

    @ExceptionHandler(NotFoundBase.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleBaseNotFound(NotFoundBase e, ServerWebExchange exchange) {
        return handleWithMessage(HttpStatus.NOT_FOUND, e, exchange);
    }

    @ExceptionHandler(IdNameException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleIdNameNotFound(IdNameException exception, ServerWebExchange exchange) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("name", exception.getName());
        resp.put("id", exception.getId());
        resp.put("message", exception.getMessage());
        resp.putAll(respMapWithMessage(HttpStatus.NOT_FOUND, exception, exchange));
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp));
    }

    @ExceptionHandler(SubEntityUsed.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleSubEntityUsed(IdNameException exception, ServerWebExchange exchange) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("name", exception.getName());
        resp.put("id", exception.getId());
        resp.put("message", exception.getMessage());
        resp.putAll(respMapWithMessage(HttpStatus.BAD_REQUEST, exception, exchange));
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleUsernameNotFound(UsernameNotFoundException e, ServerWebExchange exchange) {
        return handleWithMessage(HttpStatus.NOT_FOUND, e, exchange);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleValidation(WebExchangeBindException e, ServerWebExchange exchange) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("reasons", e.getBindingResult().getFieldErrors().stream()
                .collect(HashMap::new, (map, fieldError) ->
                        map.put(fieldError.getField(), fieldError.getDefaultMessage()), HashMap::putAll));
        resp.putAll(respMapWithMessage(HttpStatus.BAD_REQUEST, e, exchange));
        return Mono.just(ResponseEntity.badRequest().body(resp));
    }

    @ExceptionHandler(SortingCriteriaException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleSortingCriteria(SortingCriteriaException e, ServerWebExchange exchange) {

        Map<String, Object> resp = new HashMap<>(e.getInvalidCriteria());
        resp.putAll(respMapWithMessage(HttpStatus.BAD_REQUEST, e, exchange));

        return Mono.just(ResponseEntity.badRequest().body(resp));
    }

    @ExceptionHandler({TokenNotFound.class, PrivateRouteException.class})
    public Mono<ResponseEntity<Map<String, Object>>> handleToken(RuntimeException e, ServerWebExchange exchange) {
        return handleWithMessage(HttpStatus.FORBIDDEN, e, exchange);
    }

    @ExceptionHandler(IllegalActionException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleBaseNotFound(IllegalActionException e, ServerWebExchange exchange) {
        return handleWithMessage(HttpStatus.BAD_REQUEST, e, exchange);
    }

    @ExceptionHandler(SubEntityNotOwner.class)
    public Mono<ResponseEntity<Map<String, Object>>> subEntityNotMatch(SubEntityNotOwner exception, ServerWebExchange exchange) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("expectedUserId", exception.getEntityUserId());
        resp.put("receivedUserId", exception.getAuthId());
        resp.put("entityId", exception.getEntityId());
        resp.putAll(respMapWithMessage(HttpStatus.BAD_REQUEST, exception, exchange));
        return Mono.just(ResponseEntity.badRequest().body(resp));
    }


}
