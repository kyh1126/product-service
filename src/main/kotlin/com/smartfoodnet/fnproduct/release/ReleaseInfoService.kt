package com.smartfoodnet.fnproduct.release

import com.smartfoodnet.common.model.response.PageResponse
import com.smartfoodnet.fnproduct.release.model.request.ReleaseInfoSearchCondition
import com.smartfoodnet.fnproduct.release.model.response.ReleaseInfoDetailModel
import com.smartfoodnet.fnproduct.release.model.response.ReleaseInfoModel
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ReleaseInfoService(
    private val releaseInfoRepository: ReleaseInfoRepository
) {
    fun getReleaseInfos(condition: ReleaseInfoSearchCondition, page: Pageable): PageResponse<ReleaseInfoModel> {
        return releaseInfoRepository.findAllByCondition(condition, page)
            .map(ReleaseInfoModel::fromEntity)
            .run { PageResponse.of(this) }
    }

    fun getReleaseInfo(id: Long): ReleaseInfoDetailModel {
        val releaseInfo = releaseInfoRepository.findById(id).get()
        val collectedOrders = releaseInfo.releaseOrderMappings.map { it.collectedOrder }

        return ReleaseInfoDetailModel.fromEntity(releaseInfo, collectedOrders)
    }
}
