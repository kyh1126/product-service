package com.smartfoodnet.fninventory.stock.dto

import com.smartfoodnet.apiclient.response.NosnosStockModel
import com.smartfoodnet.common.formatToBasicIsoDate
import com.smartfoodnet.fninventory.stock.model.AvailableStockModel
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.entity.PackageProductMapping
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType

class BasicProductAvailableStock(
    val baseBasicProduct: BasicProduct
) {
    var stock: Int = 0

    val isPackage: Boolean
        get() = baseBasicProduct.type == BasicProductType.PACKAGE

    fun getSubBasicProduct(): List<BasicProduct> {
        return when (baseBasicProduct.type) {
            BasicProductType.PACKAGE -> getBasicProductFromPackage(baseBasicProduct.packageProductMappings)
            else -> listOf()
        }
    }

    fun getBasicProductFromPackage(packageProductList: MutableSet<PackageProductMapping>): List<BasicProduct> {
        return packageProductList.map { it.selectedBasicProduct }
    }

    fun calcMinStocks(nosnosStockModel : List<NosnosStockModel>) {
        stock = if (isPackage) {
            baseBasicProduct.packageProductMappings.toList().map {
                val orderQuantity: Int = nosnosStockModel
                    .find { stock -> stock.shippingProductId == it.selectedBasicProduct.shippingProductId }?.normalStock
                    ?: 0
                orderQuantity / it.quantity
            }.minOf { it }
        } else {
            nosnosStockModel.find { stock -> stock.shippingProductId == baseBasicProduct.shippingProductId }?.normalStock ?: 0
        }
    }

    fun calcQuantityPerBasicProduct(confirmProductQuantity : Int) : List<QuantityPerBasicProduct> {
        return if (isPackage) {
            baseBasicProduct.packageProductMappings.map {
                QuantityPerBasicProduct(it.selectedBasicProduct, it.quantity * confirmProductQuantity)
            }
        } else {
            listOf(QuantityPerBasicProduct(baseBasicProduct, confirmProductQuantity))
        }
    }

    fun toDto() : AvailableStockModel {
        return AvailableStockModel(baseBasicProduct.id, stock)
    }
}

data class QuantityPerBasicProduct(
    val basicProduct: BasicProduct,
    val quantity : Int
)