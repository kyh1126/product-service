package com.smartfoodnet.config.feign

import org.springframework.context.annotation.Configuration

@Configuration
class FeignConfiguration{
    /**
     * feign client log 설정
     * application.yml 설정에서 처리 가능함
     */
//    @Bean
//    fun feignLoggerLevel(): Logger.Level? {
//        return Logger.Level.FULL
//    }

    /**
     * Timeout 설정
     * application.yml 설정에서 처리 가능함
     */
//    @Bean
//    fun requestOptions(): Request.Options? {
//        return Request.Options(2, TimeUnit.SECONDS, 2, TimeUnit.SECONDS, true)
//    }

    /**
     * Decoder 생성시 아래 주석을 풀고 진행하면 됩니다
     * 공통 응답부분을 처리하던가에 대한 내용을 진행하면 됩니다
     * 디코더가 없다면 기본 Decoder는 SpringDecoder입니다
     */
//    @Bean
//    fun feignDecoder(messageConverters: ObjectFactory<HttpMessageConverters>) : Decoder {
//        return OptionalDecoder(FeignDecoder(SpringDecoder(messageConverters)))
//    }

    /**
     * query string snake case 적용
     */
//    @Bean
//    fun queryMapEncoder() : QueryMapEncoder{
//        return SnakeCaseQueryMap()
//    }

    /**
     * 요청시 Header에 인증정보 추가
     * 필요시 주석 제거하고 실행
     */
//    @Bean
//    fun requestInterceptor(apiHeader : ApiHeader) : RequestInterceptor =
//    RequestInterceptor{
//        with(apiHeader) {
//            it.header("Authorization", authorization)
//            it.header("Credential", createCredential())
//            it.header("Signature", createSignature())
//        }
//    }


}