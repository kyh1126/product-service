package com.smartfoodnet.fnproduct.order.entity

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(columnDefinition = "BIGINT UNSIGNED")
    val basicProduct: BasicProduct? = null,

    @OneToMany(mappedBy = "basicProduct", cascade = [CascadeType.PERSIST])
    val confirmPackageProductList: MutableCollection<ConfirmPackageProduct> = mutableListOf(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "confirm_order_id", columnDefinition = "BIGINT UNSIGNED")
    var confirmOrder : ConfirmOrder? = null,

    val quantity: Int
)