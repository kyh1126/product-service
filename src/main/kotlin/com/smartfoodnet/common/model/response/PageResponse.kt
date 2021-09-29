package com.smartfoodnet.common.model.response

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

class PageResponse<T>(
    val contents: Collection<T> = listOf(),
    val pageRequest: Pageable,
    val totalCount: Long,
    val isLast: Boolean,
) {

    companion object {
        fun <T> of(target: Page<T>): PageResponse<T> {
            return target.run {
                PageResponse(
                    contents = content,
                    pageRequest = target.pageable,
                    totalCount = totalElements,
                    isLast = isLast
                )
            }
        }

        fun <T> of(
            contents: Collection<T>,
            totalCount: Long,
            page: Int,
            size: Int,
            sort: Sort = Sort.unsorted(),
        ): PageResponse<T> {
            return of(contents, totalCount, PageRequest.of(page, size, sort))
        }

        fun <T> of(contents: Collection<T>, totalCount: Long, pageRequest: Pageable): PageResponse<T> {
            return PageResponse(
                contents = contents,
                pageRequest = pageRequest,
                totalCount = totalCount,
                isLast = totalCount <= (pageRequest.pageNumber + 1) * pageRequest.pageSize
            )
        }
    }
}
