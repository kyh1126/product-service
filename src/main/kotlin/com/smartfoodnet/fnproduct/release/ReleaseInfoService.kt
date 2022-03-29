package com.smartfoodnet.fnproduct.release

import com.smartfoodnet.apiclient.WmsApiClient
import com.smartfoodnet.apiclient.response.CommonDataListModel
import com.smartfoodnet.apiclient.response.NosnosReleaseItemModel
import com.smartfoodnet.apiclient.response.NosnosReleaseModel
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
        val doneOrderIds = mutableSetOf<Long>()

        while (true) {
            val targetList =
                releaseInfoRepository.findAllByReleaseStatusIn(ReleaseStatus.SYNCABLE_STATUSES, page)
            if (!targetList.hasContent()) break

            val orderIds = targetList.filter { it.orderId !in doneOrderIds }
                .map { it.orderId }.toSet()
            if (orderIds.isEmpty()) {
                page = page.next()
                continue
            }

            val releaseInfoListByOrderIdFromTarget = targetList.content
                .groupBy { it.orderId }
            val releasesByOrderIdFromNosnos = getReleases(orderIds = orderIds)
                .groupBy { it.orderId!!.toLong() }
            val itemsByReleaseIdFromNosnos = getReleaseItems(releasesByOrderIdFromNosnos)
                .groupBy { it.releaseId!!.toLong() }

            releaseInfoStoreService.updateReleaseInfo(
                releaseInfoListByOrderIdFromTarget,
                releasesByOrderIdFromNosnos,
                itemsByReleaseIdFromNosnos
            )

            if (targetList.isLast) break

            page = page.next()
            doneOrderIds.addAll(orderIds)
        }
    }

    private fun getReleases(orderIds: Set<Long>): List<NosnosReleaseModel> {
        val releases = mutableListOf<NosnosReleaseModel>()
        var page = nosnosInitialPage
        var totalPage = nosnosInitialPage

        while (page <= totalPage) {
            val model: CommonDataListModel<NosnosReleaseModel>?
            try {
                model = wmsApiClient.getReleases(orderIds = orderIds.toList(), page = page).payload
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

    private fun getReleaseItems(releasesByOrderId: Map<Long, List<NosnosReleaseModel>>): List<NosnosReleaseItemModel> {
        val releaseIds = releasesByOrderId.values.flatten().mapNotNull { it.releaseId?.toLong() }
        val releaseItems = mutableListOf<NosnosReleaseItemModel>()
        var page = nosnosInitialPage
        var totalPage = nosnosInitialPage

        while (page <= totalPage) {
            val model: CommonDataListModel<NosnosReleaseItemModel>?
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
