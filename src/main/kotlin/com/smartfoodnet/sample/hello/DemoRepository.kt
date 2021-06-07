package com.smartfoodnet.sample.hello

import com.smartfoodnet.sample.hello.entity.Demo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DemoRepository : JpaRepository<Demo, Int> {
}