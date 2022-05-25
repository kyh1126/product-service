package com.smartfoodnet.fninventory.stock.support

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate
import com.smartfoodnet.common.Constants
import com.smartfoodnet.common.model.request.PredicateSearchCondition
import com.smartfoodnet.fninventory.stock.entity.QStockByBestBefore.stockByBestBefore
import com.smartfoodnet.fninventory.stock.support.StockByBestBeforeSearchCondition.SearchType.*
import io.swagger.annotations.ApiModelProperty
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

class StockByBestBeforeSearchCondition(
    @ApiModelProperty(hidden = true)
    var partnerId: Long? = null,

    /*--상미기한별 조회 임시 주석
    @ApiModelProperty(value = "상미기한 from")
    val rangeBestBeforeFrom: Int? = null,

    @ApiModelProperty(value = "상미기한 to(0 검색일 경우 이 파라미터에만 0)")
    val rangeBestBeforeTo: Int? = null,
    */

    @DateTimeFormat(pattern = Constants.DATE_FORMAT)
    @ApiModelProperty(value = "유통기한 from", example = "2021-12-03")
    val expirationDateFrom: LocalDate? = null,

    @DateTimeFormat(pattern = Constants.DATE_FORMAT)
    @ApiModelProperty(value = "유통기한 to", example = "2021-12-04")
    val expirationDateTo: LocalDate? = null,

    ) : PredicateSearchCondition() {

    @ApiModelProperty(
        value = "상품별검색 (NAME:기본상품명/CODE:기본상품코드/BARCODE:상품바코드)",
        allowableValues = "NAME,CODE,BARCODE"
    )
    val searchType: SearchType? = null

    @ApiModelProperty(value = "검색 키워드")
    val searchKeyword: String? = null

    enum class SearchType {
        NAME, CODE, BARCODE
    }

    override fun assemblePredicate(predicate: BooleanBuilder): Predicate {
        return predicate.orAllOf(
            /* --상미기한별 조회 임시 주석
            notExistExpirationDate(),
            */
            eqCollectDateToday(),
            eqPartnerId(partnerId),
            searchType?.let { toPredicate(it) },
            goeExpirationDateFrom(expirationDateFrom),
            ltExpirationDateTo(expirationDateTo)
            /* --상미기한별 조회 임시 주석
            betweenBestBeforeFromTo(rangeBestBeforeFrom, rangeBestBeforeTo)
             */
        )
    }

    private fun toPredicate(searchType: SearchType) =
        when (searchType) {
            NAME -> containBasicProductName(searchKeyword)
            CODE -> containBasicProductCode(searchKeyword)
            BARCODE -> containBarcode(searchKeyword)
        }

   /*--상미기한별 조회 임시 주석
   private fun notExistExpirationDate() =
        stockByBestBefore.bestBefore.ne(-1f)
   */

    private fun eqCollectDateToday()=
        stockByBestBefore.collectedDate.eq(LocalDate.now())

    private fun eqPartnerId(partnerId: Long?) = partnerId?.let { stockByBestBefore.partnerId.eq(it) }

    private fun containBasicProductName(basicProductName: String?) =
        basicProductName?.let { stockByBestBefore.basicProduct.name.contains(it) }

    private fun containBasicProductCode(basicProductCode: String?) =
        basicProductCode?.let { stockByBestBefore.basicProduct.code.contains(it) }

    private fun containBarcode(barcode: String?) =
        barcode?.let { stockByBestBefore.basicProduct.barcode.contains(it) }

    private fun eqExpirationDateManagementYn(expirationDateManagementYn: String?) =
        expirationDateManagementYn?.let { stockByBestBefore.basicProduct.expirationDateManagementYn.eq(it) }

    /*--상미기한별 조회 임시 주석
    private fun betweenBestBeforeFromTo(rangeBestBeforeFrom: Int?, rangeBestBeforeTo: Int?) =
        if (rangeBestBeforeTo != 0) {
            safeLet(rangeBestBeforeFrom, rangeBestBeforeTo) { from, to ->
                stockByBestBefore.bestBefore.between(from, to)
            }
        } else {
            rangeBestBeforeTo.let { stockByBestBefore.bestBefore.eq(it.toFloat()) }
        }
    */
    private fun goeExpirationDateFrom(expirationDateFrom: LocalDate?) =
        expirationDateFrom?.let {
            stockByBestBefore.expirationDate.goe(it.atStartOfDay())
        }

    private fun ltExpirationDateTo(expirationDateTo: LocalDate?) =
        expirationDateTo?.let {
            stockByBestBefore.expirationDate.lt(it.plusDays(1).atStartOfDay())
        }

    private inline fun <T1 : Any, T2 : Any, R : Any> safeLet(p1: T1?, p2: T2?, block: (T1, T2) -> R?): R? {
        return if (p1 != null && p2 != null) block(p1, p2) else null
    }
}