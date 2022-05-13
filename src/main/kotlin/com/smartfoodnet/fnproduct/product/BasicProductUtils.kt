package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.entity.PackageProductMapping
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType

object BasicProductUtils {

    fun expandPackageProducts(basicProduct: BasicProduct): List<BasicProduct> {
        return when (basicProduct.type) {
            BasicProductType.PACKAGE -> getBasicProduct(basicProduct.packageProductMappings)
            else -> listOf(basicProduct)
        }
    }

    private fun getBasicProduct(packageProductList: Collection<PackageProductMapping>): List<BasicProduct> {
        return packageProductList.map { it.selectedBasicProduct }
    }
}