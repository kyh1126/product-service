package com.smartfoodnet.common

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

fun LocalDate.toInstant(): Instant {
    return this.atStartOfDay(ZoneId.systemDefault()).toInstant()
}

fun LocalDate.isBetween(startDate: LocalDate, endDate: LocalDate): Boolean =
    this == startDate || this == endDate || isAfter(startDate) && isBefore(endDate)

fun LocalDate.isBeforeOrEqual(date: LocalDate) = !isAfter(date)

fun LocalDate.isAfterOrEqual(date: LocalDate) = !isBefore(date)
