package com.github.stepbeeio.kelsingra.config

import com.github.stepbeeio.kelsingra.kafka.InterceptorConfig
import com.github.stepbeeio.kelsingra.kafka.TenantConsumerInterceptor
import com.github.stepbeeio.kelsingra.kafka.TenantProducerInterceptor
import com.github.stepbeeio.kelsingra.servlet.TenantInterceptorService
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.*


@Configuration
@EnableKafka
class KafkaConfig(
    @Value("\${spring.kafka.bootstrap-servers}") val bootstrapAddress: String,
    @Value("\${spring.kafka.consumer.group-id}") val consumerGroupId: String,
    private val interceptorService: TenantInterceptorService,
) {
    @Bean
    fun consumerFactory(): ConsumerFactory<String, String> {
        val props = mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapAddress,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG to TenantConsumerInterceptor::class.java.name,
            ConsumerConfig.GROUP_ID_CONFIG to consumerGroupId,
            InterceptorConfig.interceptorService to interceptorService,
        )

        return DefaultKafkaConsumerFactory(props);
    }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory =
            ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = consumerFactory()

        return factory
    }

    @Bean
    fun producerFactory(): ProducerFactory<String, String> {
        val configProps = mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapAddress,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.INTERCEPTOR_CLASSES_CONFIG to TenantProducerInterceptor::class.java.name,
            InterceptorConfig.interceptorService to interceptorService,
        )
        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(producerFactory())
    }

    @Bean
    fun exampleTopic(): NewTopic {
        return TopicBuilder.name(exampleTopic).build()
    }

    companion object {
        const val exampleTopic = "example-1"
    }
}
