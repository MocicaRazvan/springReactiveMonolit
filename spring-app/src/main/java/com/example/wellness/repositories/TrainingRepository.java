package com.example.wellness.repositories;

import com.example.wellness.dto.training.TrainingWithOrderCount;
import com.example.wellness.models.Training;
import com.example.wellness.repositories.generic.ApprovedRepository;
import com.example.wellness.repositories.generic.CountIds;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TrainingRepository extends ApprovedRepository<Training>, CountIds {

    @Query("""
                select count(*) from order_custom o
                where :trainingId = any (o.trainings)
            """)
    Mono<Long> countOrdersByTrainingId(Long trainingId);

    Flux<Training> findAllByApprovedAndIdIn(boolean approved, List<Long> ids);


    @Query("""
             select sum(price) from training where id in (:ids)
            """)
    Mono<Double> sumPriceByIds(List<Long> ids);

    @Query(
            """
                               select count(*) from training t
                               where t.id in (:ids) and t.approved = true
                    """
    )
    Mono<Long> countByIds(List<Long> ids);

    @Query("""
                select t.*, count(o.id) as order_count
                from training t left join order_custom o on t.id = any(o.trainings)
                where t.id=:id
                group by t.id, t.title, t.body, t.approved, t.user_likes, t.user_dislikes, t.user_id, t.created_at, t.updated_at, t.images;
                
            """)
    Mono<TrainingWithOrderCount> findByIdWithOrderCount(Long id);
}
