package com.smartfoodnet.fnproduct.migration.dto

import com.smartfoodnet.fnproduct.migration.mapper.NosnosModelMapperTestImpl
import com.smartfoodnet.fnproduct.migration.mapper.NosnosShippingProductModelMapper

class NosnosShippingProductTestModel(
    private val modelMapperTestImpl: NosnosModelMapperTestImpl = NosnosModelMapperTestImpl()
) : NosnosShippingProductModelMapper by modelMapperTestImpl
