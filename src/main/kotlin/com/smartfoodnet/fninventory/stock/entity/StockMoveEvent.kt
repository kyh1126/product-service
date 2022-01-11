package com.smartfoodnet.fninventory.stock.entity

import com.smartfoodnet.common.entity.BaseEntity
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import org.hibernate.annotations.Where
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "stock_move_event")
@Where(clause = "deleted_at is NULL")
class StockMoveEvent(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    var id: Long? = null,

    @Column(name = "partner_id", columnDefinition = "BIGINT UNSIGNED")
    var partnerId: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "basic_product_id", columnDefinition = "BIGINT UNSIGNED", foreignKey = ForeignKey(name = "fk_stock_by_best_before__basic_product"))
    var basicProduct: BasicProduct,

    @Column(name = "shipping_product_id", columnDefinition = "BIGINT UNSIGNED")
    var shippingProductId: Long,

    @Column(name = "move_quantity")
    var moveQuantity: Int? = null,

    @Column(name = "move_event_type")
    @Enumerated(EnumType.STRING)
    var moveEventType: MoveEventType? = null,

    @Column(name = "total_stock_count")
    var totalStockCount: Int? = null,

    @Column(name = "available_stock_count")
    var availableStockCount: Int? = null,

    @Column(name = "processed_at")
    var processedAt: LocalDateTime

) : BaseEntity() {

}

enum class MoveEventType {
    INBOUND,
    OUTBOUND
}

