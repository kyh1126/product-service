package com.smartfoodnet.fnproduct.store

import com.smartfoodnet.common.error.exception.NoSuchElementError
import com.smartfoodnet.common.error.exception.ValidateError
import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.fnproduct.product.BasicProductRepository
import com.smartfoodnet.fnproduct.store.entity.StoreProduct
import com.smartfoodnet.fnproduct.store.entity.StoreProductMapping
import com.smartfoodnet.fnproduct.store.model.request.StoreProductCreateModel
import com.smartfoodnet.fnproduct.store.model.request.StoreProductMappingCreateModel
import com.smartfoodnet.fnproduct.store.model.request.StoreProductUpdateModel
import com.smartfoodnet.fnproduct.store.model.response.StoreProductModel
import com.smartfoodnet.fnproduct.store.support.StoreProductMappingRepository
import com.smartfoodnet.fnproduct.store.support.StoreProductRepository
import com.smartfoodnet.fnproduct.store.support.StoreProductSearchCondition
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class StoreProductService(
    val storeProductRepository: StoreProductRepository,
    val basicProductRepository: BasicProductRepository,
    val storeProductMappingRepository: StoreProductMappingRepository
) {
    fun findStoreProducts(condition: PredicateSearchCondition, page: Pageable): Page<StoreProductModel> {
        return storeProductRepository.findStoreProducts(condition, page).map { StoreProductModel.from(it) }
    }

    fun getStoreProduct(storeProductId: Long): StoreProductModel? {
        return storeProductRepository.findByIdOrNull(storeProductId)?.let { StoreProductModel.from(it) }
    }

    fun getStoreProductForOrderDetail(condition: StoreProductSearchCondition): StoreProduct? {
        return storeProductRepository.findStoreProduct(condition)
    }

    @Transactional
    fun createStoreProducts(storeProductCreateModel: StoreProductCreateModel): List<StoreProductModel> {
        val storeProducts = with(storeProductCreateModel) {
            options?.map { option ->
                val storeProduct = StoreProduct(
                    storeId = storeId,
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
                        ?: throw NoSuchElementError("기본상품이 존재하지 않습니다 [id = ${mapping.basicProductId}]")

                    storeProduct.storeProductMappings.add(
                        StoreProductMapping(
                            storeProduct = storeProduct,
                            basicProduct = basicProduct,
                            quantity = mapping.quantity
                        )
                    )
                }

                storeProduct
            }
        } ?: listOf(storeProductCreateModel.toEntity())



        return storeProductRepository.saveAll(storeProducts).map(StoreProductModel::from)
    }

    @Transactional
    fun updateStoreProduct(storeProductModel: StoreProductUpdateModel): StoreProductModel {
        val storeProduct = storeProductRepository.findByIdOrNull(storeProductModel.id)
            ?: throw NoSuchElementError(errorMessage = "존재하지 않는 상품입니다. [storeProductId = ${storeProductModel.id}]")

        storeProduct.update(storeProductModel)

        storeProduct.storeProductMappings =
            buildNewStoreProductMappings(storeProductModel, storeProduct)?.toMutableSet() ?: mutableSetOf()

        return StoreProductModel.from(storeProduct)
    }

    fun buildNewStoreProductMappings(
        storeProductModel: StoreProductUpdateModel,
        storeProduct: StoreProduct
    ): List<StoreProductMapping>? {
        val newBasicProductIds = storeProductModel.storeProductBasicProductMappings?.map { it.basicProductId }

        val unMappedBasicProducts = storeProduct.storeProductMappings.filterNot { mapping ->
            newBasicProductIds?.contains(mapping.basicProduct.id) ?: false
        }

        unMappedBasicProducts.forEach { it.delete() }

        val storeProductMappings = storeProductModel.storeProductBasicProductMappings?.map { mapping ->
            val storeProductMapping = mapping.id?.let { id -> storeProductMappingRepository.findByIdOrNull(id) }
                ?: StoreProductMapping(
                    storeProduct = storeProduct,
                    basicProduct = basicProductRepository.findByIdOrNull(mapping.basicProductId)
                        ?: throw NoSuchElementError(errorMessage = "기본상품이 존재하지 않습니다 [id = ${mapping.basicProductId}]")
                )
            storeProductMapping.quantity = mapping.quantity

            storeProductMapping
        }

        return storeProductMappings
    }

    @Transactional
    fun mapBasicProducts(storeProductMappingModel: StoreProductMappingCreateModel): StoreProductModel {
        val storeProduct = storeProductRepository.findById(storeProductMappingModel.storeProductId)
            .orElseThrow { ValidateError(errorMessage = "store product does not exist.") }

        storeProductMappingModel.mappings.forEach { mappingModel ->
            val basicProduct =
                basicProductRepository.findByIdOrNull(mappingModel.basicProductId) ?: throw NoSuchElementError(
                    errorMessage = "기본상품이 존재하지 않습니다 [id = ${mappingModel.basicProductId}]"
                )

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
