package com.smartfoodnet.fnproduct.order.entity

import com.smartfoodnet.common.entity.BaseEntity
import javax.persistence.*

@Entity
class ConfirmOrder(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "confirm_order_id", columnDefinition = "BIGINT UNSIGNED")
    val id: Long? = null,
    val partnerId: Long,
    val bundleNumber : String,
    var orderId: Long? = null,
    var orderCode: String? = null,
    @OneToMany(mappedBy = "confirmOrder", cascade = [CascadeType.PERSIST], orphanRemoval = true)
    val confirmProductList: MutableSet<ConfirmProduct> = mutableSetOf()
): BaseEntity(){
    fun addConfirmProduct(confirmProduct : ConfirmProduct){
        if (this.confirmProductList.contains(confirmProduct)) this.confirmProductList.remove(confirmProduct)
        this.confirmProductList.add(confirmProduct)
        confirmProduct.confirmOrder = this
    }
}