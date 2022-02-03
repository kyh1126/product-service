package com.smartfoodnet.fninventory.stock.support

import com.smartfoodnet.fninventory.stock.entity.StockByBestBefore
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import java.time.LocalDate

interface StockByBestBeforeRepository : JpaRepository<StockByBestBefore, Long>,
    QuerydslPredicateExecutor<StockByBestBefore> {
    fun findAllByCollectedDate(collectedDate: LocalDate): List<StockByBestBefore>?
}
