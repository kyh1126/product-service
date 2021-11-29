package com.smartfoodnet.fnproduct.store

import com.smartfoodnet.common.error.exception.UserRequestError
import com.smartfoodnet.common.error.exception.ValidateError
import com.smartfoodnet.fnproduct.product.BasicProductRepository
import com.smartfoodnet.fnproduct.store.entity.StoreProduct
import com.smartfoodnet.fnproduct.store.model.StoreProductModel
import com.smartfoodnet.fnproduct.store.support.StoreProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class StoreProductService(
    var storeProductRepository: StoreProductRepository,
    var basicProductRepository: BasicProductRepository
) {

    fun getStoreProducts(partnerId: Long): List<StoreProductModel> {
        val storeProducts = storeProductRepository.findAllByPartnerId(partnerId)
        return storeProducts.map { StoreProductModel.from(it) }
    }

    fun getStoreProductForOrderDetail(partnerId: Long?, storeProductCode: String?): StoreProduct {
        return storeProductRepository.findByPartnerIdAndStoreProductCode(partnerId!!, storeProductCode!!) ?: throw UserRequestError(errorMessage = "쇼핑몰 상품이 존재하지 않습니다")
    }

    @Transactional
    fun mapBasicProduct(storeProductId: Long, basicProductId: Long): StoreProductModel {
        val storeProduct = storeProductRepository.findById(storeProductId)
            .orElseThrow { ValidateError(errorMessage = "store product does not exist.") }
        storeProduct.basicProduct = basicProductRepository.findById(basicProductId)
            .orElseThrow { ValidateError(errorMessage = "store product does not exist") }

        return StoreProductModel.from(storeProductRepository.save(storeProduct))
    }

    @Transactional
    fun createStoreProduct(storeProductModel: StoreProductModel): StoreProductModel {
        val storeProduct = storeProductModel.toEntity()
        if (storeProductModel.basicProductId != null) {
            storeProduct.basicProduct =
                basicProductRepository.findById(storeProductModel.basicProductId!!)
                    .orElseThrow { ValidateError(errorMessage = "store product does not exist") }
        }

        return StoreProductModel.from(storeProductRepository.save(storeProduct))
    }
}
