package com.smartfoodnet.fnproduct.order.support

import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.fnproduct.order.dto.CollectedOrderModel
import com.smartfoodnet.fnproduct.order.dto.ConfirmProductModel
import com.smartfoodnet.fnproduct.order.entity.ConfirmOrder
import com.smartfoodnet.fnproduct.order.entity.ConfirmProduct
import org.springframework.data.jpa.repository.JpaRepository

interface ConfirmProductRepositoryCustom{
    fun findAllCollectedOrderWithConfirmProduct(condition: PredicateSearchCondition): List<ConfirmProductModel>
}