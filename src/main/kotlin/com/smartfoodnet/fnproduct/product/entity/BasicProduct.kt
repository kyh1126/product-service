package com.smartfoodnet.fnproduct.product.entity

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "basic_product")
class BasicProduct(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "partner_id")
    var partnerId: Long,

    @Column(name = "name")
    var name: String,

    @Column(name = "barcode_yn")
    var barcodeYn: String = "N",

    @Column(name = "barcode")
    var barcode: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    var category: BasicProductCategory,

    @Column(name = "handling_temperature")
    var handlingTemperature: Int? = null,

    @Column(name = "single_packaging_yn")
    var singlePackagingYn: String = "N",

    @Column(name = "warehouse_code")
    var warehouseCode: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    var supplier: Supplier? = null,

    @Column(name = "supply_price")
    var supplyPrice: Int? = null,

    @Column(name = "pieces_per_box")
    var piecesPerBox: Int? = null,

    @Column(name = "boxes_per_palette")
    var boxesPerPalette: Int? = null,

    @Column(name = "image_url")
    var imageUrl: String? = null,

    @Column(name = "expiration_date_management_yn")
    var expirationDateManagementYn: String = "N",

    @Column(name = "active_yn")
    var activeYn: String = "N",

    @Column(name = "deleted_at")
    var deletedAt: Instant? = null,

    @CreationTimestamp
    @Column(name = "created_at")
    var createdAt: Instant? = null,

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: Instant? = null,
)
