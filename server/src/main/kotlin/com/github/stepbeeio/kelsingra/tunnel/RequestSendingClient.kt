package com.github.stepbeeio.kelsingra.tunnel

import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component

@Component
class RequestSendingClient(
    private val messagingTemplate: SimpMessagingTemplate,
) {

    fun sendRequest(serviceKey: String, sandboxKey: String, request: Request) {
        messagingTemplate.convertAndSend("/topic/services/$serviceKey/sandboxes/$sandboxKey", request)
    }
}
