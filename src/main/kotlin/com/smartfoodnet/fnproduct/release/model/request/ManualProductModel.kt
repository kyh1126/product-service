package com.smartfoodnet.fnproduct.release.model.request

import com.smartfoodnet.fnproduct.order.entity.CollectedOrder
import com.smartfoodnet.fnproduct.order.entity.ConfirmProduct
import com.smartfoodnet.fnproduct.order.vo.MatchingType
import com.smartfoodnet.fnproduct.product.entity.BasicProduct

sealed class ManualProductModel {
    abstract val productId: Long
    abstract val quantity: Int

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

data class ReOrderProductInfo(
    override var productId: Long,
    override var quantity: Int,
) : ManualProductModel()

data class ManualReleaseProductInfo(
    override var productId: Long,
    override var quantity: Int,
) : ManualProductModel()