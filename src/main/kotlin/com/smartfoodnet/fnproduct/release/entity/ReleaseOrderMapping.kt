package com.smartfoodnet.fnproduct.release.entity

import com.smartfoodnet.common.entity.BaseEntity
import com.smartfoodnet.fnproduct.order.entity.CollectedOrder
import org.hibernate.annotations.Where
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "release_order_mapping")
@Where(clause = "deleted_at is NULL")
class ReleaseOrderMapping(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "release_info_id", columnDefinition = "BIGINT UNSIGNED")
    var releaseInfo: ReleaseInfo? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collected_order_id", columnDefinition = "BIGINT UNSIGNED")
    var collectedOrder: CollectedOrder
) : BaseEntity() {
    fun delete() {
        deletedAt = LocalDateTime.now()
    }
}
