package com.example.wellness.advices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseAdvice {


    protected Map<String, Object> respMapWithMessage(HttpStatus status, Exception error, ServerWebExchange exchange) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("message", error.getMessage());
        resp.put("timestamp", Instant.now().toString());
        resp.put("error", status.getReasonPhrase());
        resp.put("status", status.value());
        resp.put("path", exchange.getRequest().getPath().value());
        return resp;
    }

    public Mono<ResponseEntity<Map<String, Object>>> handleWithMessage(HttpStatus status, Exception exception, ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.status(status).body(respMapWithMessage(status, exception, exchange)));
    }
}
