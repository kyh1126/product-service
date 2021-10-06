package com.smartfoodnet.fnproduct.code.entity

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "code")
class Code(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    var id: Long? = null,

    @Column(name = "group_id")
    var groupId: Int,

    @Column(name = "group_name")
    var groupName: String? = null,

    @Column(name = "text_key")
    var textKey: String? = null,

    @Column(name = "key_id")
    var keyId: Int,

    @Column(name = "key_name")
    var keyName: String,

    @Column(name = "description")
    var description: String? = null,

    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null,
)
