package com.smartfoodnet.fnproduct.order.enums

import com.smartfoodnet.fnproduct.order.vo.ShippingMethodType
import javax.persistence.AttributeConverter
import javax.persistence.Converter

enum class DeliveryType(
    val description: String,
    val shippingMethodType: ShippingMethodType
) {
    PARCEL("택배", ShippingMethodType.PARCEL),
    VEHICLE("차량", ShippingMethodType.DIRECT),
    DAWN("새벽배송", ShippingMethodType.DAWN),
    SAME_DAY("당일배송", ShippingMethodType.SAME_DAY);

    companion object {
        fun fromName(name: String?): DeliveryType {
            return values().firstOrNull { it.name == name }
                ?: throw IllegalArgumentException("Format $name is illegal")
        }
    }
}

@Converter
class DeliveryTypeConverter : AttributeConverter<DeliveryType, String> {
    override fun convertToDatabaseColumn(attribute: DeliveryType) = attribute.name
    override fun convertToEntityAttribute(dbData: String) = DeliveryType.fromName(dbData)
}
