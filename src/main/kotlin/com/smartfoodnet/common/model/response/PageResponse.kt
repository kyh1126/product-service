package com.smartfoodnet.common.model.response

import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort

class PageResponse<T>(
    val payload: Collection<T> = listOf(),
    val pagination: Pagination,
) {

    companion object {
        fun <T> of(target: Page<T>): PageResponse<T> {
            return target.run {
                val pageRequest = with(pageable) {
                    CustomPageRequest(number, size, sort)
                }
                PageResponse(content, Pagination(totalElements, isLast, pageRequest))
            }
        }

        // Collection 으로 보낼 경우, count 쿼리를 날려 totalCount 책임 지기
        fun <T> of(
            contents: Collection<T>,
            totalCount: Long,
            page: Int,
            size: Int,
            sort: Sort = Sort.unsorted(),
        ): PageResponse<T> {
            return of(contents, totalCount, CustomPageRequest(page, size, sort))
        }

        fun <T> of(
            contents: Collection<T>,
            totalCount: Long,
            pageRequest: CustomPageRequest
        ): PageResponse<T> {
            return PageResponse(
                payload = contents,
                pagination = Pagination(
                    totalCount = totalCount,
                    isLast = totalCount <= (pageRequest.page + 1) * pageRequest.size,
                    pageRequest = pageRequest
                )
            )
        }
    }
}

data class Pagination(
    val totalCount: Long,
    val isLast: Boolean,
    val pageRequest: CustomPageRequest,
)

data class CustomPageRequest(
    val page: Int,
    val size: Int,
    val sort: Sort,
)
