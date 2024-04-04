package com.example.wellness.utils;


import com.example.wellness.dto.common.response.PageInfo;
import com.example.wellness.dto.common.PageableBody;
import com.example.wellness.dto.common.response.PageableResponse;
import com.example.wellness.exceptions.common.SortingCriteriaException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PageableUtilsCustom {

    public Mono<Void> isSortingCriteriaValid(Map<String, String> sortingCriteria, List<String> allowedFields) {
        assert allowedFields != null;

        if (sortingCriteria == null) {
            return Mono.empty();
        }

        final Set<String> allowedValues = Set.of("asc", "desc");
        Map<String, String> invalidCriteria = new HashMap<>();

        sortingCriteria.forEach((key, value) -> {
            if (allowedFields.contains(key) && !allowedValues.contains(value.toLowerCase())) {
                invalidCriteria.put(key, value);
            }
        });

        if (!invalidCriteria.isEmpty()) {
            return Mono.error(new SortingCriteriaException("Invalid sorting criteria provided.", invalidCriteria));
        }
        return Mono.empty();
    }

    public Mono<Sort> createSortFromMap(Map<String, String> sortCriteria) {
        return Mono.just(Sort.by(
                sortCriteria.entrySet().stream().filter(
                        entry -> entry.getValue().equals("asc") || entry.getValue().equals("desc")
                ).map(
                        entry -> new Sort.Order(
                                entry.getValue().equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                                entry.getKey()
                        )
                ).collect(Collectors.toList())

        )).log();


    }

    public Mono<PageRequest> createPageRequest(PageableBody pageableBody) {
        return createSortFromMap(pageableBody.getSortingCriteria())
                .map(sort -> PageRequest.of(
                        pageableBody.getPage(),
                        pageableBody.getSize(),
                        sort
                ));
    }

    public <T> Flux<PageableResponse<T>> createPageableResponse(Flux<T> content, Mono<Long> count, Pageable pageable) {


        return count.flatMapMany(totalElements -> {
            PageInfo pi = PageInfo.builder()
                    .currentPage(pageable.getPageNumber())
                    .pageSize(pageable.getPageSize())
                    .totalElements(totalElements)
                    .totalPages((int) Math.ceil((double) totalElements / pageable.getPageSize()))
                    .build();
            return content.map(c -> PageableResponse.<T>builder()
                    .pageInfo(pi)
                    .content(c)
                    .build());
        });

    }

    public <T> Mono<PageInfo> createPageInfo(Page<T> page) {
        return Mono.just(PageInfo.builder()
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build());
    }
}
