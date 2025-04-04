package com.smartfoodnet.fnproduct.store

import com.smartfoodnet.common.error.exception.NoSuchElementError
import com.smartfoodnet.common.error.exception.ValidateError
import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.fnproduct.product.BasicProductRepository
import com.smartfoodnet.fnproduct.product.model.response.BasicProductSimpleModel
import com.smartfoodnet.fnproduct.store.entity.StoreProduct
import com.smartfoodnet.fnproduct.store.entity.StoreProductMapping
import com.smartfoodnet.fnproduct.store.model.request.StoreProductCreateModel
import com.smartfoodnet.fnproduct.store.model.request.StoreProductUpdateModel
import com.smartfoodnet.fnproduct.store.model.response.StoreProductFlatModel
import com.smartfoodnet.fnproduct.store.model.response.StoreProductModel
import com.smartfoodnet.fnproduct.store.support.StoreProductMappingRepository
import com.smartfoodnet.fnproduct.store.support.StoreProductRepository
import com.smartfoodnet.fnproduct.store.support.StoreProductSearchCondition
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
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

    fun findFlattenedStoreProducts(condition: PredicateSearchCondition, page: Pageable): Page<StoreProductFlatModel> {
        val storeProducts = storeProductRepository.findFlattenedStoreProducts(condition, page)
        val pageable = storeProducts.pageable

        val storeProductFlatModels =
            storeProducts.content.flatMap { it.storeProductMappings }
                .map { buildStoreProductFlatModel(it) }

        return PageImpl(storeProductFlatModels, pageable, storeProducts.totalElements)
    }

    fun getStoreProduct(storeProductId: Long): StoreProductModel? {
        return storeProductRepository.findByIdOrNull(storeProductId)?.let { StoreProductModel.from(it) }
    }

    fun getStoreProductForOrderDetail(condition: StoreProductSearchCondition): StoreProduct? {
        return storeProductRepository.findStoreProduct(condition)
    }

    @Transactional
    fun createStoreProduct(storeProduct: StoreProduct) = storeProductRepository.save(storeProduct)


    @Transactional
    fun createStoreProducts(storeProductCreateModel: StoreProductCreateModel): List<StoreProductModel> {
        val storeProducts = with(storeProductCreateModel) {
            options?.map { option ->
                val storeProduct = StoreProduct(
                    storeId = storeId,
                    storeName = storeName,
                    storeIcon = storeIcon,
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
    fun updateStoreProduct(storeProductId: Long, storeProductModel: StoreProductUpdateModel): StoreProductModel {
        val storeProduct = storeProductRepository.findByIdOrNull(storeProductId)
            ?: throw NoSuchElementError(errorMessage = "존재하지 않는 상품입니다. [storeProductId = ${storeProductId}]")

        storeProduct.update(storeProductModel)

        val newMappings = buildStoreProductMappings(storeProductModel, storeProduct)
        newMappings?.let {
            if (it.isNotEmpty()) {
                storeProduct.updateStoreProductMappings(it)
            }
        }

        return StoreProductModel.from(storeProduct)
    }

    @Transactional
    fun deleteStoreProduct(storeProductId: Long) {
        val storeProduct = storeProductRepository.findByIdOrNull(storeProductId)
            ?: throw NoSuchElementError(errorMessage = "존재하지 않는 상품입니다. [storeProductId = ${storeProductId}]")
        storeProduct.delete()

        storeProductRepository.save(storeProduct)
    }

    private fun buildStoreProductFlatModel(storeProductMapping: StoreProductMapping): StoreProductFlatModel {
        val storeProduct = storeProductMapping.storeProduct
        val basicProduct = storeProductMapping.basicProduct
        return storeProductMapping.run {
            StoreProductFlatModel(
                id = storeProduct.id,
                storeId = storeProduct.storeId,
                storeName = storeProduct.storeName,
                storeIcon = storeProduct.storeIcon,
                partnerId = storeProduct.partnerId,
                name = storeProduct.name,
                storeProductCode = storeProduct.storeProductCode,
                optionName = storeProduct.optionName,
                optionCode = storeProduct.optionCode,
                basicProduct = BasicProductSimpleModel.fromEntity(basicProduct),
                quantity = quantity,
                createdAt = storeProduct.createdAt,
                updatedAt = storeProduct.updatedAt
            )
        }
    }

    private fun buildStoreProductMappings(
        storeProductModel: StoreProductUpdateModel,
        storeProduct: StoreProduct
    ): Set<StoreProductMapping>? {
        val newBasicProductIds =
            storeProductModel.storeProductBasicProductMappings?.map { it.basicProductId } ?: return null

        if (newBasicProductIds.size != newBasicProductIds.toSet().size) {
            throw ValidateError("중복된 기본상품이 존재합니다.")
        }

        val newMappings = storeProductModel.storeProductBasicProductMappings.map { model ->
            val newMapping = storeProduct.storeProductMappings.firstOrNull { mapping ->
                mapping.basicProduct.id == model.basicProductId
            } ?: StoreProductMapping(
                storeProduct = storeProduct,
                basicProduct = basicProductRepository.findByIdOrNull(model.basicProductId)
                    ?: throw NoSuchElementError("기본상품이 존재하지 않습니다. [basicProductId: ${model.basicProductId}]")
            )

            newMapping.quantity = model.quantity
            newMapping
        }.toSet()

        return newMappings
    }
}
