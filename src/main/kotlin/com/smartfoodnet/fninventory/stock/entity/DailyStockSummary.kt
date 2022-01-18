package com.smartfoodnet.fninventory.stock.entity

import com.smartfoodnet.common.entity.BaseEntity
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import org.hibernate.annotations.Where
import java.time.LocalDate
import javax.persistence.*
import kotlin.math.abs

@Entity
@Table(name = "daily_stock_summary")
@Where(clause = "deleted_at is NULL")
class DailyStockSummary(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    var id: Long? = null,

    @Column(name = "partner_id", columnDefinition = "BIGINT UNSIGNED")
    var partnerId: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "basic_product_id",
        columnDefinition = "BIGINT UNSIGNED",
        foreignKey = ForeignKey(name = "fk_stock_by_best_before__basic_product")
    )
    var basicProduct: BasicProduct,

    @Column(name = "shipping_product_id", columnDefinition = "BIGINT UNSIGNED")
    var shippingProductId: Long,

    /**
     * 입고 수량
     */
    @Column(name = "inbound_quantity")
    var inboundQuantity: Int = 0,
    /**
     * 출고 수량
     */
    @Column(name = "outbound_quantity")
    var outboundQuantity: Int = 0,
    /**
     * 반품 입고 수량
     */
    @Column(name = "return_quantity")
    var returnQuantity: Int = 0,
    /**
     * 반출 수량
     */
    @Column(name = "out_quantity")
    var outQuantity: Int = 0,
    /**
     * 회송 수량
     */
    @Column(name = "return_back_quantity")
    var returnBackQuantity: Int = 0,
    /**
     * 불량양품 수량
     */
    @Column(name = "return_receive_quantity")
    var returnReceiveQuantity: Int = 0,
    /**
     * 창고 내 입고 수량
     */
    @Column(name = "rollback_receive_quantity")
    var rollbackReceiveQuantity: Int = 0,
    /**
     * 재고조정 입고 수량
     */
    @Column(name = "adjust_in_quantity")
    var adjustInQuantity: Int = 0,
    /**
     * 재고조정 반출 수량
     */
    @Column(name = "adjust_out_quantity")
    var adjustOutQuantity: Int = 0,

    @Column(name = "total_stock_count")
    var totalStockCount: Int = 0,

    @Column(name = "available_stock_count")
    var availableStockCount: Int = 0,

    @Column(name = "total_stock_change_count")
    var totalStockChangeCount: Int = 0,

    @Column(name = "effective_date")
    var effectiveDate: LocalDate

) : BaseEntity() {

    fun calculateTotalStockChange() {
        val positiveChanges = abs(inboundQuantity) + abs(returnQuantity) + abs(rollbackReceiveQuantity) + abs(adjustInQuantity)

        val negativeChanges = abs(outboundQuantity) + abs(outQuantity) + abs(adjustOutQuantity)

        totalStockChangeCount = positiveChanges - negativeChanges
    }
}
