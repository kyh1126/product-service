package com.smartfoodnet.sample

import com.smartfoodnet.fnproduct.claim.ClaimService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("local")
class CodeTest {
    @Autowired
    lateinit var claimService: ClaimService

    @Test
    fun test() {
        claimService.getReturnInfo()
    }
}
