package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.fnproduct.product.entity.PackageProductMapping
import com.smartfoodnet.fnproduct.product.model.request.PackageProductMappingSearchCondition
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PackageProductMappingCustom {
    fun findAllByCondition(
        condition: PackageProductMappingSearchCondition,
        page: Pageable
    ): Page<PackageProductMapping>

    fun findAllByBasicProductId(basicProductId: Long) : List<PackageProductMapping>
}
