package org.fin.walley.domain.common;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Базовый класс с полями аудита для всех JPA-сущностей.
 * Поля createdAt и updatedAt заполняются автоматически
 * через JPA-хуки @PrePersist и @PreUpdate.
 */

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractAuditableEntity {
    @Column(name = "ceratedAt",  nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt",  nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected  void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
