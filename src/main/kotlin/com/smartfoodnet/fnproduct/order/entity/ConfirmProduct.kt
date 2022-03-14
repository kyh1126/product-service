package com.smartfoodnet.fnproduct.order.entity

import com.smartfoodnet.fnproduct.order.vo.MatchingType
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import javax.persistence.*

@Entity
class ConfirmProduct(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "confirm_product_id", columnDefinition = "BIGINT UNSIGNED")
    val id: Long? = null,

    @Enumerated(EnumType.STRING)
    var type: BasicProductType,

    @Enumerated(EnumType.STRING)
    val matchingType: MatchingType,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(columnDefinition = "BIGINT UNSIGNED")
    val basicProduct: BasicProduct? = null,

    @OneToMany(mappedBy = "confirmProduct", cascade = [CascadeType.PERSIST], orphanRemoval = true)
    val confirmPackageProductList: MutableCollection<ConfirmPackageProduct> = mutableListOf(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "confirm_order_id", columnDefinition = "BIGINT UNSIGNED")
    var confirmOrder : ConfirmOrder? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collected_order_id")
    val collectedOrder : CollectedOrder,

    val quantity: Int,

    val quantityPerUnit: Int,

)