package com.github.stepbeeio.kelsingra.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Import(SpringInterceptorConfig::class)
@Configuration
class InterceptorConfig {
}
