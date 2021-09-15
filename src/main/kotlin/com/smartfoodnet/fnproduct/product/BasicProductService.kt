package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.model.response.BasicProductModel
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class BasicProductService(
    private val basicProductRepository: BasicProductRepository,
) {

    fun getBasicProducts(partnerId: Long): List<BasicProductModel> {
        return basicProductRepository.findByPartnerId(partnerId).map { toBasicProductModel(it) }
    }

    private fun toBasicProductModel(basicProduct: BasicProduct): BasicProductModel {
        return BasicProductModel.fromEntity(basicProduct)
    }

}
