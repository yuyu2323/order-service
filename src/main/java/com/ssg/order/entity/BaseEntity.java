package com.ssg.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Column(name = "reg_dtm", nullable = false)
    private LocalDateTime regDtm;

    @Column(name = "mod_dtm", nullable = false)
    private LocalDateTime modDtm;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.regDtm = now;
        this.modDtm = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.modDtm = LocalDateTime.now();
    }
}
