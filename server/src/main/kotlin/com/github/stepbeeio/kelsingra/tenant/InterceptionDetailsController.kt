package com.github.stepbeeio.kelsingra.tenant

import com.github.stepbeeio.kelsingra.model.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException


@RestController
class InterceptionDetailsController(
    private val interceptionService: InterceptionService,
) : IInterceptionDetailsController {
    @ResponseBody
    @GetMapping("/interception-details/services/{serviceKey}.json")
    override fun getByService(
        @PathVariable serviceKey: String,
    ): TenantInterceptionResponse {
        return interceptionService.detailsForService(serviceKey)
    }

    @ResponseBody
    @PutMapping("/interception-details")
    fun createTenant(@RequestBody request: CreateInterceptionRequest): TenantInterceptionResponse {
        return interceptionService.create(request)
    }

    @ResponseBody
    @PutMapping("/interception-details/services/{serviceKey}/sandbox/{sandboxKey}.json")
    fun addTenant(@RequestBody request: AddTenantRequest): TenantInterceptionResponse {
        return interceptionService.update(request)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

    @ResponseBody
    @DeleteMapping("/interception-details/services/{serviceKey}/sandbox/{sandboxKey}.json")
    fun removeTenant(@RequestBody request: RemoveTenantRequest): TenantInterceptionResponse {
        return interceptionService.update(request)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }
}
