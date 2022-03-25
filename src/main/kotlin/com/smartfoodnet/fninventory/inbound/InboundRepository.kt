package com.smartfoodnet.fninventory.inbound

import com.smartfoodnet.fninventory.inbound.entity.Inbound
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import java.util.*

interface InboundRepository: JpaRepository<Inbound, Long>, InboundRepositoryCustom, QuerydslPredicateExecutor<Inbound>{
    @EntityGraph(attributePaths = ["expectedList", "expectedList.basicProduct"])
    fun findByRegistrationId(receivingPlanId : Long) : Optional<Inbound>
}