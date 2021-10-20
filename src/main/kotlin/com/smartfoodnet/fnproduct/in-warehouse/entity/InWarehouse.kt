package com.smartfoodnet.fnproduct.warehouse.entity

import com.smartfoodnet.common.entity.BaseEntity
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDateTime
import javax.persistence.*

/**
 * 출고처 엔티티
 */
@Entity
@Table(name = "in_warehouse")
@DynamicUpdate
class InWarehouse(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    var id: Long? = null,

    @Column(name = "partner_id")
    var partnerId: Long?,

    @Column(name = "name", length = 70)
    var name: String?,

    @Column(name = "post_number", length = 6)
    var postNumber: String?,

    @Column(name = "address", length = 70)
    var address: String?,

    @Column(name = "address_detail", length = 70)
    var addressDetail: String?,

    @Column(name = "representative", length = 70)
    var representative: String?,

    @Column(name = "business_number", length = 15)
    var businessNumber: String?,

    @Column(name = "contact_number", length = 50)
    var contactNumber: String?,

    @Column(name = "manager_name", length = 70)
    var managerName: String?,

    @Column(name = "manager_contact_number", length = 50)
    var managerContactNumber: String?,

    @Column(name = "manager_email", length = 70)
    var managerEmail: String?,

    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null,
) : BaseEntity()
