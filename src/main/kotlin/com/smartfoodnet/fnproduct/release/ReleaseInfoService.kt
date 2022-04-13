package com.smartfoodnet.fnproduct.release

import com.smartfoodnet.apiclient.CjDeliveryInfoApiClient
import com.smartfoodnet.apiclient.LotteDeliveryInfoApiClient
import com.smartfoodnet.apiclient.WmsApiClient
import com.smartfoodnet.apiclient.request.LotteDeliveryInfoDto
import com.smartfoodnet.apiclient.request.LotteTrackingDto
import com.smartfoodnet.apiclient.response.CommonDataListModel
import com.smartfoodnet.apiclient.response.NosnosReleaseItemModel
import com.smartfoodnet.apiclient.response.NosnosReleaseModel
import com.smartfoodnet.common.Constants.NOSNOS_INITIAL_PAGE
import com.smartfoodnet.common.error.exception.BaseRuntimeException
import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.common.utils.Log
import com.smartfoodnet.fnproduct.order.ConfirmOrderService
import com.smartfoodnet.fnproduct.order.support.condition.ConfirmProductSearchCondition
import com.smartfoodnet.fnproduct.product.BasicProductService
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.release.entity.ReleaseInfo
import com.smartfoodnet.fnproduct.release.model.request.ReleaseInfoSearchCondition
import com.smartfoodnet.fnproduct.release.model.response.OrderConfirmProductModel
import com.smartfoodnet.fnproduct.release.model.response.OrderProductModel
import com.smartfoodnet.fnproduct.release.model.response.ReleaseInfoModel
import com.smartfoodnet.fnproduct.release.model.vo.DeliveryAgency
import com.smartfoodnet.fnproduct.release.model.vo.DeliveryAgency.Companion.getDeliveryAgencyByName
import com.smartfoodnet.fnproduct.release.model.vo.DeliveryStatus
import com.smartfoodnet.fnproduct.release.model.vo.ReleaseStatus
import com.smartfoodnet.fnproduct.release.model.vo.TrackingNumberStatus
import org.springframework.data.domain.Page
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
    private val confirmOrderService: ConfirmOrderService,
    private val releaseInfoRepository: ReleaseInfoRepository,
    private val wmsApiClient: WmsApiClient,
    private val lotteDeliveryInfoApiClient: LotteDeliveryInfoApiClient,
    private val cjDeliveryInfoApiClient: CjDeliveryInfoApiClient
) {
    fun getReleaseInfoList(
        condition: ReleaseInfoSearchCondition,
        page: Pageable
    ): PageResponse<ReleaseInfoModel> {
        val releaseInfoPage = releaseInfoRepository.findAllByCondition(condition, page)
        val deliveryAgencyModelsByDeliveryAgencyId = getDeliveryAgencyInfoList()
            ?.associateBy { it.deliveryAgencyId!!.toLong() } ?: emptyMap()

        return releaseInfoPage.map {
            ReleaseInfoModel.fromEntity(it, deliveryAgencyModelsByDeliveryAgencyId)
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

    fun getOrderProductsByOrderCode(orderCode: String): List<OrderProductModel> {
        val releaseInfoList = releaseInfoRepository.findByOrderCode(orderCode)
        return releaseInfoList.flatMap(::getOrderProducts)
    }

    fun getOrderProductsByReleaseCode(releaseCode: String): List<OrderProductModel> {
        val releaseInfo = releaseInfoRepository.findByReleaseCode(releaseCode) ?: return emptyList()
        return getOrderProducts(releaseInfo)
    }

    fun getOrderProducts(releaseInfo: ReleaseInfo): List<OrderProductModel> {
        return releaseInfo.releaseProducts.map { OrderProductModel.fromEntity(it, releaseInfo) }
            .ifEmpty {
                val confirmProducts = releaseInfo.confirmOrder?.requestOrderList
                    ?.flatMap { it.collectedOrder.confirmProductList } ?: emptyList()

                confirmProducts.map { it ->
                    OrderProductModel.fromEntity(it, releaseInfo)
                }
            }
    }

    fun getConfirmProducts(partnerId: Long, orderNumber: String): List<OrderConfirmProductModel> {
        val condition = ConfirmProductSearchCondition(partnerId = partnerId, orderNumber = orderNumber)
        val confirmProducts = confirmOrderService.getConfirmProduct(condition)

        return confirmProducts.map(OrderConfirmProductModel::from)
    }

    fun syncDeliveryInfo(deliveryAgency: DeliveryAgency) {
        var page = PageRequest.of(0, 100, Sort.Direction.DESC, "id")

        val idByDeliveryAgency = getDeliveryAgencyInfoList()
            ?.associateBy(
                { getDeliveryAgencyByName(it.deliveryAgencyName) }, { it.deliveryAgencyId!!.toLong() }
            ) ?: emptyMap()

        while (true) {
            val targetList = releaseInfoRepository.findAllByReleaseStatusIn(
                ReleaseStatus.DELIVERY_SYNCABLE_STATUSES,
                page
            )
            if (!targetList.hasContent()) break

            when (deliveryAgency) {
                DeliveryAgency.LOTTE ->
                    updateLotteDeliveryCompletedAt(targetList, idByDeliveryAgency[deliveryAgency])
                DeliveryAgency.CJ ->
                    updateCjDeliveryCompletedAt(targetList, idByDeliveryAgency[deliveryAgency])
            }

            if (targetList.isLast) break

            page = page.next()
        }
    }

    fun registerTrackingNumber() {
        var page = PageRequest.of(0, 100, Sort.Direction.DESC, "id")

        val deliveryAgencyById = getDeliveryAgencyInfoList()
            ?.associateBy(
                { it.deliveryAgencyId!!.toLong() }, { getDeliveryAgencyByName(it.deliveryAgencyName) }
            ) ?: emptyMap()

        while (true) {
            val targetList =
                releaseInfoRepository.findByTrackingNumberStatusAndTrackingNumberIsNotNull(
                    TrackingNumberStatus.BEFORE_REGISTER,
                    page
                )
            if (!targetList.hasContent()) break

            try {
                releaseInfoStoreService.updateTrackingNumberStatus(
                    targetList.content.map { it.id!! },
                    deliveryAgencyById
                )
            } catch (e: BaseRuntimeException) {
                log.error("[registerTrackingNumber] 플레이오토 송장등록 실패," +
                    " releaseIds: ${targetList.map { it.releaseId }}")
            }

            if (targetList.isLast) break

            page = page.next()
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

    private fun getReleaseItems(
        releasesByOrderId: Map<Long, List<NosnosReleaseModel>>
    ): List<NosnosReleaseItemModel> {
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

    private fun getDeliveryAgencyInfoList() =
        wmsApiClient.getDeliveryAgencyInfoList().payload?.dataList

    private fun updateLotteDeliveryCompletedAt(
        targetList: Page<ReleaseInfo>,
        deliveryAgencyId: Long?
    ) {
        val lotteTargetList = targetList.content
            .filter { it.deliveryAgencyId == deliveryAgencyId }
        val request = lotteTargetList
            .map { LotteTrackingDto(it.trackingNumber!!, DeliveryStatus.COMPLETED_LOTTE.code) }
            .run { LotteDeliveryInfoDto(sendParamList = this) }

        try {
            val deliveryInfoByTrackingNumber =
                lotteDeliveryInfoApiClient.getDeliveryInfo(request)?.rtnList
                    ?.associateBy { it.invNo } ?: emptyMap()

            releaseInfoStoreService.updateDeliveryCompletedAt(
                lotteTargetList.map { it.id!! },
                lotteDeliveryInfoByTrackingNumber = deliveryInfoByTrackingNumber
            )
        } catch (e: BaseRuntimeException) {
            log.error("[syncDeliveryInfo] ${DeliveryAgency.LOTTE.playAutoName} 동기화 실패," +
                " releaseIds: ${lotteTargetList.map { it.releaseId }}")
        }
    }

    private fun updateCjDeliveryCompletedAt(
        targetList: Page<ReleaseInfo>,
        deliveryAgencyId: Long?
    ) {
        val cjTargetList = targetList.content.filter { it.deliveryAgencyId == deliveryAgencyId }
        val trackingNumbers = cjTargetList.map { it.trackingNumber!! }

        try {
            val deliveryInfoByTrackingNumber =
                cjDeliveryInfoApiClient.getDeliveryInfo(trackingNumbers)
                    .filter { it.nsDlvNm == DeliveryStatus.COMPLETED_CJ.code }
                    .associateBy { it.invcNo }

            releaseInfoStoreService.updateDeliveryCompletedAt(
                cjTargetList.map { it.id!! },
                cjDeliveryInfoByTrackingNumber = deliveryInfoByTrackingNumber
            )
        } catch (e: BaseRuntimeException) {
            log.error("[syncDeliveryInfo] ${DeliveryAgency.CJ.playAutoName} 동기화 실패," +
                " releaseIds: ${cjTargetList.map { it.releaseId }}")
        }
    }

    companion object : Log
}
