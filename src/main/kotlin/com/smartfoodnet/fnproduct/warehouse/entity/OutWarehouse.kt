package com.smartfoodnet.fnproduct.warehouse.entity

import com.smartfoodnet.common.entity.BaseEntity
import com.smartfoodnet.fnproduct.product.model.vo.DropType
import com.smartfoodnet.fnproduct.product.model.vo.InspectionType
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.Where
import javax.persistence.*

/**
 * 출고처 엔티티
 */
@Entity
@Table(name = "out_warehouse")
@Where(clause = "deleted_at IS NULL")
class OutWarehouse(
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

    @Column(name = "drop_type")
    @Enumerated(EnumType.STRING)
    var dropType: DropType?,

    @Column(name = "inspection_type")
    @Enumerated(EnumType.STRING)
    var inspectionType: InspectionType?,

    @Column(name = "wait_type")
    var waitType: Boolean?,

    @Column(name = "manager_name", length = 70)
    var managerName: String?,

    @Column(name = "manager_contact_number", length = 50)
    var managerContactNumber: String?,

    @Column(name = "manager_email", length = 70)
    var managerEmail: String?,
) : BaseEntity()
