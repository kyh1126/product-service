package com.smartfoodnet.fnproduct.product.model.vo

import javax.persistence.AttributeConverter
import javax.persistence.Converter

enum class HandlingTemperatureType(val desc: String, val code: String) {
    ROOM("상온", "A"),
    REFRIGERATE("냉장", "C"),
    FREEZE("냉동", "B");

    companion object {
        fun fromName(name: String?): HandlingTemperatureType? {
            return values().firstOrNull { it.name == name }
        }
    }
}

@Converter
class HandlingTemperatureTypeConverter : AttributeConverter<HandlingTemperatureType, String> {
    override fun convertToDatabaseColumn(attribute: HandlingTemperatureType?) = attribute?.name
    override fun convertToEntityAttribute(dbData: String?) = HandlingTemperatureType.fromName(dbData)
}
