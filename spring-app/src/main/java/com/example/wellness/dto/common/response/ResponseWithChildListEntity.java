package com.example.wellness.dto.common.response;

import com.example.wellness.hateos.CustomEntityModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.EntityModel;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ResponseWithChildListEntity<E, C> {
    private CustomEntityModel<E> entity;
    private List<C> children;
}
