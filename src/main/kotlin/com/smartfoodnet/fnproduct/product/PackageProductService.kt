package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.fnproduct.product.model.response.PackageProductDetailModel
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PackageProductService(
    private val basicProductRepository: BasicProductRepository,
) {

    fun getPackageProducts(
        condition: PredicateSearchCondition,
        page: Pageable
    ): PageResponse<PackageProductDetailModel> {
        return basicProductRepository.findAll(condition.toPredicate(), page)
            .map(PackageProductDetailModel::fromEntity)
            .run { PageResponse.of(this) }
    }

}
