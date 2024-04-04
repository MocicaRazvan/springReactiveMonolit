package com.example.wellness.advices;

import com.example.wellness.exceptions.UserWithEmailExists;
import com.example.wellness.exceptions.notFound.NotFoundBase;
import com.example.wellness.exceptions.notFound.TokenNotFound;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestControllerAdvice
public class AuthAdvice extends BaseAdvice {


    @ApiResponse(responseCode = "404", description = "The user credentials are not valid", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(ref = "#/components/schemas/AuthMessage"))})
    @ExceptionHandler(UsernameNotFoundException.class)
    public Mono<ResponseEntity<Map<String, Object>>> notFound(UsernameNotFoundException error, ServerWebExchange exchange) {
        return handleWithMessage(HttpStatus.NOT_FOUND, error, exchange);

    }

    @ExceptionHandler(UserWithEmailExists.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleUserWithEmailExists(UserWithEmailExists e, ServerWebExchange exchange) {
        return handleWithMessage(HttpStatus.CONFLICT, e, exchange);
    }
}
