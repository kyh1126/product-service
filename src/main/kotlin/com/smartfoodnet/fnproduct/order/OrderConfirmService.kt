package com.smartfoodnet.fnproduct.order

import com.smartfoodnet.common.error.exception.BaseRuntimeException
import com.smartfoodnet.fnproduct.order.entity.CollectedOrder
import com.smartfoodnet.fnproduct.order.entity.ConfirmOrder
import com.smartfoodnet.fnproduct.order.entity.ConfirmPackageProduct
import com.smartfoodnet.fnproduct.order.entity.ConfirmProduct
import com.smartfoodnet.fnproduct.order.model.OrderStatus
import com.smartfoodnet.fnproduct.order.support.CollectedOrderRepository
import com.smartfoodnet.fnproduct.order.support.ConfirmOrderRepository
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class OrderConfirmService(
    val collectedOrderRepository: CollectedOrderRepository,
    val confirmOrderRepository: ConfirmOrderRepository
){
    @Transactional
    fun createConfirmOrder(partnerId: Long, collectedIds: List<Long>){
        val collectedList = collectedOrderRepository.findAllById(collectedIds)

        if (!collectedList.all { it.partnerId == partnerId })
            throw BaseRuntimeException(errorMessage = "해당 고객사의 주문건이 아닙니다")

        if (!collectedList.all { it.status == OrderStatus.NEW })
            throw BaseRuntimeException(errorMessage = "이미 출고지시가 처리된 데이터 입니다")

        collectedList.groupBy {
            it.bundleNumber
        }.forEach{
            convertData(partnerId, it.key, it.value)
        }
    }

    private fun convertData(partnerId: Long, bundleNumber : String, collectedList : List<CollectedOrder>){
        // TODO : 묶음번호에 해당하는 마스터 데이터 생성
        val confirmOrder = ConfirmOrder(
            partnerId = partnerId,
            bundleNumber = bundleNumber
        )

        // TODO : 기본/모음상품 매핑
        collectedList.forEach {
            val storeMapping = it.storeProduct?.storeProductMappings
            storeMapping?.forEach { storeProductMapping ->
                val basicProduct = storeProductMapping.basicProduct
                val orderQuantity = (it.quantity?:1) * storeProductMapping.quantity
                createConfirmProductAndPackageProduct(confirmOrder, basicProduct, orderQuantity)
            }
            it.confirmOrder = confirmOrder
            it.status = it.status.next()
        }

        confirmOrderRepository.save(confirmOrder)
    }

    private fun createConfirmProductAndPackageProduct(confirmOrder: ConfirmOrder, basicProduct : BasicProduct, orderQuantity: Int){
        val confirmProduct = ConfirmProduct(
            type = basicProduct.type,
            basicProduct = basicProduct,
            confirmOrder = confirmOrder,
            quantity = orderQuantity
        )

        if (basicProduct.type == BasicProductType.PACKAGE){
            basicProduct.packageProductMappings.forEach {
                val selectedBasicProduct = it.selectedBasicProduct
                confirmProduct.confirmPackageProductList.add(
                    ConfirmPackageProduct(
                        confirmProduct = confirmProduct,
                        basicProduct = selectedBasicProduct,
                        quantity = orderQuantity * it.quantity
                    )
                )
            }
        }

        confirmOrder.addConfirmProduct(confirmProduct)
    }
}