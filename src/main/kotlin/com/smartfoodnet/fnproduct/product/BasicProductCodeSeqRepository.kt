package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.fnproduct.product.entity.BasicProductCodeSeq
import org.springframework.data.jpa.repository.JpaRepository

interface BasicProductCodeSeqRepository : JpaRepository<BasicProductCodeSeq, Long>
