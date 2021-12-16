package com.smartfoodnet.fnproduct.stock

import io.swagger.annotations.Api
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@Api(description = "재고 관련 API")
@RestController
@RequestMapping("stock")
class StockController {
}