package com.smartfoodnet.common.model.binder

import com.fasterxml.jackson.databind.ObjectMapper
import com.smartfoodnet.common.model.header.SfnMetaUser
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class StringToSfnMetaUserConverter(
    private val objectMapper: ObjectMapper
) : Converter<String, SfnMetaUser?> {
    override fun convert(source: String): SfnMetaUser? = objectMapper.readValue(source, SfnMetaUser::class.java)
}
