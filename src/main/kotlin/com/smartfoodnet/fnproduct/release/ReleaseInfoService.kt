package com.smartfoodnet.fnproduct.release

import com.smartfoodnet.apiclient.WmsApiClient
import com.smartfoodnet.apiclient.response.CommonDataListModel
import com.smartfoodnet.apiclient.response.GetReleaseItemModel
import com.smartfoodnet.apiclient.response.GetReleaseModel
import com.smartfoodnet.common.error.exception.BaseRuntimeException
import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.common.utils.Log
import com.smartfoodnet.fnproduct.release.model.request.ReleaseInfoSearchCondition
import com.smartfoodnet.fnproduct.release.model.response.ReleaseInfoDetailModel
import com.smartfoodnet.fnproduct.release.model.response.ReleaseInfoModel
import com.smartfoodnet.fnproduct.release.model.vo.ReleaseStatus
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ReleaseInfoService(
    private val releaseInfoStoreService: ReleaseInfoStoreService,
    private val releaseInfoRepository: ReleaseInfoRepository,
    private val wmsApiClient: WmsApiClient,
) {
    private val nosnosInitialPage = 1

    fun getReleaseInfoList(
        condition: ReleaseInfoSearchCondition,
        page: Pageable
    ): PageResponse<ReleaseInfoModel> {
        return releaseInfoRepository.findAllByCondition(condition, page)
            .map(ReleaseInfoModel::fromEntity)
            .run { PageResponse.of(this) }
    }

    fun getReleaseInfo(id: Long): ReleaseInfoDetailModel {
        val releaseInfo = releaseInfoRepository.findById(id).get()
        val collectedOrders = releaseInfo.releaseOrderMappings.map { it.collectedOrder }

        return ReleaseInfoDetailModel.fromEntity(releaseInfo, collectedOrders)
    }

    fun syncReleaseInfo() {
        var page = PageRequest.of(0, 100, Sort.Direction.DESC, "id")
        val doneOrderIds: MutableSet<Long> = mutableSetOf()

        while (true) {
            val targetList = releaseInfoRepository.findAllByReleaseStatusIn(ReleaseStatus.SYNCABLE_STATUSES, page)
            if (!targetList.hasContent()) break

            val orderIds = targetList.filter { it.orderId !in doneOrderIds }.map { it.orderId }.toList()
            if (orderIds.isEmpty()) {
                page = page.next()
                continue
            }

            val releasesByOrderId = getReleases(orderIds = orderIds).groupBy { it.orderId?.toLong()!! }
            val releaseIds = releasesByOrderId.values.flatten().mapNotNull { it.releaseId?.toLong() }
            val releaseItems = getReleaseItems(releaseIds)

            releaseInfoStoreService.updateReleaseInfo(targetList, releasesByOrderId, releaseItems)

            if (targetList.isLast) break

            page = page.next()
            doneOrderIds.addAll(orderIds)
        }
    }

    private fun getReleases(orderIds: List<Long>): List<GetReleaseModel> {
        val releases: MutableList<GetReleaseModel> = mutableListOf()
        var page = nosnosInitialPage
        var totalPage = nosnosInitialPage

        while (page <= totalPage) {
            val model: CommonDataListModel<GetReleaseModel>?
            try {
                model = wmsApiClient.getReleases(orderIds = orderIds, page = page).payload
            } catch (e: Exception) {
                log.error("orderIds: ${orderIds}, page: ${page}, error: ${e.message}")
                throw BaseRuntimeException(errorMessage = "출고 정보 조회 실패, orderIds: ${orderIds}, page: ${page}")
            }

            if (totalPage == nosnosInitialPage) {
                totalPage = model?.totalPage?.toInt() ?: totalPage
            }
            releases.addAll(model?.dataList ?: emptyList())

            page++
        }
        return releases
    }

    private fun getReleaseItems(releaseIds: List<Long>): List<GetReleaseItemModel> {
        val releaseItems: MutableList<GetReleaseItemModel> = mutableListOf()
        var page = nosnosInitialPage
        var totalPage = nosnosInitialPage

        while (page <= totalPage) {
            val model: CommonDataListModel<GetReleaseItemModel>?
            try {
                model = wmsApiClient.getReleaseItems(releaseIds = releaseIds, page = page).payload
            } catch (e: Exception) {
                log.error("releaseIds: ${releaseIds}, page: ${page}, error: ${e.message}")
                throw BaseRuntimeException(errorMessage = "출고 대상 상품 조회 실패, releaseIds: ${releaseIds}, page: ${page}")
            }

            if (totalPage == nosnosInitialPage) {
                totalPage = model?.totalPage?.toInt() ?: totalPage
            }
            releaseItems.addAll(model?.dataList ?: emptyList())

            page++
        }
        return releaseItems
    }

    companion object : Log
}
