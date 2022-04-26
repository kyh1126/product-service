package com.smartfoodnet.common

import org.springframework.beans.BeanUtils
import org.springframework.beans.BeanWrapperImpl
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//// String extensions
fun parseLocalDateTimeOrNull(text: CharSequence, formatter: String): LocalDateTime? {
    return try {
        val dtf = DateTimeFormatter.ofPattern(formatter)
        LocalDateTime.parse(text, dtf)
    } catch (ex: Throwable) {
        null
    }
}

fun formatToBasicIsoDate(date: LocalDate): String {
    return date.format(DateTimeFormatter.BASIC_ISO_DATE)
}

fun formatToIsoDate(date: LocalDate): String {
    return date.format(DateTimeFormatter.ISO_DATE)
}

fun getNullPropertyNames(source: Any): Array<String> {
    val src = BeanWrapperImpl(source)
    val pds = src.propertyDescriptors
    val emptyNames = HashSet<String>()
    for (pd in pds) {
        if (src.getPropertyValue(pd.name) == null) emptyNames.add(pd.name)
    }
    return emptyNames.toTypedArray()
}

/**
 * @param source 원본 객체
 * @param target 복삽받을 대상 객체
 */
fun copyNonNullProperty(source: Any, target: Any) {
    BeanUtils.copyProperties(source, target, *getNullPropertyNames(source))
}

fun convertYnToInt(yn: String?) = if (yn == "Y") 1 else 0

fun convertYnToLong(yn: String?) = if (yn == "Y") 1L else 0L

/**
 * FeignException 의 message 에서 노스노스 에러 메시지를 반환한다.
 * ex>
 * [400 ] during [PATCH] to [http://localhost:4001/fresh-networks/fn-warehouse-service/release/cancel/193353] [WmsApiClient#cancelRelease(long)]: [{"serviceCode":"FN-NOSNOS-SERVICE","errorCode":"500","errorMessage":"4117-출고요청 상태인 경우만 출고취소가 가능합니다."}]
 *
 * @see com.smartfoodnet.common.error.ApiExceptionHandler.handleFeignException
 */
fun getNosnosErrorMessage(message: String?): String? {
    if (message == null) return null

    // "serviceCode":"FN-NOSNOS-SERVICE","errorCode":"500","errorMessage":"4117-출고요청 상태인 경우만 출고취소가 가능합니다."
    val responseJson = message.split("[", "{", "}", "]")
        .firstOrNull { it.contains("errorMessage") }

    // 4117-출고요청 상태인 경우만 출고취소가 가능합니다.
    val codeAndMessage = responseJson?.split(",")
        ?.firstOrNull { it.contains("errorMessage") }
        ?.split(":")?.lastOrNull()
        ?.replace("\"", "")

    return codeAndMessage?.split("-")?.lastOrNull()
}

/**
 * Kotlin 에서, 일반적인 경우 런타임에 generic parameter 를 확인할 방법은 없다.
 * <p>
 * @see <a href="https://stackoverflow.com/questions/36569421/kotlin-how-to-work-with-list-casts-unchecked-cast-kotlin-collections-listkot#answer-36570969">동일 이슈</a>
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T : Any> List<*>.checkItemsAre() =
    if (all { it is T })
        this as List<T>
    else null
