package com.smartfoodnet.fnproduct.store.support

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.BooleanExpression
import com.smartfoodnet.common.error.exception.BaseRuntimeException
import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.fnproduct.order.entity.CollectedOrder
import com.smartfoodnet.fnproduct.product.entity.QBasicProduct.basicProduct
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import com.smartfoodnet.fnproduct.store.entity.QStoreProduct.storeProduct
import io.swagger.annotations.ApiModelProperty

class StoreProductSearchCondition(
    @ApiModelProperty(value = "화주사 (고객) ID", required = true)
    var partnerId: Long? = null,
    @ApiModelProperty(value = "쇼핑몰 ID")
    val storeIds: List<Long>? = null,
    @ApiModelProperty(value = "쇼핑몰 상품명")
    val storeProductName: String? = null,
    @ApiModelProperty(value = "쇼핑몰 상품코드")
    val storeProductCode: String? = null,
    @ApiModelProperty(value = "쇼핑몰 상품 옵션코드")
    val storeProductOptionCode: String? = null,
    @ApiModelProperty(value = "쇼핑몰 상품 옵션명")
    val storeProductOptionName: String? = null,
    @ApiModelProperty(value = "매칭상품명")
    val basicProductName: String? = null,
    @ApiModelProperty(value = "매칭상코드")
    val basicProductCode: String? = null,
    @ApiModelProperty(value = "매칭상품 타입 (기본/묶음)")
    val basicProductType: BasicProductType? = null,
    @ApiModelProperty(value = "매칭 여부")
    val basicProductMatchFlag: Boolean? = null
    ) : PredicateSearchCondition() {
    override fun assemblePredicate(predicate: BooleanBuilder): Predicate {
        return predicate.orAllOf(
            storeProduct.partnerId.eq(partnerId),
            storeIds?.let { storeProduct.storeId.`in`(it) },
            storeProductName?.let { storeProduct.name.contains(it) },
            storeProductCode?.let { storeProduct.storeProductCode.eq(it) },
            storeProductOptionCode?.let { storeProduct.optionCode.eq(it) },
            storeProductOptionName?.let { storeProduct.optionName.contains(it) },
            basicProductName?.let { basicProduct.name.contains(it) },
            basicProductCode?.let { basicProduct.code.eq(it) },
            basicProductType?.let { basicProduct.type.eq(it) },
            isMatched()
        )
    }

    private fun isMatched(): BooleanExpression? {
        return basicProductMatchFlag?.let {
            if (!basicProductMatchFlag) {
                storeProduct.storeProductMappings.isEmpty
            } else {
                storeProduct.storeProductMappings.isNotEmpty
            }
        }
    }

    companion object {
        fun toSearchConditionModel(collectedOrder: CollectedOrder): StoreProductSearchCondition {
            return with(collectedOrder) {
                val nonNullStoreId = storeId
                    ?: throw BaseRuntimeException(errorMessage = "storeId가 없습니다")

                StoreProductSearchCondition(
                    partnerId = partnerId,
                    storeIds = listOf(nonNullStoreId),
                    storeProductCode = collectedProductInfo.collectedStoreProductCode,
                    storeProductName = collectedProductInfo.collectedStoreProductName,
                    storeProductOptionName = collectedProductInfo.collectedStoreProductOptionName,
                )
            }
        }
    }
}
