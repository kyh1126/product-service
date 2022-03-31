package com.smartfoodnet.fnproduct.release.entity

import com.smartfoodnet.common.entity.BaseEntity
import org.hibernate.annotations.Where
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "release_product")
@Where(clause = "deleted_at is NULL")
class ReleaseProduct(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "release_info_id", columnDefinition = "BIGINT UNSIGNED")
    var releaseInfo: ReleaseInfo? = null,

    @Column(name = "release_item_id")
    var releaseItemId: Long? = null,

    @Column(name = "basic_product_id", columnDefinition = "BIGINT UNSIGNED")
    var basicProductId: Long,

    @Column(name = "quantity")
    var quantity: Int,
) : BaseEntity() {
    fun update(quantity: Int) {
        this.quantity = quantity
    }

    fun delete() {
        deletedAt = LocalDateTime.now()
    }
}
