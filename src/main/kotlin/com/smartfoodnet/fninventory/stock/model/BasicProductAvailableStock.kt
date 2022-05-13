package com.smartfoodnet.fninventory.stock.model

import com.smartfoodnet.apiclient.response.NosnosStockModel
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.entity.PackageProductMapping
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType

class BasicProductAvailableStock(
    val baseBasicProduct: BasicProduct
) {
    var stock: Long = 0

    val isPackage: Boolean = baseBasicProduct.type == BasicProductType.PACKAGE

    val subBasicProduct: List<BasicProduct>
        get() = expandPackageProducts()

    private val packageProductMappings: List<PackageProductMapping>
        get() = baseBasicProduct.packageProductMappings.toList()

    val nosnosStockModel: MutableList<NosnosStockModel> = mutableListOf()

    private fun expandPackageProducts(): List<BasicProduct> {
        return when (baseBasicProduct.type) {
            BasicProductType.PACKAGE -> getBasicProduct(baseBasicProduct.packageProductMappings)
            else -> listOf()
        }
    }

    private fun getBasicProduct(packageProductList: Collection<PackageProductMapping>): List<BasicProduct> {
        return packageProductList.map { it.selectedBasicProduct }
    }

    private fun getMinStocks(): Int {
        return if (isPackage) {
            packageProductMappings.map {
                val orderQuantity: Int = nosnosStockModel
                    .find { stock -> stock.shippingProductId == it.selectedBasicProduct.shippingProductId }?.normalStock
                    ?: 0
                orderQuantity / it.quantity
            }.minOf { it }
        } else {
            nosnosStockModel.find { stock -> stock.shippingProductId == baseBasicProduct.shippingProductId }?.normalStock ?: 0
        }
    }

    fun toDto() : AvailableStockModel{
        return AvailableStockModel(baseBasicProduct.id!!, getMinStocks())
    }
}