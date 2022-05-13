package com.smartfoodnet.fninventory.stock

import com.smartfoodnet.apiclient.WmsApiClient
import com.smartfoodnet.common.Constants
import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.common.utils.Log
import com.smartfoodnet.fninventory.stock.model.*
import com.smartfoodnet.fninventory.stock.support.DailyStockSummaryRepository
import com.smartfoodnet.fninventory.stock.support.StockByBestBeforeRepository
import com.smartfoodnet.fninventory.stock.support.StockByBestBeforeSearchCondition
import com.smartfoodnet.fnproduct.order.OrderService
import com.smartfoodnet.fnproduct.order.vo.OrderStatus
import com.smartfoodnet.fnproduct.product.BasicProductRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
@Transactional(readOnly = true)
class StockService(
    private val basicProductRepository: BasicProductRepository,
    private val stockByBestBeforeRepository: StockByBestBeforeRepository,
    private val dailyStockSummaryRepository: DailyStockSummaryRepository,
    private val orderService: OrderService,
    private val wmsApiClient: WmsApiClient
) {
    fun getBasicProductStocks(
        partnerId: Long,
        condition: PredicateSearchCondition,
        page: Pageable
    ): PageResponse<BasicProductStockModel> {
        val basicProducts = basicProductRepository.findAll(condition.toPredicate(), page)
        val basicProductStockModels =
            basicProducts.map {
                val orderCount = orderService.getOrderCountByProductIdAndStatus(
                    it.id!!,
                    OrderStatus.NEW
                ) ?: 0
                BasicProductStockModel.fromBasicProduct(it,orderCount)
            }

        val nosnosStocks = wmsApiClient.getStocks(
            partnerId = partnerId,
            shippingProductIds = basicProductStockModels.content.mapNotNull { it.shippingProductId }
        ).payload?.dataList ?: listOf()

        basicProductStockModels.forEach { model ->
            val nosnosStock =
                nosnosStocks.firstOrNull { it.shippingProductId == model.shippingProductId }
            nosnosStock?.let { model.fillInNosnosStockValues(nosnosStock) }
        }

        return PageResponse.of(basicProductStockModels)
    }

    fun getBasicProductStocks(
        partnerId: Long,
        ids: List<Long>
    ) : List<AvailableStockModel>{
        return basicProductRepository.findAllById(ids.toSet()).map {
            BasicProductAvailableStock(it)
        }.map {
            getNosnosStocks(partnerId, it)
            it.toDto()
        }
    }

    private fun getNosnosStocks(partnerId: Long, basicProductList : BasicProductAvailableStock) {

        val shippingProductIds : List<Long> = if (basicProductList.isPackage)
            basicProductList.subBasicProduct.mapNotNull { it.shippingProductId }
        else
            listOf(basicProductList.baseBasicProduct.shippingProductId!!)

        val stocks = wmsApiClient.getStocks(partnerId, shippingProductIds).payload?.dataList ?: listOf()
        basicProductList.nosnosStockModel.addAll(stocks)
    }

    fun getStocksByBestBefore(
        partnerId: Long,
        condition: StockByBestBeforeSearchCondition,
        page: Pageable
    ): PageResponse<StockByBestBeforeModel> {
        val stocksByBestBefore = stockByBestBeforeRepository.findAll(condition.toPredicate(), page)
        val stockByBestBeforeModel =
            stocksByBestBefore.map { StockByBestBeforeModel.fromStockByBestBefore(it) }

        return PageResponse.of(stockByBestBeforeModel)
    }

    fun getDailyStockSummaries(
        condition: PredicateSearchCondition,
        page: Pageable
    ): PageResponse<DailyStockSummaryModel> {
        val dailyStockSummaries = dailyStockSummaryRepository.findAll(condition.toPredicate(), page)
        val dailyStockSummaryModels = dailyStockSummaries.map { DailyStockSummaryModel.from(it) }

        return PageResponse.of(dailyStockSummaryModels)
    }

    companion object : Log
}
