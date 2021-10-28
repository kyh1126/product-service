package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.config.Querydsl4RepositorySupport
import com.smartfoodnet.fnproduct.product.entity.PackageProductMapping
import com.smartfoodnet.fnproduct.product.entity.QBasicProduct
import com.smartfoodnet.fnproduct.product.entity.QBasicProduct.basicProduct
import com.smartfoodnet.fnproduct.product.entity.QPackageProductMapping.packageProductMapping
import com.smartfoodnet.fnproduct.product.model.request.PackageProductMappingSearchCondition
import com.smartfoodnet.fnproduct.product.model.request.PackageProductMappingSearchCondition.SearchType.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

class PackageProductMappingRepositoryImpl :
    Querydsl4RepositorySupport(PackageProductMapping::class.java),
    PackageProductMappingCustom {

    private val packageProduct = QBasicProduct("packageProduct")

    override fun findAllByCondition(
        condition: PackageProductMappingSearchCondition,
        page: Pageable
    ): Page<PackageProductMapping> {
        return applyPagination(page) {
            it.selectFrom(packageProductMapping)
                .innerJoin(packageProductMapping.packageProduct, packageProduct)
                .innerJoin(packageProductMapping.selectedBasicProduct, basicProduct)
                .where(
                    eqPartnerId(condition.partnerId),
                    eqActiveYn(condition.activeYn),
                    when (condition.searchType) {
                        PACKAGE_NAME -> eqName(condition.searchKeyword, packageProduct)
                        PACKAGE_CODE -> eqCode(condition.searchKeyword, packageProduct)
                        NAME -> eqName(condition.searchKeyword)
                        CODE -> eqCode(condition.searchKeyword)
                        BARCODE -> eqBarcode(condition.searchKeyword)
                        else -> null
                    }
                )
        }
    }

    private fun eqPartnerId(partnerId: Long?) = partnerId?.let { packageProduct.partnerId.eq(it) }

    private fun eqActiveYn(activeYn: String?) = activeYn?.let { packageProduct.activeYn.eq(it) }

    private fun eqBarcode(barcode: String?) = barcode?.let { basicProduct.barcode.eq(it) }

    private fun eqName(name: String?, target: QBasicProduct = basicProduct) =
        name?.let { target.name.eq(it) }

    private fun eqCode(code: String?, target: QBasicProduct = basicProduct) =
        code?.let { target.code.eq(it) }

}
