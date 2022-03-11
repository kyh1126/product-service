package com.smartfoodnet.fnproduct.release.entity

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
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
    var releaseInfo: ReleaseInfo? = null,

    @Column(name = "basic_product_id", columnDefinition = "BIGINT UNSIGNED")
    var basicProductId: Long? = null,

    @Column(name = "quantity")
    var quantity: Int? = null,

    @CreationTimestamp
    @Column(name = "created_at")
    var createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null
)
