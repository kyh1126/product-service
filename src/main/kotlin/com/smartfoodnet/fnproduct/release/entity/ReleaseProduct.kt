package com.smartfoodnet.fnproduct.release.entity

import com.smartfoodnet.common.entity.SimpleBaseEntity
import javax.persistence.*

@Entity
@Table(name = "release_product")
class ReleaseProduct(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "release_info_id", columnDefinition = "BIGINT UNSIGNED")
    var releaseInfo: ReleaseInfo,

    @Column(name = "basic_product_id", columnDefinition = "BIGINT UNSIGNED")
    var basicProductId: Long,

    @Column(name = "quantity")
    var quantity: Int
) : SimpleBaseEntity()
