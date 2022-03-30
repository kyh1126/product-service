package com.smartfoodnet.apiclient.response

class UserPartnerInfoModel(
        val partnerId: Long?,
        val rootUserId: Long?,
        val memberId: Long?,
        val partnerCode: String?,
        val tosAgreementYn: String,
        val phoneNo: String?,
        val businessType: String?,
        val businessRegistrationNumber: String?,
        val companyName: String?,
        val businessName: String?,
        val companyCategory: String?,
        val companyAddress: String?,
        val detailAddress: String?,
        val zipCode: String?,
        val ceoName: String?,
        val telNo: String?,
        val businessRegistrationDocumentUrl: String?,
        val desiredService: String?,
        val productCategory: String?,
        val productType: String?,
        val productCount: Int?,
        val expectedMonthlyOutboundCount: String?,
        val etcInfo: String?,
        val updatedAt: String?
)