package com.smartfoodnet.fninventory.inbound

import com.smartfoodnet.fninventory.inbound.entity.Inbound
import com.smartfoodnet.fninventory.inbound.model.dto.GetInboundActualDetail
import com.smartfoodnet.fninventory.inbound.model.dto.GetInboundParent
import com.smartfoodnet.fninventory.inbound.model.dto.GetInboundSumDetail
import com.smartfoodnet.fninventory.inbound.model.request.InboundSearchCondition
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import java.util.*

interface InboundRepository: JpaRepository<Inbound, Long>, InboundRepositoryCustom, QuerydslPredicateExecutor<Inbound>