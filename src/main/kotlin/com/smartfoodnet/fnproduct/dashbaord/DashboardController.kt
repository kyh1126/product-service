package com.smartfoodnet.fnproduct.dashbaord

import com.smartfoodnet.fnproduct.dashbaord.model.DashboardModel
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("dashboard")
class DashboardController(
    val dashboardService : DashboardService
) {

    @GetMapping("{partnerId}")
    fun getDashboard(
        @PathVariable partnerId: Long
    ) : DashboardModel {
        return dashboardService.getCount(partnerId)
    }
}