package com.smartfoodnet.fnproduct.product

import com.smartfoodnet.fnproduct.product.entity.BasicProductCodeSeq
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BasicProductCodeSeqRepository : JpaRepository<BasicProductCodeSeq, Long> {
    @Modifying
    @Query("UPDATE BasicProductCodeSeq SET seq = seq + :count WHERE partnerId = :id")
    fun updateSeq(@Param("id") partnerId: Long, @Param("count") count: Int): Int
}
