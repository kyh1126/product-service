package com.smartfoodnet.fnpartner

import com.smartfoodnet.apiclient.PartnerApiClient
import com.smartfoodnet.apiclient.WmsApiClient
import com.smartfoodnet.apiclient.response.UserPartnerInfoModel
import com.smartfoodnet.common.error.exception.BaseRuntimeException
import com.smartfoodnet.common.model.header.SfnMetaUser
import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.common.model.response.CommonResponse
import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.common.utils.Log
import com.smartfoodnet.fninventory.stock.model.BasicProductStockModel
import com.smartfoodnet.fninventory.stock.model.DailyStockSummaryModel
import com.smartfoodnet.fninventory.stock.model.StockByBestBeforeModel
import com.smartfoodnet.fninventory.stock.support.DailyStockSummaryRepository
import com.smartfoodnet.fninventory.stock.support.StockByBestBeforeRepository
import com.smartfoodnet.fninventory.stock.support.StockByBestBeforeSearchCondition
import com.smartfoodnet.fnproduct.order.OrderService
import com.smartfoodnet.fnproduct.order.vo.OrderStatus
import com.smartfoodnet.fnproduct.product.BasicProductRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestParam


@Service
@Transactional(readOnly = true)
class PartnerService(
        private val partnerApiClient: PartnerApiClient
) {

    fun getUserPartnerInfo(@RequestParam userId: Long): UserPartnerInfoModel? {
        val response = partnerApiClient.getUserPartnerInfo(userId)
        return response.payload
    }

    fun checkUserPartnerMembership(sfnMetaUser: SfnMetaUser?, partnerId: Long) {
        val userId = sfnMetaUser?.id
                ?: throw BaseRuntimeException(errorMessage = "사용자 정보가 없습니다(userId).")
        val userPartnerInfo = getUserPartnerInfo(userId)
        if (userPartnerInfo?.partnerId != partnerId) {
            throw BaseRuntimeException(errorMessage = "파트너 정보 검증을 실패 했습니다(partnerId: $partnerId)")
        }
    }

    companion object : Log
}
