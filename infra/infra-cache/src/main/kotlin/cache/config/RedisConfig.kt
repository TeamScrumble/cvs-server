package cache.config

import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
@ConfigurationPropertiesScan
class RedisConfig {

    @Bean
    fun redisConnectionFactory(props: CacheProperties): LettuceConnectionFactory { // Changed
        val conf = RedisStandaloneConfiguration(props.host, props.port).apply {
            if (!props.password.isNullOrBlank()) {
                password = RedisPassword.of(props.password)
            }
            database = props.database
        }
        return LettuceConnectionFactory(conf)
    }

    @Bean
    fun reactiveRedisTemplate(
        factory: ReactiveRedisConnectionFactory,
    ): ReactiveRedisTemplate<String, String> {
        val keySerializer = StringRedisSerializer()
        val valueSerializer = StringRedisSerializer()

        val context = RedisSerializationContext
            .newSerializationContext<String, String>(keySerializer)
            .value(valueSerializer)
            .build()

        return ReactiveRedisTemplate(factory, context)
    }
}
