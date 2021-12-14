package com.smartfoodnet.fninventory.inbound

import com.smartfoodnet.fninventory.inbound.entity.Inbound
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import java.util.*

interface InboundRepository: JpaRepository<Inbound, Long>, InboundCustom, QuerydslPredicateExecutor<Inbound> {
    @EntityGraph(attributePaths = ["basicProduct"])
    override fun findById(id : Long) : Optional<Inbound>
}