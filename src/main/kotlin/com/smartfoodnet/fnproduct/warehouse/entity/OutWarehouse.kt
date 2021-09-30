package com.smartfoodnet.fnproduct.warehouse.entity

import com.smartfoodnet.fnproduct.product.model.vo.DropType
import com.smartfoodnet.fnproduct.product.model.vo.DropTypeConverter
import com.smartfoodnet.fnproduct.product.model.vo.InspectionType
import com.smartfoodnet.fnproduct.product.model.vo.InspectionTypeConverter
import javax.persistence.*

/**
 * 출고처 엔티티
 */
@Entity
@Table(name = "out_warehouse")
class OutWarehouse (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long,

    @Column(name = "partner_id")
    var partnerId : Long,

    @Column(name = "name", length = 70)
    var name : String,

    @Column(name = "post_number", length = 6)
    var postNumber : String,

    @Column(name = "address", length = 70)
    var address : String,

    @Column(name = "address_detail", length = 70)
    var addressDetail : String,

    @Column(name = "representative", length = 70)
    var representative : String,

    @Column(name = "business_number", length = 15)
    var businessNumber : String,

    @Column(name = "contact_number", length = 50)
    var contactNumber : String,

    @Column(name = "drop_type")
    @Enumerated(EnumType.STRING)
    var dropType : DropType,

    @Column(name = "inspection_type")
    @Enumerated(EnumType.STRING)
    var inspectionType : InspectionType,

    @Column(name = "wait_type")
    var waitType : Boolean,

    @Column(name = "manager_name", length = 70)
    var managerName : String,

    @Column(name = "manager_contact_number", length = 50)
    var managerContactNumber : String,

    @Column(name = "manager_email", length = 70)
    var managerEmail : String
)