package com.smartfoodnet.base

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.web.filter.CharacterEncodingFilter

/**
 * MediaType.APPLICATION_JSON_UTF8 가 deprecated 되어 MediaType.APPLICATION_JSON 를 사용하도록 변경되어
 * response header 의 content-type 에 charset=UTF-8 가 제거되어 한글이 깨지는 이슈가 발생. UTF-8 CharacterEncodingFilter bean 별도 추가
 * <p>
 * @see @AutoConfigureMockMvc 는 SpringBootMockMvcBuilderCustomizer 에서 bean 등록된 filter 들을 mockMvc filter 에 설정해준다.
 */
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Retention
@MustBeDocumented
@AutoConfigureMockMvc
@Import(UTF8AutoConfigureMockMvc.Config::class)
annotation class UTF8AutoConfigureMockMvc {
    class Config {
        @Bean
        fun characterEncodingFilter(): CharacterEncodingFilter {
            return CharacterEncodingFilter("UTF-8", true)
        }
    }
}
