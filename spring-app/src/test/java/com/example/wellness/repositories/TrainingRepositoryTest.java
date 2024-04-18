package com.example.wellness.repositories;

import com.example.wellness.config.TrxStepVerifier;
import com.example.wellness.config.TrxStepVerifierTestConfig;
import com.example.wellness.enums.Role;
import com.example.wellness.models.Exercise;
import com.example.wellness.models.Order;
import com.example.wellness.models.Training;
import com.example.wellness.models.user.UserCustom;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataR2dbcTest
@ActiveProfiles("tc")
@Import(TrxStepVerifierTestConfig.class)
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class TrainingRepositoryTest extends AbstractPostgresContainerBase {

    @Autowired
    private TrainingRepository trainingRepository;

    @Autowired
    private TrxStepVerifier trxStepVerifier;


    UserCustom admin = UserCustom.builder()
            .id(2L)
            .firstName("Jane")
            .lastName("Doe")
            .email("jane.doe@example.com")
            .password("password")
            .role(Role.ROLE_ADMIN)
            .createdAt(LocalDateTime.of(2021, 1, 1, 0, 0, 0))
            .updatedAt(LocalDateTime.of(2021, 1, 1, 0, 0, 0))
            .build();

    UserCustom trainer = UserCustom.builder()
            .id(3L)
            .firstName("Jim")
            .lastName("Doe")
            .email("jim.doe@example.com")
            .password("password")
            .role(Role.ROLE_TRAINER)
            .createdAt(LocalDateTime.of(2021, 1, 1, 0, 0, 0))
            .updatedAt(LocalDateTime.of(2021, 1, 1, 0, 0, 0))
            .build();

    Exercise exerciseAdmin1 = Exercise.builder()
            .id(1L)
            .muscleGroups(List.of("arms", "legs"))
            .approved(true)
            .body("Exercise body 1")
            .title("Exercise Title 1")
            .userId(2L)
            .createdAt(LocalDateTime.of(2021, 1, 1, 0, 0, 0))
            .updatedAt(LocalDateTime.of(2021, 1, 1, 0, 0, 0))
            .build();
    Exercise exerciseAdmin2 = Exercise.builder()
            .id(2L)
            .muscleGroups(List.of("arms", "legs"))
            .approved(true)
            .body("Exercise body 2")
            .title("Exercise Title 2")
            .userId(2L)
            .createdAt(LocalDateTime.of(2021, 1, 1, 0, 0, 0))
            .updatedAt(LocalDateTime.of(2021, 1, 1, 0, 0, 0))
            .build();

    Exercise exerciseTrainer1 = Exercise.builder()
            .id(3L)
            .muscleGroups(List.of("chest", "back"))
            .approved(true)
            .body("Exercise body 2")
            .title("Exercise Title 2")
            .userId(3L)
            .createdAt(LocalDateTime.of(2021, 1, 1, 0, 0, 0))
            .updatedAt(LocalDateTime.of(2021, 1, 1, 0, 0, 0))
            .build();

    Exercise exerciseTrainer2 = Exercise.builder().muscleGroups(List.of("chest", "legs"))
            .id(4L)
            .approved(true)
            .body("Exercise body 3")
            .title("Exercise Title 3")
            .userId(3L)
            .createdAt(LocalDateTime.of(2021, 1, 1, 0, 0, 0))
            .updatedAt(LocalDateTime.of(2021, 1, 1, 0, 0, 0))
            .build();

    Training trainingAdmin = Training.builder()
            .id(1L)
            .approved(true)
            .body("Training body 1")
            .title("Training Title 1")
            .userId(2L)
            .price(19.99)
            .exercises(List.of(exerciseAdmin1.getId()))
            .createdAt(LocalDateTime.of(2021, 1, 1, 0, 0, 0))
            .updatedAt(LocalDateTime.of(2021, 1, 1, 0, 0, 0))
            .build();

    Training trainingTrainer1 = Training.builder()
            .id(2L)
            .approved(true)
            .body("Training body 2")
            .title("Training Title 2")
            .userId(3L)
            .price(29.99)
            .exercises(List.of(2L, 3L))
            .createdAt(LocalDateTime.of(2021, 1, 1, 0, 0, 0))
            .updatedAt(LocalDateTime.of(2021, 1, 1, 0, 0, 0))
            .build();
    Training trainingTrainer2 = Training.builder()
            .id(3L)
            .approved(false)
            .body("Training body 3")
            .title("Training Title 3")
            .userId(3L)
            .price(39.99)
            .exercises(List.of(2L, 3L))
            .createdAt(LocalDateTime.of(2021, 1, 1, 0, 0, 0))
            .updatedAt(LocalDateTime.of(2021, 1, 1, 0, 0, 0))
            .build();

    Order order = Order.builder()
            .id(1L)
            .shippingAddress("123 Fake St.")
            .payed(true)
            .trainings(List.of(1L, 2L))
            .userId(1L)
            .build();


    @Test
    void count_training_1_in_orders() {
        trxStepVerifier.create(trainingRepository.countOrdersByTrainingId(1L))
                .assertNext(count -> assertEquals(2, count))
                .verifyComplete();
    }

    @Test
    void count_training_3_in_orders() {
        trxStepVerifier.create(trainingRepository.countOrdersByTrainingId(3L))
                .assertNext(count -> assertEquals(0, count))
                .verifyComplete();
    }

    @Test
    void find_all_by_approvedTrue_and_id_in() {
        trxStepVerifier.create(trainingRepository.findAllByApprovedAndIdIn(true, List.of(1L, 2L, 3L)))
                .assertNext(training -> assertEquals(trainingAdmin, training))
                .assertNext(training -> assertEquals(trainingTrainer1, training))
                .verifyComplete();
    }


    @Test
    void find_all_by_approvedFalse_and_id_in() {
        trxStepVerifier.create(trainingRepository.findAllByApprovedAndIdIn(false, List.of(1L, 2L, 3L)))
                .assertNext(training -> assertEquals(trainingTrainer2, training))
                .verifyComplete();
    }

    @Test
    void find_all_by_approvedTrue_and_title_found() {
        trxStepVerifier.create(trainingRepository.findAllByTitleContainingIgnoreCaseAndApproved("Training Title 1", true, PageRequest.of(0, 10)))
                .assertNext(training -> assertEquals(trainingAdmin, training))
                .verifyComplete();
    }

    @Test
    void find_all_by_approvedTrue_and_title_notFound() {
        trxStepVerifier.create(trainingRepository.findAllByTitleContainingIgnoreCaseAndApproved("Training Title 11", true, PageRequest.of(0, 10)))
                .verifyComplete();
    }


    @Test
    void find_all_by_approvedTrue_and_title_sortByTitleAsc() {
        trxStepVerifier.create(trainingRepository.findAllByTitleContainingIgnoreCaseAndApproved("Training Title", true, PageRequest.of(0, 10, Sort.by(Sort.Order.asc("title"))))
                )
                .assertNext(training -> assertEquals(trainingAdmin, training))
                .assertNext(training -> assertEquals(trainingTrainer1, training))
                .verifyComplete();
    }

    @Test
    void find_all_byApproved_and_titleEmpty() {
        trxStepVerifier.create(trainingRepository.findAllByTitleContainingIgnoreCaseAndApproved("", true, PageRequest.of(0, 10))
                )
                .assertNext(training -> assertEquals(trainingAdmin, training))
                .assertNext(training -> assertEquals(trainingTrainer1, training))
                .verifyComplete();
    }

    @Test
    void count_all_by_title_containing_and_approvedFalse() {
        trxStepVerifier.create(trainingRepository.countAllByTitleContainingIgnoreCaseAndApproved("Training Title", false))
                .assertNext(count -> assertEquals(1, count))
                .verifyComplete();
    }

    @Test
    void count_all_by_title_containing_and_approvedTrue() {
        trxStepVerifier.create(trainingRepository.countAllByTitleContainingIgnoreCaseAndApproved("Training Title", true))
                .assertNext(count -> assertEquals(2, count))
                .verifyComplete();
    }

    @Test
    void count_all_by_title_containing_and_approvedTrue_notFound() {
        trxStepVerifier.create(trainingRepository.countAllByTitleContainingIgnoreCaseAndApproved("Training Title 11", true))
                .assertNext(count -> assertEquals(0, count))
                .verifyComplete();
    }


    @Test
    void find_all_by_userId_and_title_containing() {
        trxStepVerifier.create(trainingRepository.findAllByUserIdAndTitleContainingIgnoreCase(3L, "Training Title", PageRequest.of(0, 10)))
                .assertNext(training -> assertEquals(trainingTrainer1, training))
                .assertNext(training -> assertEquals(trainingTrainer2, training))
                .verifyComplete();
    }

    @Test
    void count_all_by_userId_and_title_containing() {
        trxStepVerifier.create(trainingRepository.countAllByUserIdAndTitleContainingIgnoreCase(3L, "Training Title"))
                .assertNext(count -> assertEquals(2, count))
                .verifyComplete();
    }

    @Test
    void sum_price_by_ids() {
        trxStepVerifier.create(trainingRepository.sumPriceByIds(List.of(1L, 2L)))
                .assertNext(sum -> assertEquals(49.98, sum))
                .verifyComplete();
    }


    @Test
    void count_by_ids_allApproved() {
        trxStepVerifier.create(trainingRepository.countByIds(List.of(1L, 2L)))
                .assertNext(count -> assertEquals(2, count))
                .verifyComplete();
    }

    @Test
    void count_by_ids_notAllApproved() {
        trxStepVerifier.create(trainingRepository.countByIds(List.of(1L, 2L, 3L)))
                .assertNext(count -> assertEquals(2, count))
                .verifyComplete();
    }

    @Test
    void find_all_by_approved() {
        trxStepVerifier.create(trainingRepository.findAllByApproved(true,
                        PageRequest.of(0, 10)))
                .assertNext(training -> assertEquals(trainingAdmin, training))
                .assertNext(training -> assertEquals(trainingTrainer1, training))
                .verifyComplete();
    }

    @Test
    void find_all_by_approved_onePerPage() {
        trxStepVerifier.create(trainingRepository.findAllByApproved(true,
                        PageRequest.of(0, 1)))
                .assertNext(training -> assertEquals(trainingAdmin, training))
                .verifyComplete();
    }

    @Test
    void count_by_approvedTrue() {
        trxStepVerifier.create(trainingRepository.countByApproved(true))
                .assertNext(count -> assertEquals(2, count))
                .verifyComplete();
    }

    @Test
    void count_by_approvedFalse() {
        trxStepVerifier.create(trainingRepository.countByApproved(false))
                .assertNext(count -> assertEquals(1, count))
                .verifyComplete();
    }

    @Test
    void count_by_user_id_2found() {
        trxStepVerifier.create(trainingRepository.countByUserId(3L))
                .assertNext(count -> assertEquals(2, count))
                .verifyComplete();
    }

    @Test
    void count_by_user_id_0found() {
        trxStepVerifier.create(trainingRepository.countByUserId(1L))
                .assertNext(count -> assertEquals(0, count))
                .verifyComplete();
    }

    @Test
    void find_by_approvedTrue_and_id_found() {
        trxStepVerifier.create(trainingRepository.findByApprovedAndId(true, 1L))
                .assertNext(training -> assertEquals(trainingAdmin, training))
                .verifyComplete();
    }

    @Test
    void find_by_approvedTrue_and_id_notFound() {
        trxStepVerifier.create(trainingRepository.findByApprovedAndId(true, 3L))
                .verifyComplete();
    }

    @Test
    void find_by_approvedFalse_and_id_found() {
        trxStepVerifier.create(trainingRepository.findByApprovedAndId(false, 3L))
                .assertNext(training -> assertEquals(trainingTrainer2, training))
                .verifyComplete();
    }

    @Test
    void find_by_approvedFalse_and_id_notFound() {
        trxStepVerifier.create(trainingRepository.findByApprovedAndId(false, 13L))
                .verifyComplete();
    }

    @Test
    void exists_by_id_and_approvedTrue() {
        trxStepVerifier.create(trainingRepository.existsByIdAndApprovedIsTrue(1L))
                .assertNext(Assertions::assertTrue)
                .verifyComplete();
    }

    @Test
    void exists_by_id_and_approvedFalse() {
        trxStepVerifier.create(trainingRepository.existsByIdAndApprovedIsTrue(3L))
                .assertNext(Assertions::assertFalse)
                .verifyComplete();
    }

    @Test
    void find_all_by_userId() {
        trxStepVerifier.create(trainingRepository.findAllByUserId(3L, PageRequest.of(0, 10)))
                .assertNext(training -> assertEquals(trainingTrainer1, training))
                .assertNext(training -> assertEquals(trainingTrainer2, training))
                .verifyComplete();
    }

    @Test
    void find_all_by_userId_onePerPage() {
        trxStepVerifier.create(trainingRepository.findAllByUserId(3L, PageRequest.of(0, 1)))
                .assertNext(training -> assertEquals(trainingTrainer1, training))
                .verifyComplete();
    }

    @Test
    void find_all_by_userId_sortByTitleDesc() {
        trxStepVerifier.create(trainingRepository.findAllByUserId(3L, PageRequest.of(0, 10,
                        Sort.by(Sort.Order.desc("title"))))
                )
                .assertNext(training -> assertEquals(trainingTrainer2, training))
                .assertNext(training -> assertEquals(trainingTrainer1, training))
                .verifyComplete();
    }

    @Test
    void find_all_by_userId_notFound() {
        trxStepVerifier.create(trainingRepository.findAllByUserId(1L, PageRequest.of(0, 10)))
                .verifyComplete();
    }

    @Test
    void find_all_by_2PerPage_sortByTitleDesc() {
        trxStepVerifier.create(trainingRepository.findAllBy(PageRequest.of(0, 2,
                        Sort.by(Sort.Order.desc("title"))))
                )
                .assertNext(training -> assertEquals(trainingTrainer2, training))
                .assertNext(training -> assertEquals(trainingTrainer1, training))
                .verifyComplete();

    }

    @Test
    void find_all_by_2PerPage() {
        trxStepVerifier.create(trainingRepository.findAllBy(PageRequest.of(0, 2))
                )
                .assertNext(training -> assertEquals(trainingAdmin, training))
                .assertNext(training -> assertEquals(trainingTrainer1, training))
                .verifyComplete();
    }

    @Test
    void find_all_by_id_in_found() {
        trxStepVerifier.create(trainingRepository.findAllByIdIn(List.of(1L, 2L, 3L), PageRequest.of(0, 10)))
                .assertNext(training -> assertEquals(trainingAdmin, training))
                .assertNext(training -> assertEquals(trainingTrainer1, training))
                .assertNext(training -> assertEquals(trainingTrainer2, training))
                .verifyComplete();
    }

    @Test
    void find_all_by_id_in_notFound() {
        trxStepVerifier.create(trainingRepository.findAllByIdIn(List.of(13L, 14L, 15L), PageRequest.of(0, 10)))
                .verifyComplete();
    }

    @Test
    void count_all_by_id_in_found() {
        trxStepVerifier.create(trainingRepository.countAllByIdIn(List.of(1L, 2L, 13L)))
                .assertNext(count -> assertEquals(2, count))
                .verifyComplete();
    }

    @Test
    void count_all_by_id_in_notFound() {
        trxStepVerifier.create(trainingRepository.countAllByIdIn(List.of(13L, 14L, 15L)))
                .assertNext(count -> assertEquals(0, count))
                .verifyComplete();
    }


}