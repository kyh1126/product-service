package com.smartfoodnet.sample

import com.smartfoodnet.fninventory.inbound.model.dto.GetInboundParent
import com.smartfoodnet.fninventory.inbound.model.vo.InboundStatusType
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.stream.Collectors
import java.util.stream.Collectors.toList
import kotlin.streams.toList

class CodeTest {

    @Test
    fun mapToInt(){
        val list = listOf(
            GetInboundParent(inboundId = 1, inboundExpectedId = 1, createDate = LocalDateTime.now(), inboundStatusType = InboundStatusType.EXPECTED),
            GetInboundParent(inboundId = 1, inboundExpectedId = 2, createDate = LocalDateTime.now(), inboundStatusType = InboundStatusType.EXPECTED)
        )

        val list1 = list.stream().mapToLong {
            it.inboundExpectedId
        }.toList()

        println(list1)
    }
}