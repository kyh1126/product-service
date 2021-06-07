package com.smartfoodnet.sample.hello

import com.querydsl.jpa.impl.JPAQueryFactory
import com.smartfoodnet.sample.hello.entity.Demo
import com.smartfoodnet.sample.hello.entity.QDemo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager

@Repository
class QDemoRepository(
    val entityManager: EntityManager,
    val jpaQueryFactory: JPAQueryFactory
){
    fun getDemos(): List<Demo> {
        return jpaQueryFactory.selectFrom(QDemo.demo).fetch()
    }
}