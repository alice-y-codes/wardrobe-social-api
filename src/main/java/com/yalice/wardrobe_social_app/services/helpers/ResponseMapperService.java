package com.yalice.wardrobe_social_app.services.helpers;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ResponseMapperService {

    public <T, R> List<R> mapList(List<T> entities, Function<T, R> mapper) {
        return entities.stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

    public <T, R> R mapOrNull(T entity, Function<T, R> mapper) {
        return entity != null ? mapper.apply(entity) : null;
    }

    public <T, R> List<R> mapListSafely(List<T> entities, Function<T, R> mapper) {
        return entities != null ? mapList(entities, mapper) : List.of();
    }
}