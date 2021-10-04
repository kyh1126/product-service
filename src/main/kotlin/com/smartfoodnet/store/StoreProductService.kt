package com.smartfoodnet.store

import com.smartfoodnet.common.error.exception.ValidateError
import com.smartfoodnet.fnproduct.product.BasicProductRepository
import com.smartfoodnet.store.model.StoreProductModel
import com.smartfoodnet.store.support.StoreProductRepository
import org.springframework.stereotype.Service

@Service
class StoreProductService(var storeProductRepository: StoreProductRepository, var basicProductRepository: BasicProductRepository) {
    fun getStoreProducts(partnerId: Long): List<StoreProductModel> {
        val storeProducts = storeProductRepository.findAllByPartnerId(partnerId)
        return storeProducts.map { StoreProductModel.from(it) }
    }

    fun mapBasicProduct(storeProductId: Long, basicProductId: Long) {
        val storeProduct = storeProductRepository.findById(storeProductId).orElseThrow { ValidateError(errorMessage = "store product does not exist.") }
        storeProduct.basicProduct = basicProductRepository.findById(basicProductId).orElseThrow { ValidateError(errorMessage = "store product does not exist") }

        storeProductRepository.save(storeProduct)
    }

    fun createStoreProduct(storeProductModel: StoreProductModel): StoreProductModel {
        val storeProduct = storeProductModel.toEntity()
        if(storeProductModel.basicProductId != null) {
            storeProduct.basicProduct = basicProductRepository.findById(storeProductModel.basicProductId!!).orElseThrow { ValidateError(errorMessage = "store product does not exist") }
        }

        return StoreProductModel.from(storeProductRepository.save(storeProduct))
    }
}