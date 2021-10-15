package com.smartfoodnet.fnproduct.product.entity

import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductTypeConverter
import com.smartfoodnet.fnproduct.product.model.vo.HandlingTemperatureType
import com.smartfoodnet.fnproduct.product.model.vo.HandlingTemperatureTypeConverter
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "basic_product")
class BasicProduct(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    var id: Long? = null,

    @Column(name = "type")
    @Convert(converter = BasicProductTypeConverter::class)
    var type: BasicProductType,

    @Column(name = "partner_id", columnDefinition = "BIGINT UNSIGNED")
    var partnerId: Long? = null,

    @Column(name = "name")
    var name: String? = null,

    @Column(name = "code")
    var code: String? = null,

    @Column(name = "barcode_yn")
    var barcodeYn: String = "N",

    @Column(name = "barcode")
    var barcode: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "basic_product_category_id", columnDefinition = "BIGINT UNSIGNED")
    var basicProductCategory: BasicProductCategory? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subsidiary_material_category_id", columnDefinition = "BIGINT UNSIGNED")
    var subsidiaryMaterialCategory: SubsidiaryMaterialCategory? = null,

    @Column(name = "handling_temperature")
    @Convert(converter = HandlingTemperatureTypeConverter::class)
    var handlingTemperature: HandlingTemperatureType? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", columnDefinition = "BIGINT UNSIGNED")
    var warehouse: Warehouse,

    @Column(name = "supply_price")
    var supplyPrice: Int? = null,

    @Column(name = "single_packaging_yn")
    var singlePackagingYn: String = "N",

    @Column(name = "expiration_date_management_yn")
    var expirationDateManagementYn: String = "N",

    @Column(name = "pieces_per_box")
    var piecesPerBox: Int? = null,

    @Column(name = "boxes_per_palette")
    var boxesPerPalette: Int? = null,

    @Column(name = "image_url")
    var imageUrl: String? = null,

    @OneToOne(mappedBy = "basicProduct", cascade = [CascadeType.PERSIST])
    var expirationDateInfo: ExpirationDateInfo? = null,

    @OneToMany(mappedBy = "basicProduct", cascade = [CascadeType.PERSIST])
    var subsidiaryMaterials: MutableList<SubsidiaryMaterial> = mutableListOf(),

    @Column(name = "active_yn")
    var activeYn: String = "N",

    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null,

    @CreationTimestamp
    @Column(name = "created_at")
    var createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,
) {
    fun addExpirationDateInfo(expirationDateInfo: ExpirationDateInfo) {
        this.expirationDateInfo = expirationDateInfo
        expirationDateInfo.basicProduct = this
    }

    fun addSubsidiaryMaterials(subsidiaryMaterial: SubsidiaryMaterial) {
        subsidiaryMaterials.add(subsidiaryMaterial)
        subsidiaryMaterial.basicProduct = this
    }
}
