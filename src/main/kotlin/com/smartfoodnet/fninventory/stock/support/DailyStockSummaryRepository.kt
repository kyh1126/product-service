package com.smartfoodnet.fninventory.stock.support

import com.smartfoodnet.fninventory.stock.entity.DailyStockSummary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface DailyStockSummaryRepository : JpaRepository<DailyStockSummary, Long>,
    QuerydslPredicateExecutor<DailyStockSummary> {
    override fun findAll(pageable: Pageable): Page<DailyStockSummary>
}
