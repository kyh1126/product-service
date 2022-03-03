package com.smartfoodnet.fnproduct.order.support

import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.fnproduct.order.dto.CollectedOrderModel
import com.smartfoodnet.fnproduct.order.dto.ConfirmOrderModel
import com.smartfoodnet.fnproduct.order.entity.CollectedOrder
import com.smartfoodnet.fnproduct.order.entity.ConfirmOrder
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ConfirmOrderRepositoryCustom{
    fun findAllByConfirmOrderWithPageable(condition: PredicateSearchCondition, page: Pageable) : Page<ConfirmOrderModel>
    fun findAllByConfirmOrder(condition: PredicateSearchCondition) : List<ConfirmOrderModel>
}