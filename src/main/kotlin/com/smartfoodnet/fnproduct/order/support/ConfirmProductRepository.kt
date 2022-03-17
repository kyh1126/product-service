package com.smartfoodnet.fnproduct.order.support

import com.smartfoodnet.fnproduct.order.entity.ConfirmOrder
import com.smartfoodnet.fnproduct.order.entity.ConfirmProduct
import org.springframework.data.jpa.repository.JpaRepository

interface ConfirmProductRepository : JpaRepository<ConfirmProduct, Long>, ConfirmProductRepositoryCustom