package com.smartfoodnet.fninventory.stock.entity

import com.smartfoodnet.common.entity.BaseEntity
import com.smartfoodnet.fnproduct.product.entity.*
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductTypeConverter
import com.smartfoodnet.fnproduct.product.model.vo.HandlingTemperatureType
import com.smartfoodnet.fnproduct.product.model.vo.HandlingTemperatureTypeConverter
import com.smartfoodnet.fnproduct.warehouse.entity.InWarehouse
import org.hibernate.annotations.Where
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "stock_by_best_before")
@Where(clause = "deleted_at is NULL")
class StockByBestBefore(
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

    @Column(name = "best_before")
    var bestBefore: Int? = null,

    @Column(name = "manufacture_date")
    var manufactureDate: LocalDateTime? = null,

    @Column(name = "expiration_date")
    var expirationDate: LocalDateTime? = null,

    @Column(name = "total_stock_count")
    var totalStockCount: Int? = null,

    @Column(name = "available_stock_count")
    var availableStockCount: Int? = null,

    @Column(name = "collected_date")
    var collectedDate: LocalDate

) : BaseEntity() {

}


