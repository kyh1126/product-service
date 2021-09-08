package com.smartfoodnet.common

import java.time.LocalDate
import java.time.format.DateTimeFormatter

//// String extensions
fun parseLocalDateOrNull(text: CharSequence, formatter: DateTimeFormatter): LocalDate? {
    return try {
        LocalDate.parse(text, formatter)
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

/**
 * Kotlin 에서, 일반적인 경우 런타임에 generic parameter 를 확인할 방법은 없다.<p>
 *
 * @see <a href="https://stackoverflow.com/questions/36569421/kotlin-how-to-work-with-list-casts-unchecked-cast-kotlin-collections-listkot#answer-36570969">동일 이슈</a>
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T : Any> List<*>.checkItemsAre() =
    if (all { it is T })
        this as List<T>
    else null
