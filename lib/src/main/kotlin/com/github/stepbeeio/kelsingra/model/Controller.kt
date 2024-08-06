package com.github.stepbeeio.kelsingra.model

import org.springframework.web.bind.annotation.*


interface IInterceptionDetailsController {
    fun getByService(@PathVariable serviceKey: String): TenantInterceptionResponse
}
