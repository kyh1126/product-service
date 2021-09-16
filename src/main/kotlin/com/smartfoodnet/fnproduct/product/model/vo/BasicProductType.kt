package com.smartfoodnet.fnproduct.product.model.vo

import javax.persistence.AttributeConverter
import javax.persistence.Converter

enum class BasicProductType(val desc: String) {
    BASIC("기본상품"),
    CUSTOM_SUB("고객전용부자재"),
    SUB("공통부자재");

    companion object {
        fun fromName(name: String): BasicProductType {
            return values().firstOrNull { it.name == name }
                ?: throw IllegalArgumentException("Format $name is illegal")
        }
    }
}

@Converter
class BasicProductTypeConverter : AttributeConverter<BasicProductType, String> {
    override fun convertToDatabaseColumn(attribute: BasicProductType) = attribute.name
    override fun convertToEntityAttribute(dbData: String) = BasicProductType.fromName(dbData)
}
