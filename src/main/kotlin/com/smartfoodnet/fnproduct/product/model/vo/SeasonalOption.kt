package com.smartfoodnet.fnproduct.product.model.vo

import javax.persistence.AttributeConverter
import javax.persistence.Converter

enum class SeasonalOption(val desc: String) {
    ALL("계절무관"),
    SUMMER("하절기");

    companion object {
        fun fromName(name: String): SeasonalOption {
            return values().firstOrNull { it.name == name }
                ?: throw IllegalArgumentException("Format $name is illegal")
        }
    }
}

@Converter
class SeasonalOptionConverter : AttributeConverter<SeasonalOption, String> {
    override fun convertToDatabaseColumn(attribute: SeasonalOption) = attribute.name
    override fun convertToEntityAttribute(dbData: String) = SeasonalOption.fromName(dbData)
}
