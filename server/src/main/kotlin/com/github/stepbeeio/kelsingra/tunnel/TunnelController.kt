package com.github.stepbeeio.kelsingra.tunnel

import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import java.util.*


data class SendRequest(val serviceKey: String, val sandboxKey: String)

@Controller
class TunnelController(
    private val persistableRequestService: PersistableRequestService,
) {
    /**
     * This message broker receives messages on the internal spring messaging bus
     */
    @MessageMapping("/tunnel/requests/{requestId}")
    fun receiveResponse(@DestinationVariable requestId: UUID, payload: Response) {
        logger.info("Received response: $payload")
        persistableRequestService.respond(requestId, payload)
    }

    @ResponseBody
    @PostMapping("/send-request")
    fun sendRequest(@RequestBody request: Request): Response {
        return persistableRequestService.send(request)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(TunnelController::class.java)
    }
}
