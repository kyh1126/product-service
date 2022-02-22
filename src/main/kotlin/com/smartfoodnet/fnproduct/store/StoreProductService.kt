package com.smartfoodnet.fnproduct.store

import com.smartfoodnet.common.error.exception.ValidateError
import com.smartfoodnet.fnproduct.product.BasicProductRepository
import com.smartfoodnet.fnproduct.store.entity.StoreProduct
import com.smartfoodnet.fnproduct.store.entity.StoreProductMapping
import com.smartfoodnet.fnproduct.store.model.request.StoreProductCreateModel
import com.smartfoodnet.fnproduct.store.model.request.StoreProductMappingCreateModel
import com.smartfoodnet.fnproduct.store.model.response.StoreProductModel
import com.smartfoodnet.fnproduct.store.support.StoreProductMappingRepository
import com.smartfoodnet.fnproduct.store.support.StoreProductRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class StoreProductService(
    var storeProductRepository: StoreProductRepository,
    var basicProductRepository: BasicProductRepository,
    var storeProductMappingRepository: StoreProductMappingRepository
) {

    fun getStoreProducts(partnerId: Long): List<StoreProductModel> {
        val storeProducts = storeProductRepository.findAllByPartnerId(partnerId)
        return storeProducts.map { StoreProductModel.from(it) }
    }

    fun getStoreProductForOrderDetail(partnerId: Long?, storeProductCode: String?): StoreProduct? {
        if (partnerId == null || storeProductCode == null) {
            return null
        }

        return storeProductRepository.findByPartnerIdAndStoreProductCode(partnerId, storeProductCode)
    }

    @Transactional
    fun createStoreProducts(storeProductCreateModel: StoreProductCreateModel): List<StoreProductModel> {
        val storeProducts = with(storeProductCreateModel) {
            options.map { option ->
                val storeProduct = StoreProduct(
                    storeCode = storeCode,
                    storeName = storeName,
                    partnerId = partnerId,
                    name = name,
                    storeProductCode = storeProductCode,
                    optionName = option.name,
                    optionCode = option.code
                )

                option.storeProductBasicProductMappings?.forEach { mapping ->
                    val basicProduct = basicProductRepository.findByIdOrNull(mapping.basicProductId)
                    if (basicProduct != null) {
                        storeProduct.storeProductMappings.add(
                            StoreProductMapping(
                                storeProduct = storeProduct,
                                basicProduct = basicProduct,
                                quantity = mapping.quantity
                            )
                        )
                    }
                }

                storeProduct
            }
        }

        return storeProductRepository.saveAll(storeProducts).map(StoreProductModel::from)
    }

    @Transactional
    fun mapBasicProducts(storeProductMappingModel: StoreProductMappingCreateModel): StoreProductModel {
        val storeProduct = storeProductRepository.findById(storeProductMappingModel.storeProductId)
            .orElseThrow { ValidateError(errorMessage = "store product does not exist.") }

        storeProductMappingModel.mappings.forEach { mappingModel ->
            val basicProduct = basicProductRepository.findByIdOrNull(mappingModel.basicProductId)

            if (mappingModel.id != null) {
                val storeProductMapping =
                    storeProduct.storeProductMappings.firstOrNull { storeProductMapping -> mappingModel.id == storeProductMapping.id }
                storeProductMapping?.basicProduct = basicProduct
                storeProductMapping?.quantity = mappingModel.quantity
            } else {
                val storeProductMapping = StoreProductMapping(
                    basicProduct = basicProduct,
                    storeProduct = storeProduct,
                    quantity = mappingModel.quantity
                )
                storeProduct.storeProductMappings.add(storeProductMapping)
            }
        }

        return StoreProductModel.from(storeProductRepository.save(storeProduct))
    }
}
