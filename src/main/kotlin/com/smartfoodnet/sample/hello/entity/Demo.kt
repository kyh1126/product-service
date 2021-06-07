package com.smartfoodnet.sample.hello.entity

import javax.persistence.*

@Entity
@Table(name = "demo", schema = "sample", catalog = "sample")
data class Demo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    val id: Int,
    @Column
    val name: String
)
