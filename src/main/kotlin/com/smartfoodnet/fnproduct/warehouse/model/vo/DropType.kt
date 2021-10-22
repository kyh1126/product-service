package com.smartfoodnet.fnproduct.product.model.vo

import javax.persistence.AttributeConverter
import javax.persistence.Converter

enum class DropType(val desc: String) {
    FORKLIFT("지게차"),
    HANDWORK("수작업");

    companion object {
        fun forName(name: String): DropType {
            return values().firstOrNull { it.name == name }
                ?: throw IllegalArgumentException("format $name is illegal")
        }
    }
}

@Converter
class DropTypeConverter : AttributeConverter<DropType, String> {
    override fun convertToDatabaseColumn(attribute: DropType) = attribute.name
    override fun convertToEntityAttribute(dbData: String) = DropType.forName(dbData)
}
