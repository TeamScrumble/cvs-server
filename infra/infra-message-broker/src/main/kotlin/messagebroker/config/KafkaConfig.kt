package messagebroker.config

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.*

@EnableKafka
@Configuration
@ConfigurationPropertiesScan
class KafkaConfig{

    @Bean
    fun producerConfig(
        props: KafkaProperties
    ): Map<String, Any> =
        mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to props.url,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
        )

    @Bean
    fun producerFactory(
        producerConfig: Map<String, Any>
    ): ProducerFactory<String, String> =
        DefaultKafkaProducerFactory(producerConfig)

    @Bean
    fun kafkaTemplate(
        producerFactory: ProducerFactory<String, String>
    ): KafkaTemplate<String, String> =
        KafkaTemplate(producerFactory)

    @Bean
    fun consumerConfig(
        props: KafkaProperties
    ): Map<String, Any> =
        mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to props.url,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.GROUP_ID_CONFIG to "cvs-service",
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "latest",
        )

    @Bean
    fun consumerFactory(
        consumerConfig: Map<String, Any>
    ): ConsumerFactory<String, String> =
        DefaultKafkaConsumerFactory(consumerConfig)

    @Bean
    fun kafkaListenerContainerFactory(
        consumerFactory: ConsumerFactory<String, String>
    ): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = consumerFactory
        return factory
    }
}
