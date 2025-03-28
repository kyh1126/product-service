package com.smartfoodnet.fnproduct.product.entity

import com.smartfoodnet.common.entity.BaseEntity
import com.smartfoodnet.fnproduct.product.model.request.BasicProductCreateModel
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import com.smartfoodnet.fnproduct.product.model.vo.HandlingTemperatureType
import com.smartfoodnet.fnproduct.product.model.vo.HandlingTemperatureTypeConverter
import com.smartfoodnet.fnproduct.warehouse.entity.InWarehouse
import javax.persistence.*

@Entity
@Table(name = "basic_product")
class BasicProduct(
    @Column(name = "partner_id")
    var partnerId: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    val type: BasicProductType,

    @Column(name = "barcode_yn", nullable = false)
    val barcodeYn: String,

    @Column(name = "code")
    var code: String? = null,

    @Column(name = "barcode")
    var barcode: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "basic_product_category_id")
    var basicProductCategory: BasicProductCategory? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subsidiary_material_category_id")
    var subsidiaryMaterialCategory: SubsidiaryMaterialCategory? = null,

    @Column(name = "handling_temperature")
    @Convert(converter = HandlingTemperatureTypeConverter::class)
    var handlingTemperature: HandlingTemperatureType? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    var warehouse: InWarehouse? = null,

    @Column(name = "supply_price")
    var supplyPrice: Int? = null,

    @Embedded
    var expirationDateInfo: ExpirationDateInfo? = null,

    @Embedded
    val singleDimension: SingleDimension,

    @Embedded
    val boxDimension: BoxDimension,

    @Column(name = "pieces_per_box")
    var piecesPerBox: Int? = null,

    @Column(name = "pieces_per_palette")
    var piecesPerPalette: Int? = null,

    @Column(name = "image_url")
    var imageUrl: String? = null,

    @OneToMany(mappedBy = "basicProduct", cascade = [CascadeType.PERSIST])
    var subsidiaryMaterialMappings: MutableSet<SubsidiaryMaterialMapping> = LinkedHashSet(),

    @OneToMany(mappedBy = "packageProduct", cascade = [CascadeType.PERSIST])
    var packageProductMappings: MutableSet<PackageProductMapping> = LinkedHashSet(),

    @Column(name = "active_yn")
    var activeYn: String = "N",

    @Column(name = "shipping_product_id")
    var shippingProductId: Long? = null,

    @Column(name = "product_code")
    var productCode: String? = null,

    @Column(name = "sales_product_id")
    var salesProductId: Long? = null,

    @Column(name = "sales_product_code")
    var salesProductCode: String? = null,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long = 0

    @Column(name = "name", nullable = false)
    lateinit var name: String

    @Column(name = "expiration_date_management_yn", nullable = false)
    lateinit var expirationDateManagementYn: String

    constructor(partnerId: Long, code: String, activeYn: String, name: String) : this(
        partnerId = partnerId,
        type = BasicProductType.PACKAGE,
        barcodeYn = "N",
        code = code,
        handlingTemperature = HandlingTemperatureType.MIX,
        expirationDateInfo = ExpirationDateInfo.default,
        activeYn = activeYn,
        singleDimension = SingleDimension.default,
        boxDimension = BoxDimension.default
    ) {
        apply {
            this.name = name
            this.expirationDateManagementYn = "N"
        }
    }

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
        warehouseRequest: InWarehouse?,
    ) {
        name = request.name
        supplyPrice = request.supplyPrice
        expirationDateManagementYn = request.expirationDateManagementYn
        expirationDateInfo = request.expirationDateInfoModel?.toEntity()
        piecesPerBox = request.piecesPerBox
        piecesPerPalette = request.piecesPerPalette
        imageUrl = request.imageUrl
        activeYn = request.activeYn

        // 단방향
        basicProductCategory = basicProductCategoryRequest
        subsidiaryMaterialCategory = subsidiaryMaterialCategoryRequest
        warehouse = warehouseRequest

        // 양방향
        subsidiaryMaterialMappings.clear()
        subsidiaryMaterialMappingRequests.forEach { addSubsidiaryMaterialMappings(it) }
    }

    fun update(name: String, activeYn: String) {
        this.name = name
        this.activeYn = activeYn
    }

    fun inactivate() {
        activeYn = "N"
    }

    fun updateProductCode(code: String) {
        productCode = code
        salesProductCode = code
    }

    fun updateSalesProductId(salesProductId: Long) {
        this.salesProductId = salesProductId
    }

    fun expireDateManage(): Boolean {
        return expirationDateManagementYn == "Y"
    }

    fun manufactureDateWrite(): Boolean {
        return expirationDateInfo?.manufactureDateWriteYn == "Y"
    }

    fun expirationDateWrite(): Boolean {
        return expirationDateInfo?.expirationDateWriteYn == "Y"
    }

    fun manufactureToExpirationDate(): Long {
        return expirationDateInfo?.manufactureToExpirationDate ?: 0
    }

    fun singleCbm(): Long {
        return (
            singleDimension.height *
                singleDimension.width *
                singleDimension.length
            ).toLong()
    }

    fun boxCbm(): Long {
        return (
            boxDimension.height *
                boxDimension.width *
                boxDimension.length
            ).toLong()
    }
}
