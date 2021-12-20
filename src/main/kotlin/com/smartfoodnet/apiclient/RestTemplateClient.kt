package com.smartfoodnet.apiclient

import com.fasterxml.jackson.databind.ObjectMapper
import com.smartfoodnet.common.model.response.CommonResponse
import com.smartfoodnet.common.utils.Log
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

abstract class RestTemplateClient : Log {
    @Autowired
    lateinit var objectMapper: ObjectMapper
    @Autowired
    lateinit var restTemplate: RestTemplate

    fun getHeader() : HttpHeaders{
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        return headers
    }

    inline fun <reified R> get(url:String) : R?{
        val httpEntity = HttpEntity<Any>(getHeader())
        val res = restTemplate.exchange(url, HttpMethod.GET, httpEntity, CommonResponse::class.java)
        return objectMapper.convertValue(res.body?.payload, R::class.java)
    }

    inline fun <reified R> get(url:String, uriVariable: Any): R?{
        val httpEntity = HttpEntity<Any>(getHeader())
        val params = convert(objectMapper, uriVariable)
        val builder = UriComponentsBuilder.fromUriString(url)
            .queryParams(params)
            .build()

        val res = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, httpEntity, CommonResponse::class.java)
        return objectMapper.convertValue(res.body?.payload, R::class.java)
    }

    inline fun <reified R> post(url:String, body : Any) : R? {
        val httpEntity = HttpEntity(body, getHeader())
        val res = restTemplate.postForEntity(url, httpEntity, CommonResponse::class.java)

        return objectMapper.convertValue(res.body?.payload, R::class.java)
    }

    inline fun <reified T> put(url:String, body : T) {
        val httpEntity = HttpEntity<T>(body, getHeader())
        restTemplate.exchange(url, HttpMethod.PUT, httpEntity, CommonResponse::class.java)
    }
}