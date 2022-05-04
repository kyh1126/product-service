package com.smartfoodnet.fnproduct.release

import com.smartfoodnet.apiclient.LotteDeliveryInfoApiClient
import com.smartfoodnet.apiclient.WmsApiClient
import com.smartfoodnet.apiclient.request.LotteDeliveryInfoDto
import com.smartfoodnet.apiclient.request.LotteTrackingDto
import com.smartfoodnet.apiclient.response.CommonDataListModel
import com.smartfoodnet.apiclient.response.NosnosReleaseItemModel
import com.smartfoodnet.apiclient.response.NosnosReleaseModel
import com.smartfoodnet.common.Constants.NOSNOS_INITIAL_PAGE
import com.smartfoodnet.common.Constants.NOSNOS_NO_VALUE_MESSAGE
import com.smartfoodnet.common.error.exception.BaseRuntimeException
import com.smartfoodnet.common.getNosnosErrorMessage
import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.common.utils.Log
import com.smartfoodnet.fnproduct.order.ConfirmOrderService
import com.smartfoodnet.fnproduct.order.entity.ConfirmProduct
import com.smartfoodnet.fnproduct.order.support.condition.ConfirmProductSearchCondition
import com.smartfoodnet.fnproduct.product.BasicProductService
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.release.entity.ReleaseInfo
import com.smartfoodnet.fnproduct.release.model.dto.OrderReleaseInfoDto
import com.smartfoodnet.fnproduct.release.model.request.ReOrderCreateModel
import com.smartfoodnet.fnproduct.release.model.request.ReleaseInfoSearchCondition
import com.smartfoodnet.fnproduct.release.model.request.ReleaseStatusSearchCondition
import com.smartfoodnet.fnproduct.release.model.response.*
import com.smartfoodnet.fnproduct.release.model.vo.DeliveryAgency
import com.smartfoodnet.fnproduct.release.model.vo.DeliveryAgency.Companion.getDeliveryAgencyByName
import com.smartfoodnet.fnproduct.release.model.vo.DeliveryStatus
import com.smartfoodnet.fnproduct.release.model.vo.ReleaseStatus
import com.smartfoodnet.fnproduct.release.model.vo.TrackingNumberStatus
import feign.FeignException
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
    private val manualReleaseService: ManualReleaseService,
    private val releaseInfoRepository: ReleaseInfoRepository,
    private val wmsApiClient: WmsApiClient,
    private val lotteDeliveryInfoApiClient: LotteDeliveryInfoApiClient,
) {
    fun getReleaseInfoList(
        condition: ReleaseInfoSearchCondition,
        page: Pageable
    ): PageResponse<ReleaseInfoModel> {
        val releaseInfoPage = releaseInfoRepository.findAllByCondition(condition, page)
        val deliveryAgencyModelsByDeliveryAgencyId = getDeliveryAgencyInfoList()
            ?.associateBy { it.deliveryAgencyId!! } ?: emptyMap()

        return releaseInfoPage.map {
            ReleaseInfoModel.fromEntity(it, deliveryAgencyModelsByDeliveryAgencyId)
        }.run { PageResponse.of(this) }
    }

    fun getPausedReleaseInfoList(
        condition: ReleaseInfoSearchCondition,
        page: Pageable
    ): PageResponse<PausedReleaseInfoModel> {
        val releaseInfoPage = releaseInfoRepository.findAllByCondition(condition, page)
        return releaseInfoPage.map(PausedReleaseInfoModel::fromEntity)
            .run { PageResponse.of(this) }
    }

    fun syncReleaseInfo(partnerId: Long?) {
        log.info("[syncReleaseInfo] partnerId: $partnerId start!")

        var page = PageRequest.of(0, 100, Sort.Direction.DESC, "id")
        val doneOrderIds = mutableSetOf<Long>()

        while (true) {
            val targetList = releaseInfoRepository.findAllByReleaseStatuses(
                condition = ReleaseStatusSearchCondition(
                    partnerId = partnerId,
                    releaseStatuses = ReleaseStatus.SYNCABLE_STATUSES,
                ),
                page = page
            )

            if (!targetList.hasContent()) break

            val orderIds = targetList.filter { it.orderId !in doneOrderIds }
                .map { it.orderId }.toSet()
            if (orderIds.isEmpty()) {
                page = page.next()
                continue
            }

            try {
                val releaseModelsByOrderId = getReleases(partnerId = partnerId, orderIds = orderIds)
                    .groupBy { it.orderId!! }
                val itemModelsByReleaseId = getReleaseItems(releaseModelsByOrderId)
                    .groupBy { it.releaseId!! }
                val basicProductByShippingProductId = getBasicProductBy(itemModelsByReleaseId)

                releaseModelsByOrderId.entries.forEach { (orderId, releaseModels) ->
                    updateReleaseInfo(
                        orderId,
                        releaseModels,
                        itemModelsByReleaseId,
                        basicProductByShippingProductId
                    )
                }
            } catch (e: RuntimeException) {
                log.error("[syncReleaseInfo] orderIds: ${orderIds} 동기화 실패", e)
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

    fun getPausedOrderProductsByOrderCode(orderCode: String): List<PausedOrderProductModel> {
        val releaseInfoList = releaseInfoRepository.findAllByReleaseStatuses(
            ReleaseStatusSearchCondition(
                orderCode = orderCode,
                releaseStatuses = setOf(ReleaseStatus.RELEASE_PAUSED)
            ),
        )
        return releaseInfoList.flatMap(::getPausedOrderProducts)
    }

    fun getPausedOrderProductsByReleaseCode(releaseCode: String): List<PausedOrderProductModel> {
        val releaseInfo = releaseInfoRepository.findAllByReleaseStatuses(
            ReleaseStatusSearchCondition(
                releaseCode = releaseCode,
                releaseStatuses = setOf(ReleaseStatus.RELEASE_PAUSED)
            ),
        ).first()
        return getPausedOrderProducts(releaseInfo)
    }

    @java.lang.Deprecated(forRemoval = true)
    fun getOrderProductsOld(releaseInfo: ReleaseInfo): List<OrderProductModel> {
        return releaseInfo.releaseProducts
            .map { OrderProductModel.fromEntity(it, releaseInfo) }
            .ifEmpty {
                val confirmProducts = getConfirmProducts(releaseInfo)
                confirmProducts.map { OrderProductModel.fromEntity(it, releaseInfo) }
            }
    }

    fun getOrderProducts(releaseInfo: ReleaseInfo): List<OrderProductModel> {
        return when (releaseInfo.trackingNumber) {
            null -> {
                val confirmProducts = getConfirmProducts(releaseInfo)
                confirmProducts.map { OrderProductModel.fromEntity(it, releaseInfo) }
            }
            else -> releaseInfo.releaseProducts.map { OrderProductModel.fromEntity(it, releaseInfo) }
        }
    }

    fun getPausedOrderProducts(releaseInfo: ReleaseInfo): List<PausedOrderProductModel> {
        return releaseInfo.releaseProducts
            .map { PausedOrderProductModel.fromEntity(it, releaseInfo) }
            .ifEmpty {
                val confirmProducts = getConfirmProducts(releaseInfo)
                confirmProducts.map { PausedOrderProductModel.fromEntity(it, releaseInfo) }
            }
    }

    fun getConfirmProducts(partnerId: Long, orderNumber: String): List<OrderConfirmProductModel> {
        val condition = ConfirmProductSearchCondition(partnerId = partnerId, orderNumber = orderNumber)
        val confirmProducts = confirmOrderService.getConfirmProduct(condition)

        return confirmProducts.map(OrderConfirmProductModel::from)
    }

    fun syncDeliveryInfo(deliveryAgency: DeliveryAgency?) {
        log.info("[syncDeliveryInfo] deliveryAgency: $deliveryAgency start!")

        var page = PageRequest.of(0, 100, Sort.Direction.DESC, "id")

        val idByDeliveryAgency = getDeliveryAgencyInfoList()
            ?.associateBy(
                { getDeliveryAgencyByName(it.deliveryAgencyName) }, { it.deliveryAgencyId!! }
            ) ?: emptyMap()

        while (true) {
            val targetList = getDeliverySyncableReleaseInfoList(deliveryAgency, page, idByDeliveryAgency)

            if (!targetList.hasContent()) break

            when (deliveryAgency) {
                DeliveryAgency.LOTTE ->
                    updateLotteDeliveryCompletedAt(targetList, idByDeliveryAgency[deliveryAgency])
                DeliveryAgency.CJ ->
                    updateCjDeliveryCompletedAt(targetList, idByDeliveryAgency[deliveryAgency])
                null -> {
                    updateLotteDeliveryCompletedAt(targetList, idByDeliveryAgency[DeliveryAgency.LOTTE])
                    updateCjDeliveryCompletedAt(targetList, idByDeliveryAgency[DeliveryAgency.CJ])
                }
            }

            if (targetList.isLast) break

            page = page.next()
        }
    }

    fun registerTrackingNumber() {
        var page = PageRequest.of(0, 100, Sort.Direction.DESC, "id")

        val deliveryAgencyById = getDeliveryAgencyInfoList()
            ?.associateBy(
                { it.deliveryAgencyId!! }, { getDeliveryAgencyByName(it.deliveryAgencyName) }
            ) ?: emptyMap()

        while (true) {
            val targetList =
                releaseInfoRepository.findAllByTrackingNumberStatus(
                    TrackingNumberStatus.BEFORE_REGISTER,
                    true,
                    page
                )
            if (!targetList.hasContent()) break

            try {
                releaseInfoStoreService.updateTrackingNumberStatus(
                    targetList.content.map { it.id!! },
                    deliveryAgencyById
                )
            } catch (e: RuntimeException) {
                log.error("[registerTrackingNumber] 플레이오토 송장등록 실패," +
                    " releaseIds: ${targetList.map { it.releaseId }}", e)
            }

            if (targetList.isLast) break

            page = page.next()
        }
    }

    fun reOrder(id: Long, createModel: ReOrderCreateModel) {
        val releaseInfo = releaseInfoRepository.findById(id).get()
        manualReleaseService.reOrder(id, releaseInfo.partnerId, createModel)
    }

    private fun getReleases(partnerId: Long?, orderIds: Set<Long>): List<NosnosReleaseModel> {
        val releases = mutableListOf<NosnosReleaseModel>()
        var page = NOSNOS_INITIAL_PAGE
        var totalPage = NOSNOS_INITIAL_PAGE

        while (page <= totalPage) {
            var model: CommonDataListModel<NosnosReleaseModel>? = null
            try {
                model = wmsApiClient.getReleases(
                    partnerId = partnerId,
                    orderIds = orderIds.toList(),
                    page = page
                ).payload
            } catch (e: FeignException) {
                if (NOSNOS_NO_VALUE_MESSAGE == getNosnosErrorMessage(e.message)) {
                    log.warn("[getReleases] ${NOSNOS_NO_VALUE_MESSAGE} orderIds: ${orderIds}, page: ${page}", e)
                } else {
                    log.error("[getReleases] orderIds: ${orderIds}, page: ${page}", e)
                    throw BaseRuntimeException(errorMessage = "출고 정보 조회 실패, orderIds: ${orderIds}, page: ${page}")
                }
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
        val releaseIds = releasesByOrderId.values.flatten().mapNotNull { it.releaseId }
        val releaseItems = mutableListOf<NosnosReleaseItemModel>()
        var page = NOSNOS_INITIAL_PAGE
        var totalPage = NOSNOS_INITIAL_PAGE

        if (releaseIds.isEmpty()) return emptyList()

        while (page <= totalPage) {
            val model: CommonDataListModel<NosnosReleaseItemModel>?
            try {
                model = wmsApiClient.getReleaseItems(releaseIds = releaseIds, page = page).payload
            } catch (e: FeignException) {
                log.error("[getReleaseItems] releaseIds: ${releaseIds}, page: ${page}", e)
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
            .mapNotNull { it.shippingProductId }.toSet()

        if (shippingProductIdsFromModel.isEmpty()) return emptyMap()

        return basicProductService.getBasicProductsByShippingProductIds(shippingProductIdsFromModel)
            .associateBy { it.shippingProductId!! }
    }

    private fun getConfirmProducts(releaseInfo: ReleaseInfo): List<ConfirmProduct> {
        return releaseInfo.confirmOrder?.requestOrderList
            ?.flatMap { it.collectedOrder.confirmProductList } ?: emptyList()
    }

    private fun updateReleaseInfo(
        orderId: Long,
        releaseModels: List<NosnosReleaseModel>,
        itemModelsByReleaseId: Map<Long, List<NosnosReleaseItemModel>>,
        basicProductByShippingProductId: Map<Long, BasicProduct>
    ) {
        val targetReleaseInfoList = releaseInfoRepository.findByOrderId(orderId)
        val confirmOrder = confirmOrderService.getConfirmOrderByOrderId(orderId)
        val orderReleaseInfoDto = OrderReleaseInfoDto(confirmOrder, targetReleaseInfoList)

        try {
            releaseInfoStoreService.createOrUpdateReleaseInfo(
                releaseModels,
                itemModelsByReleaseId,
                basicProductByShippingProductId,
                orderReleaseInfoDto,
            )
        } catch (e: RuntimeException) {
            log.error("orderId: ${orderId} releaseInfo 동기화 실패", e)
        }
    }

    private fun getDeliveryAgencyInfoList() =
        wmsApiClient.getDeliveryAgencyInfoList().payload?.dataList

    private fun getDeliverySyncableReleaseInfoList(
        deliveryAgency: DeliveryAgency?,
        page: PageRequest,
        idByDeliveryAgency: Map<DeliveryAgency?, Long>
    ) = when (deliveryAgency) {
        null -> releaseInfoRepository.findAllByReleaseStatuses(
            condition = ReleaseStatusSearchCondition(
                releaseStatuses = ReleaseStatus.DELIVERY_SYNCABLE_STATUSES
            ),
            page = page
        )
        else -> releaseInfoRepository.findAllByReleaseStatuses(
            condition = ReleaseStatusSearchCondition(
                deliveryAgencyId = idByDeliveryAgency[deliveryAgency],
                releaseStatuses = ReleaseStatus.DELIVERY_SYNCABLE_STATUSES
            ),
            page = page
        )
    }

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
                    ?.filter { it.invNo != null }
                    ?.associateBy { it.invNo!! } ?: emptyMap()

            releaseInfoStoreService.updateDeliveryCompletedAt(
                lotteTargetList.map { it.id!! },
                lotteDeliveryInfoByTrackingNumber = deliveryInfoByTrackingNumber
            )
        } catch (e: RuntimeException) {
            log.error("[syncDeliveryInfo] ${DeliveryAgency.LOTTE.playAutoName} 동기화 실패," +
                " releaseIds: ${lotteTargetList.map { it.releaseId }}", e)
        }
    }

    private fun updateCjDeliveryCompletedAt(
        targetList: Page<ReleaseInfo>,
        deliveryAgencyId: Long?
    ) {
        val cjTargetList = targetList.content.filter { it.deliveryAgencyId == deliveryAgencyId }
        val trackingNumbers = cjTargetList.map { it.trackingNumber!! }

        if (trackingNumbers.isEmpty()) return

        try {
            val deliveryInfoByTrackingNumber =
                wmsApiClient.getCjDeliveryStatuses(trackingNumbers).payload?.dataList
                    ?.filter { it.deliveryStatus == DeliveryStatus.COMPLETED_CJ.desc }
                    ?.associateBy { it.trackingNumber } ?: emptyMap()

            releaseInfoStoreService.updateDeliveryCompletedAt(
                cjTargetList.map { it.id!! },
                cjDeliveryInfoByTrackingNumber = deliveryInfoByTrackingNumber
            )
        } catch (e: RuntimeException) {
            log.error("[syncDeliveryInfo] ${DeliveryAgency.CJ.playAutoName} 동기화 실패," +
                " releaseIds: ${cjTargetList.map { it.releaseId }}", e)
        }
    }

    companion object : Log
}
