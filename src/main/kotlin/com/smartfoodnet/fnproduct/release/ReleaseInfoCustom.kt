package com.smartfoodnet.fnproduct.release

import com.smartfoodnet.fnproduct.release.entity.ReleaseInfo
import com.smartfoodnet.fnproduct.release.model.request.ReleaseInfoSearchCondition
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ReleaseInfoCustom {
    fun findAllByCondition(condition: ReleaseInfoSearchCondition, page: Pageable): Page<ReleaseInfo>
}
