package com.smartfoodnet.fnproduct.code

import com.smartfoodnet.fnproduct.code.entity.Code
import org.springframework.data.jpa.repository.JpaRepository

interface CodeRepository : JpaRepository<Code, Long>, CodeCustom
