package com.smartfoodnet.common

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun LocalDate.isBetween(startDate: LocalDate, endDate: LocalDate): Boolean =
    this == startDate || this == endDate || isAfter(startDate) && isBefore(endDate)

fun LocalDate.isBeforeOrEqual(date: LocalDate) = !isAfter(date)

fun LocalDate.isAfterOrEqual(date: LocalDate) = !isBefore(date)

fun String?.toLocalDateTime(pattern: String): LocalDateTime? {
    if (this == null) return null
    return LocalDate.parse(this, DateTimeFormatter.ofPattern(pattern)).atTime(0, 0)
}