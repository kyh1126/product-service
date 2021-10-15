package com.smartfoodnet.fnproduct.partner

import com.smartfoodnet.fnproduct.partner.model.response.PartnerModel
import com.smartfoodnet.fnproduct.product.PartnerRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PartnerService(private val partnerRepository: PartnerRepository) {

    fun getPartner(partnerId: Long): PartnerModel {
        val partner = partnerRepository.findById(partnerId).get()
        return PartnerModel.fromEntity(partner)
    }
}
