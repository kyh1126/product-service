package com.smartfoodnet.fnproduct.product.mapper

import com.smartfoodnet.fnproduct.product.PackageProductMappingRepository
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class BasicProductFinder(
    private val packageProductMappingRepository: PackageProductMappingRepository,
) {
    fun getPackageProductByBasicProduct(basicProductId: Long): BasicProduct? {
        val packageProductMapping =
            packageProductMappingRepository.findBySelectedBasicProduct_Id(basicProductId)
        return packageProductMapping?.packageProduct
    }
}
