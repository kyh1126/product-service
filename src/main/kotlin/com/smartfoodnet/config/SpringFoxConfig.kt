package com.smartfoodnet.config

import com.fasterxml.classmate.TypeResolver
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.Pageable
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.schema.AlternateTypeRules
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
@EnableSwagger2
class SpringFoxConfig(
    private val typeResolver: TypeResolver,
) {
    @Bean
    fun api(): Docket {
        val newRule = AlternateTypeRules.newRule(
            typeResolver.resolve(Pageable::class.java),
            typeResolver.resolve(Page::class.java)
        )

        return Docket(DocumentationType.SWAGGER_2)
            .alternateTypeRules(newRule)
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.smartfoodnet"))
            .paths(PathSelectors.any())
            .build()
            .apiInfo(apiInfo())
    }

    fun apiInfo(): ApiInfo {
        return ApiInfoBuilder()
            .title("FN Product API")
            .description("API Documentation")
            .version("1.0")
            .build()
    }

    @ApiModel(description = "페이지 요청")
    data class Page(
        @ApiModelProperty(value = "페이지 번호", example = "0")
        val page: Int,

        @ApiModelProperty(value = "\${sfn.swagger.size.value}", example = "50")
        val size: Int,

        @ApiModelProperty(value = "정렬 (사용법: 컬럼명,ASC|DESC) ex> id,DESC", example = "id,DESC")
        val sort: List<String>,
    )
}
