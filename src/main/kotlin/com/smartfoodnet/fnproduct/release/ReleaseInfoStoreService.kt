package com.smartfoodnet.fnproduct.release

import com.smartfoodnet.apiclient.WmsApiClient
import com.smartfoodnet.apiclient.response.GetReleaseItemModel
import com.smartfoodnet.apiclient.response.GetReleaseModel
import com.smartfoodnet.fnproduct.release.entity.ReleaseInfo
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ReleaseInfoStoreService(
    private val releaseInfoRepository: ReleaseInfoRepository,
    private val releaseProductRepository: ReleaseProductRepository,
    private val releaseOrderMappingRepository: ReleaseOrderMappingRepository,
    private val wmsApiClient: WmsApiClient,
) {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun updateReleaseInfo(
        releaseInfoList: Page<ReleaseInfo>,
        releasesByOrderId: Map<Long, List<GetReleaseModel>>,
        releaseItems: List<GetReleaseItemModel>
    ) {
        // TODO: releaseInfo, releaseProduct, releaseOrderMapping 업데이트 하기

    }

}
