package com.smartfoodnet.common

import java.time.LocalDate

fun LocalDate.isBetween(startDate: LocalDate, endDate: LocalDate): Boolean =
    this == startDate || this == endDate || isAfter(startDate) && isBefore(endDate)

fun LocalDate.isBeforeOrEqual(date: LocalDate) = !isAfter(date)

fun LocalDate.isAfterOrEqual(date: LocalDate) = !isBefore(date)
