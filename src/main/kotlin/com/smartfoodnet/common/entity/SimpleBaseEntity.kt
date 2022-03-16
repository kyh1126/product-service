package com.smartfoodnet.common.entity

import io.swagger.annotations.ApiModelProperty
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class SimpleBaseEntity {
    @CreationTimestamp
    @Column(name = "created_at")
    @ApiModelProperty(required = false, hidden = true)
    var createdAt: LocalDateTime? = null

    @UpdateTimestamp
    @Column(name = "updated_at")
    @ApiModelProperty(required = false, hidden = true)
    var updatedAt: LocalDateTime? = null
}
