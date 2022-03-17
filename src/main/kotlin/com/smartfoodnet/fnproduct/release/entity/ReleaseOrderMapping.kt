package com.smartfoodnet.fnproduct.release.entity

import com.smartfoodnet.common.entity.SimpleBaseEntity
import com.smartfoodnet.fnproduct.order.entity.CollectedOrder
import javax.persistence.*

@Entity
@Table(name = "release_order_mapping")
class ReleaseOrderMapping(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "release_info_id", columnDefinition = "BIGINT UNSIGNED")
    var releaseInfo: ReleaseInfo,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collected_order_id", columnDefinition = "BIGINT UNSIGNED")
    var collectedOrder: CollectedOrder
) : SimpleBaseEntity()
