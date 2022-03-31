package com.smartfoodnet.fnproduct.release

import com.smartfoodnet.apiclient.WmsApiClient
import com.smartfoodnet.apiclient.response.CommonDataListModel
import com.smartfoodnet.apiclient.response.NosnosReleaseItemModel
import com.smartfoodnet.apiclient.response.NosnosReleaseModel
import com.smartfoodnet.common.Constants.NOSNOS_INITIAL_PAGE
import com.smartfoodnet.common.error.exception.BaseRuntimeException
import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.common.utils.Log
import com.smartfoodnet.fnproduct.product.BasicProductService
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.release.model.request.ReleaseInfoSearchCondition
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
    private val basicProductService: BasicProductService,
    private val releaseInfoRepository: ReleaseInfoRepository,
    private val wmsApiClient: WmsApiClient,
) {
    fun getReleaseInfoList(
        condition: ReleaseInfoSearchCondition,
        page: Pageable
    ): PageResponse<ReleaseInfoModel> {
        val releaseInfoPage = releaseInfoRepository.findAllByCondition(condition, page)
        val deliveryAgencyModelsByDeliveryAgencyId =
            wmsApiClient.getDeliveryAgencyInfoList().payload?.dataList
                ?.associateBy { it.deliveryAgencyId!!.toLong() } ?: emptyMap()

        return releaseInfoPage.map { it ->
            val collectedOrders = it.confirmOrder?.requestOrderList
                ?.map { it.collectedOrder } ?: emptyList()
            ReleaseInfoModel.fromEntity(it, collectedOrders, deliveryAgencyModelsByDeliveryAgencyId)
        }.run { PageResponse.of(this) }
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

            try {
                val releaseModelsByOrderId = getReleases(orderIds = orderIds)
                    .groupBy { it.orderId!!.toLong() }
                val itemModelsByReleaseId = getReleaseItems(releaseModelsByOrderId)
                    .groupBy { it.releaseId!!.toLong() }
                val basicProductByShippingProductId = getBasicProductBy(itemModelsByReleaseId)

                releaseModelsByOrderId.entries.forEach { (orderId, releaseModels) ->
                    updateReleaseInfo(
                        orderId,
                        releaseModels,
                        itemModelsByReleaseId,
                        basicProductByShippingProductId
                    )
                }
            } catch (e: BaseRuntimeException) {
                log.error("[syncReleaseInfo] orderIds: ${orderIds} 동기화 실패")
            }

            if (targetList.isLast) break

            page = page.next()
            doneOrderIds.addAll(orderIds)
        }
    }

    private fun getReleases(orderIds: Set<Long>): List<NosnosReleaseModel> {
        val releases = mutableListOf<NosnosReleaseModel>()
        var page = NOSNOS_INITIAL_PAGE
        var totalPage = NOSNOS_INITIAL_PAGE

        while (page <= totalPage) {
            val model: CommonDataListModel<NosnosReleaseModel>?
            try {
                model = wmsApiClient.getReleases(orderIds = orderIds.toList(), page = page).payload
            } catch (e: Exception) {
                log.error("[getReleases] orderIds: ${orderIds}, page: ${page}, error: ${e.message}")
                throw BaseRuntimeException(errorMessage = "출고 정보 조회 실패, orderIds: ${orderIds}, page: ${page}")
            }

            if (totalPage == NOSNOS_INITIAL_PAGE) {
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
        var page = NOSNOS_INITIAL_PAGE
        var totalPage = NOSNOS_INITIAL_PAGE

        while (page <= totalPage) {
            val model: CommonDataListModel<NosnosReleaseItemModel>?
            try {
                model = wmsApiClient.getReleaseItems(releaseIds = releaseIds, page = page).payload
            } catch (e: Exception) {
                log.error("[getReleaseItems] releaseIds: ${releaseIds}, page: ${page}, error: ${e.message}")
                throw BaseRuntimeException(errorMessage = "출고 대상 상품 조회 실패, releaseIds: ${releaseIds}, page: ${page}")
            }

            if (totalPage == NOSNOS_INITIAL_PAGE) {
                totalPage = model?.totalPage?.toInt() ?: totalPage
            }
            releaseItems.addAll(model?.dataList ?: emptyList())

            page++
        }
        return releaseItems
    }

    private fun getBasicProductBy(
        itemsByReleaseId: Map<Long, List<NosnosReleaseItemModel>>
    ): Map<Long, BasicProduct> {
        val shippingProductIdsFromModel = itemsByReleaseId.values.flatten()
            .mapNotNull { it.shippingProductId?.toLong() }.toSet()
        return basicProductService.getBasicProductsByShippingProductIds(shippingProductIdsFromModel)
            .associateBy { it.shippingProductId!! }
    }

    private fun updateReleaseInfo(
        orderId: Long,
        releaseModels: List<NosnosReleaseModel>,
        itemModelsByReleaseId: Map<Long, List<NosnosReleaseItemModel>>,
        basicProductByShippingProductId: Map<Long, BasicProduct>
    ) {
        val releaseInfoListByOrderId = releaseInfoRepository.findByOrderId(orderId)
        try {
            releaseInfoStoreService.createOrUpdateReleaseInfo(
                releaseModels,
                itemModelsByReleaseId,
                basicProductByShippingProductId,
                releaseInfoListByOrderId
            )
        } catch (e: BaseRuntimeException) {
            log.error("orderId: ${orderId} releaseInfo 동기화 실패")
        }
    }

    companion object : Log
}
