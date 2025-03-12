package com.yalice.wardrobe_social_app.services.helpers;

import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Base service class that provides common functionality for all services.
 * This includes converting entities to response DTOs.
 */
public abstract class BaseService<T, ID> {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected ValidationService validationService;

    @Autowired
    protected ResponseMapperService responseMapper;

    protected abstract JpaRepository<T, ID> getRepository();

    protected abstract String getEntityName();

    @Transactional(readOnly = true)
    public T findById(ID id) {
        validationService.validateNotNull(id, "ID");
        return getRepository().findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("%s not found with ID: %s", getEntityName(), id)));
    }

    @Transactional(readOnly = true)
    public List<T> findAll() {
        return getRepository().findAll();
    }

    @Transactional(readOnly = true)
    public Page<T> findAll(Pageable pageable) {
        return getRepository().findAll(pageable);
    }

    @Transactional
    public T save(T entity) {
        validationService.validateNotNull(entity, getEntityName());
        return getRepository().save(entity);
    }

    @Transactional
    public void delete(ID id) {
        validationService.validateNotNull(id, "ID");
        if (getRepository().existsById(id)) {
            getRepository().deleteById(id);
        } else {
            throw new ResourceNotFoundException(
                    String.format("%s not found with ID: %s", getEntityName(), id));
        }
    }

    @Transactional(readOnly = true)
    public boolean exists(ID id) {
        validationService.validateNotNull(id, "ID");
        return getRepository().existsById(id);
    }

    protected <R> List<R> mapEntityList(List<T> entities, Function<T, R> mapper) {
        return responseMapper.mapList(entities, mapper);
    }

    protected <R> R mapEntity(T entity, Function<T, R> mapper) {
        return responseMapper.mapOrNull(entity, mapper);
    }

    protected <R> Page<R> mapPage(Page<T> page, Function<T, R> mapper) {
        return page.map(mapper);
    }

    protected T getEntityOrThrow(Optional<T> optional, String message) {
        return optional.orElseThrow(() -> new ResourceNotFoundException(message));
    }
}
