package com.smartfoodnet.fnproduct.product.entity

import com.smartfoodnet.common.entity.BaseEntity
import com.smartfoodnet.fnproduct.product.model.request.BasicProductCreateModel
import com.smartfoodnet.fnproduct.product.model.request.BasicProductSimpleCreateModel
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductTypeConverter
import com.smartfoodnet.fnproduct.product.model.vo.HandlingTemperatureType
import com.smartfoodnet.fnproduct.product.model.vo.HandlingTemperatureTypeConverter
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
    var warehouse: Warehouse? = null,

    @Column(name = "supply_price")
    var supplyPrice: Int? = null,

    @Column(name = "single_packaging_yn")
    var singlePackagingYn: String = "N",

    @Column(name = "expiration_date_management_yn")
    var expirationDateManagementYn: String = "N",

    @Embedded
    var expirationDateInfo: ExpirationDateInfo? = null,

    @Column(name = "pieces_per_box")
    var piecesPerBox: Int? = null,

    @Column(name = "boxes_per_palette")
    var boxesPerPalette: Int? = null,

    @Column(name = "image_url")
    var imageUrl: String? = null,

    @OneToMany(mappedBy = "basicProduct", cascade = [CascadeType.PERSIST])
    var subsidiaryMaterialMappings: MutableSet<SubsidiaryMaterialMapping> = LinkedHashSet(),

    @OneToMany(mappedBy = "packageProduct", cascade = [CascadeType.PERSIST])
    var packageProductMappings: MutableSet<PackageProductMapping> = LinkedHashSet(),

    @Column(name = "active_yn")
    var activeYn: String = "N",
) : BaseEntity() {
    fun addSubsidiaryMaterialMappings(subsidiaryMaterialMapping: SubsidiaryMaterialMapping) {
        subsidiaryMaterialMappings.add(subsidiaryMaterialMapping)
        subsidiaryMaterialMapping.basicProduct = this
    }

    fun addPackageProductMappings(packageProductMapping: PackageProductMapping) {
        packageProductMappings.add(packageProductMapping)
        packageProductMapping.packageProduct = this
    }

    fun update(
        request: BasicProductCreateModel,
        basicProductCategoryRequest: BasicProductCategory?,
        subsidiaryMaterialCategoryRequest: SubsidiaryMaterialCategory?,
        subsidiaryMaterialMappingRequests: Set<SubsidiaryMaterialMapping>,
        warehouseRequest: Warehouse,
    ) {
        name = request.name
        supplyPrice = request.supplyPrice
        singlePackagingYn = request.singlePackagingYn
        expirationDateManagementYn = request.expirationDateManagementYn
        expirationDateInfo = request.expirationDateInfoModel?.toEntity()
        piecesPerBox = request.piecesPerBox
        boxesPerPalette = request.boxesPerPalette
        imageUrl = request.imageUrl

        // 단방향
        basicProductCategory = basicProductCategoryRequest
        subsidiaryMaterialCategory = subsidiaryMaterialCategoryRequest
        warehouse = warehouseRequest

        subsidiaryMaterialMappings.clear()
        subsidiaryMaterialMappingRequests.forEach { addSubsidiaryMaterialMappings(it) }
    }

    fun update(
        request: BasicProductSimpleCreateModel,
        packageProductMappingRequests: Set<PackageProductMapping>,
    ) {
        name = request.name

        // 양방향
        packageProductMappings.clear()
        packageProductMappingRequests.forEach { addPackageProductMappings(it) }
    }

    fun inactivate() {
        activeYn = "N"
    }
}
