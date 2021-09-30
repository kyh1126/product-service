package com.smartfoodnet.common.model

import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.Parameters
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema


@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@Retention
@Parameters(
    Parameter(
        `in` = ParameterIn.QUERY,
        description = "페이지 번호(0..N)",
        name = "page",
        schema = Schema(type = "integer", defaultValue = "0")),
    Parameter(
        `in` = ParameterIn.QUERY,
        description = "페이지 크기",
        name = "size",
        schema = Schema(type = "integer", defaultValue = "50")),
    Parameter(
        `in` = ParameterIn.QUERY,
        description = "정렬 (사용법: 컬럼명, ASC|DESC)",
        name = "sort",
        array = ArraySchema(schema = Schema(type = "string")))
)
annotation class PageableApi
