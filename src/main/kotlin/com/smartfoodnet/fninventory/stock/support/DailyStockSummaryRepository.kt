package com.smartfoodnet.fninventory.stock.support

import com.smartfoodnet.fninventory.stock.entity.DailyStockSummary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import java.time.LocalDate

interface DailyStockSummaryRepository : JpaRepository<DailyStockSummary, Long>,
    QuerydslPredicateExecutor<DailyStockSummary> {
    override fun findAll(pageable: Pageable): Page<DailyStockSummary>
    fun findAllByEffectiveDateIn(effectiveDates: List<LocalDate>): List<DailyStockSummary>?

    @Modifying
    @Query("delete from DailyStockSummary summary where summary.effectiveDate in :effectiveDates")
    fun deleteAllByEffectiveDatesIn(effectiveDates: List<LocalDate>)
}
