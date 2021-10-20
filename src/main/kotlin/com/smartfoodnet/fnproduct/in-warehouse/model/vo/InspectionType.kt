//package com.smartfoodnet.fnproduct.product.model.vo
//
//import javax.persistence.AttributeConverter
//import javax.persistence.Converter
//
//enum class InspectionType(desc : String) {
//    ALL("전수"),
//    SAMPLE("샘플");
//
//    companion object {
//        fun forName(name : String) : InspectionType {
//            return values().firstOrNull { it.name == name } ?: throw IllegalArgumentException("format $name is illegal")
//        }
//    }
//}
//
//@Converter
//class InspectionTypeConverter : AttributeConverter<InspectionType, String> {
//    override fun convertToDatabaseColumn(attribute: InspectionType) = attribute.name
//    override fun convertToEntityAttribute(dbData: String) = InspectionType.forName(dbData)
//}