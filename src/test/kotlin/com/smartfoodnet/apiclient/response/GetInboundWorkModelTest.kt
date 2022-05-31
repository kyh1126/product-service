package com.smartfoodnet.apiclient.response

import com.smartfoodnet.common.Constants.NOSNOS_DATE_FORMAT
import com.smartfoodnet.common.toLocalDateTime
import com.smartfoodnet.fninventory.inbound.entity.InboundActualDetail
import com.smartfoodnet.fninventory.inbound.entity.InboundExpectedDetail
import com.smartfoodnet.fnproduct.product.entity.BasicProduct
import com.smartfoodnet.fnproduct.product.entity.BoxDimension
import com.smartfoodnet.fnproduct.product.entity.ExpirationDateInfo
import com.smartfoodnet.fnproduct.product.entity.SingleDimension
import com.smartfoodnet.fnproduct.product.model.vo.BasicProductType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class GetInboundWorkModelTest {

    @Nested
    @DisplayName("입고작업 내역에")
    inner class WorkModelTest {
        @Test
        @DisplayName("유통기한이 있고 제조일자가 없다. 제조일로부터 30일")
        fun toEntityForExpireDate() {
            val inboundWorkModel: GetInboundWorkModel =
                GetInboundWorkModel.testModel("20230104", null)
            val basicProduct: BasicProduct = BasicProduct(
                type = BasicProductType.BASIC,
                barcodeYn = "N",
                expirationDateInfo = ExpirationDateInfo("Y", "Y", 30),
                singleDimension = SingleDimension.default,
                boxDimension = BoxDimension.default
            ).also {
                it.expirationDateManagementYn = "Y"
            }
            val inboundExpectedDetail: InboundExpectedDetail = InboundExpectedDetail(
                basicProduct = basicProduct
            )

            val inboundActualDetail: InboundActualDetail =
                inboundWorkModel.toEntity(inboundExpectedDetail, basicProduct)

            assertEquals(
                inboundWorkModel.expireDate.toLocalDateTime(NOSNOS_DATE_FORMAT)?.minusDays(basicProduct.manufactureToExpirationDate()),
                inboundActualDetail.manufactureDate
            )
        }

        @Test
        @DisplayName("유통기한이 없고 제조일자가 있다. 제조일로부터 30일")
        fun toEntityForMakeDate() {
            val inboundWorkModel: GetInboundWorkModel =
                GetInboundWorkModel.testModel(null, "20230104")
            val basicProduct: BasicProduct = BasicProduct(
                type = BasicProductType.BASIC,
                barcodeYn = "N",
                expirationDateInfo = ExpirationDateInfo("Y", "Y", 30),
                singleDimension = SingleDimension.default,
                boxDimension = BoxDimension.default
            ).also {
                it.expirationDateManagementYn = "Y"
            }
            val inboundExpectedDetail: InboundExpectedDetail = InboundExpectedDetail(
                basicProduct = basicProduct
            )

            val inboundActualDetail: InboundActualDetail =
                inboundWorkModel.toEntity(inboundExpectedDetail, basicProduct)

            assertEquals(
                inboundWorkModel.makeDate.toLocalDateTime(NOSNOS_DATE_FORMAT)?.plusDays(basicProduct.manufactureToExpirationDate()),
                inboundActualDetail.expirationDate
            )
        }

        @Test
        @DisplayName("유통기한, 제조일자가 없다")
        fun toEntityNoDate() {
            val inboundWorkModel: GetInboundWorkModel =
                GetInboundWorkModel.testModel(null, null)
            val basicProduct: BasicProduct = BasicProduct(
                type = BasicProductType.BASIC,
                barcodeYn = "N",
                expirationDateInfo = ExpirationDateInfo("Y", "Y", 30),
                singleDimension = SingleDimension.default,
                boxDimension = BoxDimension.default
            ).also {
                it.expirationDateManagementYn = "Y"
            }
            val inboundExpectedDetail: InboundExpectedDetail = InboundExpectedDetail(
                basicProduct = basicProduct
            )

            val inboundActualDetail: InboundActualDetail =
                inboundWorkModel.toEntity(inboundExpectedDetail, basicProduct)

            assertEquals(null, inboundActualDetail.expirationDate)
            assertEquals(null, inboundActualDetail.manufactureDate)
        }

        @Test
        @DisplayName("유통기한을 관리하지 않는다 - 제조일자 입력")
        fun toEntityNoManageExpireDate() {
            val date: String = "20220421"
            val inboundWorkModel: GetInboundWorkModel =
                GetInboundWorkModel.testModel(null, date)
            val basicProduct: BasicProduct = BasicProduct(
                type = BasicProductType.BASIC,
                barcodeYn = "N",
                expirationDateInfo = ExpirationDateInfo("Y", "N", 30),
                singleDimension = SingleDimension.default,
                boxDimension = BoxDimension.default
            ).also {
                it.expirationDateManagementYn = "Y"
            }
            val inboundExpectedDetail: InboundExpectedDetail = InboundExpectedDetail(
                basicProduct = basicProduct
            )

            val inboundActualDetail: InboundActualDetail =
                inboundWorkModel.toEntity(inboundExpectedDetail, basicProduct)

            assertEquals(null, inboundActualDetail.expirationDate)
            assertEquals(date.toLocalDateTime(NOSNOS_DATE_FORMAT), inboundActualDetail.manufactureDate)
        }

        @Test
        @DisplayName("제조일자를 관리하지 않는다 - 유통기한 입력")
        fun toEntityNoManageMakeDate() {
            val date: String = "20220421"
            val inboundWorkModel: GetInboundWorkModel =
                GetInboundWorkModel.testModel(date, null)
            val basicProduct: BasicProduct = BasicProduct(
                type = BasicProductType.BASIC,
                barcodeYn = "N",
                expirationDateInfo = ExpirationDateInfo("N", "Y", 30),
                singleDimension = SingleDimension.default,
                boxDimension = BoxDimension.default
            ).also {
                it.expirationDateManagementYn = "Y"
            }
            val inboundExpectedDetail: InboundExpectedDetail = InboundExpectedDetail(
                basicProduct = basicProduct
            )

            val inboundActualDetail: InboundActualDetail =
                inboundWorkModel.toEntity(inboundExpectedDetail, basicProduct)

            assertEquals(date.toLocalDateTime(NOSNOS_DATE_FORMAT), inboundActualDetail.expirationDate)
            assertEquals(null, inboundActualDetail.manufactureDate)
        }

        @Test
        @DisplayName("유통기한 관리여부를 사용하지 않는다")
        fun toEntityNoManageAll() {
            val date: String = "20220421"
            val inboundWorkModel: GetInboundWorkModel =
                GetInboundWorkModel.testModel(date, date)
            val basicProduct: BasicProduct = BasicProduct(
                type = BasicProductType.BASIC,
                barcodeYn = "N",
                expirationDateInfo = null,
                singleDimension = SingleDimension.default,
                boxDimension = BoxDimension.default
            ).also {
                it.expirationDateManagementYn = "N"
            }
            val inboundExpectedDetail: InboundExpectedDetail = InboundExpectedDetail(
                basicProduct = basicProduct
            )

            val inboundActualDetail: InboundActualDetail =
                inboundWorkModel.toEntity(inboundExpectedDetail, basicProduct)

            assertEquals(null, inboundActualDetail.expirationDate)
            assertEquals(null, inboundActualDetail.manufactureDate)
        }

    }
}
