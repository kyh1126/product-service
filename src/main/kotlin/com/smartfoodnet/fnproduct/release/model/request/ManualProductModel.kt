package com.smartfoodnet.fnproduct.release.model.request

import com.smartfoodnet.fnproduct.order.entity.CollectedOrder
import com.smartfoodnet.fnproduct.order.entity.ConfirmProduct
import com.smartfoodnet.fnproduct.order.vo.MatchingType
import com.smartfoodnet.fnproduct.product.entity.BasicProduct

data class ManualProductModel(
    val productId: Long,
    val quantity: Int
) {
    fun toConfirmProduct(
        collectedOrder: CollectedOrder,
        basicProduct: BasicProduct,
        matchingType: MatchingType
    ): ConfirmProduct {
        return ConfirmProduct(
            collectedOrder = collectedOrder,
            type = basicProduct.type,
            matchingType = matchingType,
            basicProduct = basicProduct,
            quantity = quantity,
            quantityPerUnit = quantity
        )
    }
}