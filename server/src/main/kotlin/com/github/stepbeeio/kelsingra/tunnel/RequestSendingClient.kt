package com.github.stepbeeio.kelsingra.tunnel

import org.slf4j.LoggerFactory
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component

@Component
class RequestSendingClient(
    private val messagingTemplate: SimpMessagingTemplate,
) {

    fun sendRequest(request: Request) {
        val destination = "/topic/services/${request.serviceKey}/sandboxes/${request.sandboxKey}"
        log.info("Publishing request to $destination")
        messagingTemplate.convertAndSend(
            destination,
            request
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(RequestSendingClient::class.java)
    }
}
