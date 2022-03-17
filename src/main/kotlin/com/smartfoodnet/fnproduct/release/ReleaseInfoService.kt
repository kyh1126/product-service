package com.smartfoodnet.fnproduct.release

import com.smartfoodnet.fnproduct.release.model.response.ReleaseInfoDetailModel
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ReleaseInfoService(
    private val releaseInfoRepository: ReleaseInfoRepository
) {
    fun getReleaseInfo(id: Long): ReleaseInfoDetailModel {
        val releaseInfo = releaseInfoRepository.findById(id).get()
        val collectedOrders = releaseInfo.releaseOrderMappings.map { it.collectedOrder }

        return ReleaseInfoDetailModel.fromEntity(releaseInfo, collectedOrders)
    }
}
