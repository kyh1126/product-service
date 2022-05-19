package com.smartfoodnet.fnproduct.order.support

import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.fnproduct.order.dto.ConfirmProductModel

interface ConfirmProductRepositoryCustom{
    fun findAllCollectedOrderWithConfirmProduct(condition: PredicateSearchCondition): List<ConfirmProductModel>
}