package com.smartfoodnet.fninventory.stock.support

import com.smartfoodnet.fninventory.stock.entity.StockByBestBefore
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface StockByBestBeforeRepository : JpaRepository<StockByBestBefore, Long>,
    QuerydslPredicateExecutor<StockByBestBefore> {

}
