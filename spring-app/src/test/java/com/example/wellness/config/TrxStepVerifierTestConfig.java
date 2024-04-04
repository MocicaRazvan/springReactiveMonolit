package com.example.wellness.config;


import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.ReactiveTransactionManager;

@TestConfiguration
public class TrxStepVerifierTestConfig {

    @Bean
    public TrxStepVerifier trxStepVerifier(ReactiveTransactionManager transactionManager) {
        return new TrxStepVerifier(transactionManager);
    }
}
