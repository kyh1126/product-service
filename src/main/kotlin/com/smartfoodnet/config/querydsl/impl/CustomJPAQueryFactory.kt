package com.smartfoodnet.config.querydsl.impl

import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import com.smartfoodnet.config.querydsl.core.CustomQueryMetadata
import javax.persistence.EntityManager

class CustomJPAQueryFactory(private val entityManager: EntityManager) :
    JPAQueryFactory(entityManager) {

    override fun query(): JPAQuery<*> {
        return JPAQuery<Void>(entityManager, CustomQueryMetadata())
    }
}
