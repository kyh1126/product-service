package com.smartfoodnet.fnproduct.product.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.smartfoodnet.common.entity.BaseEntity
import com.smartfoodnet.fnproduct.product.model.request.ExpirationDateInfoCreateModel
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "expiration_date_info")
class ExpirationDateInfo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    var id: Long? = null,

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "basic_product_id", columnDefinition = "BIGINT UNSIGNED", nullable = false)
    @JsonIgnore
    var basicProduct: BasicProduct? = null,

    @Column(name = "manufacture_date_write_yn")
    var manufactureDateWriteYn: String = "N",

    @Column(name = "expiration_date_write_yn")
    var expirationDateWriteYn: String = "N",

    @Column(name = "expiration_date")
    var expirationDate: Int?,

    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null,
) : BaseEntity() {
    fun update(request: ExpirationDateInfoCreateModel) {
        manufactureDateWriteYn = request.manufactureDateWriteYn
        expirationDateWriteYn = request.expirationDateWriteYn
        expirationDate = request.expirationDate
    }
}
