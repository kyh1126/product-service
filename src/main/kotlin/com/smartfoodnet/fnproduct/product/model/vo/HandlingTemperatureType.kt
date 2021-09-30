package com.smartfoodnet.fnproduct.product.model.vo

import javax.persistence.AttributeConverter
import javax.persistence.Converter

enum class HandlingTemperatureType(val desc: String) {
    ROOM("상온"),
    REFRIGERATE("냉장"),
    FREEZE("냉동");

    companion object {
        fun fromName(name: String): HandlingTemperatureType {
            return values().firstOrNull { it.name == name }
                ?: throw IllegalArgumentException("Format $name is illegal")
        }
    }
}

@Converter
class HandlingTemperatureTypeConverter : AttributeConverter<HandlingTemperatureType, String> {
    override fun convertToDatabaseColumn(attribute: HandlingTemperatureType) = attribute.name
    override fun convertToEntityAttribute(dbData: String) = HandlingTemperatureType.fromName(dbData)
}
