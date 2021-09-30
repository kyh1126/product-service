package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.fnproduct.product.entity.SubsidiaryMaterialCategory
import org.springframework.data.jpa.repository.JpaRepository

interface SubsidiaryMaterialCategoryRepository
    : JpaRepository<SubsidiaryMaterialCategory, Long>, SubsidiaryMaterialCategoryCustom
