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
                        PACKAGE_NAME -> containsName(condition.searchKeyword, packageProduct)
                        PACKAGE_CODE -> containsCode(condition.searchKeyword, packageProduct)
                        NAME -> containsName(condition.searchKeyword)
                        CODE -> containsCode(condition.searchKeyword)
                        BARCODE -> containsBarcode(condition.searchKeyword)
                        else -> null
                    }
                )
        }
    }

    private fun eqPartnerId(partnerId: Long?) = partnerId?.let { packageProduct.partnerId.eq(it) }

    private fun eqActiveYn(activeYn: String?) = activeYn?.let { packageProduct.activeYn.eq(it) }

    private fun containsBarcode(barcode: String?) =
        barcode?.let { basicProduct.barcode.contains(it) }

    private fun containsName(name: String?, target: QBasicProduct = basicProduct) =
        name?.let { target.name.contains(it) }

    private fun containsCode(code: String?, target: QBasicProduct = basicProduct) =
        code?.let { target.code.contains(it) }


    override fun findAllByBasicProductId(basicProductId: Long): List<PackageProductMapping> {
        return selectFrom(packageProductMapping)
            .innerJoin(packageProductMapping.selectedBasicProduct).fetchJoin()
            .where(packageProductMapping.packageProduct.id.eq(basicProductId))
            .fetch()
    }
}
