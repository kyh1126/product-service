package com.smartfoodnet.fnproduct.order.support

import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.fnproduct.order.dto.ConfirmOrderModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ConfirmOrderRepositoryCustom{
    fun findAllByConfirmOrderWithPageable(condition: PredicateSearchCondition, page: Pageable) : Page<ConfirmOrderModel>
    fun findAllByConfirmOrder(condition: PredicateSearchCondition) : List<ConfirmOrderModel>
}