package com.smartfoodnet.apiclient

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap


fun convert(objectMapper: ObjectMapper, dto: Any?): MultiValueMap<String, String>? {
    return try {
        val params: MultiValueMap<String, String> = LinkedMultiValueMap()
        val map = objectMapper.convertValue(
            dto,
            object : TypeReference<Map<String?, String?>?>() {})
        if (map != null) {
            params.setAll(map)
        }
        params
    } catch (e: Exception) {
        throw IllegalStateException("Url Parameter 변환중 오류가 발생했습니다.")
    }
}