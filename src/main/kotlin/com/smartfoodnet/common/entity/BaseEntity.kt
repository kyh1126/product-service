package com.smartfoodnet.common.entity

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class BaseEntity {
    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null

    @CreationTimestamp
    @Column(name = "created_at")
    var createdAt: LocalDateTime? = null

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null
}
