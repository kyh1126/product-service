package com.smartfoodnet.apiclient

import com.fasterxml.jackson.databind.ObjectMapper
import com.smartfoodnet.common.model.response.CommonResponse
import com.smartfoodnet.common.utils.Log
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

abstract class RestTemplateClient(
    open val host: String,
    open val restTemplate: RestTemplate,
) {
    @Autowired
    lateinit var objectMapper: ObjectMapper

    fun getHeader(): HttpHeaders {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        return headers
    }

    inline fun <reified R> get(
        url: String,
        uriVariable: Any? = null,
        page: Int? = null,
        pageSize: Int? = null,
        sort: Map<String, String> = mapOf()
    ): R? {
        val httpEntity = HttpEntity<Any>(getHeader())
        val params = convert(objectMapper, uriVariable)
        val builder = getUriComponentsBuilder(url, params).run {
            setPageUriComponents(this, page, pageSize, sort).build()
        }

        val res = restTemplate.exchange(
            builder.toUriString(),
            HttpMethod.GET,
            httpEntity,
            CommonResponse::class.java
        )
        return objectMapper.convertValue(res.body?.payload, R::class.java)
    }

    inline fun <reified R> post(url: String, body: Any): R? {
        val httpEntity = HttpEntity(body, getHeader())
        val res =
            restTemplate.postForEntity(getUrlString(url), httpEntity, CommonResponse::class.java)

        return objectMapper.convertValue(res.body?.payload, R::class.java)
    }

    inline fun <reified T> put(url: String, body: T) {
        val httpEntity = HttpEntity<T>(body, getHeader())
        restTemplate.exchange(
            getUrlString(url),
            HttpMethod.PUT,
            httpEntity,
            CommonResponse::class.java
        )
    }

    fun getUrlString(url: String): String {
        return getUriComponentsBuilder(url).build().toString()
    }

    fun getUriComponentsBuilder(
        url: String,
        params: MultiValueMap<String, String>? = null
    ): UriComponentsBuilder {
        return UriComponentsBuilder.fromHttpUrl(host).path(url).queryParams(params)
    }

    /**
     * Set page and sorting query params
     *
     * sort >
     *  ex1: createdAt,DESC
     *  ex2: id,ASC
     */
    fun setPageUriComponents(
        uriComponentsBuilder: UriComponentsBuilder,
        page: Int?,
        pageSize: Int?,
        sort: Map<String, String>
    ): UriComponentsBuilder {
        page?.let { uriComponentsBuilder.queryParam("page", page) }
        pageSize?.let { uriComponentsBuilder.queryParam("size", pageSize) }

        val encodedComma = URLEncoder.encode(",", StandardCharsets.UTF_8.toString())
        sort.forEach { (key, value) ->
            uriComponentsBuilder.queryParam("sort", "$key$encodedComma${value.uppercase()}")
        }
        return uriComponentsBuilder
    }

    companion object : Log
}
