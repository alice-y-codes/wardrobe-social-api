package com.yalice.wardrobe_social_app.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Abstract base entity class that provides common fields for auditing purposes.
 * This class is intended to be inherited by other entity classes that require auditing
 * information such as creation and last modification timestamps.
 *
 * <p>It uses JPA annotations to automatically track the creation and modification dates.</p>
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@SuperBuilder
@NoArgsConstructor
public abstract class BaseEntity {

    /**
     * The unique identifier of the entity. This is the primary key, and it is automatically
     * generated using an identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The timestamp when the entity was created. This field is set automatically when the entity
     * is persisted and cannot be updated once set.
     */
    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    /**
     * The timestamp when the entity was last modified. This field is automatically updated
     * whenever the entity is updated.
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
