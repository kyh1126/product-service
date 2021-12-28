package com.smartfoodnet.fninventory.shortage

import io.swagger.annotations.Api
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Api(description = "결품 관련 API")
@RestController
@RequestMapping("shortage")
class ShortageController (private val shortageService: ShortageService){
}