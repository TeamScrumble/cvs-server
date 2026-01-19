package product.common.config

import io.github.jwhyee.profanity.helper.ProfanityTrie
import io.github.jwhyee.profanity.validator.ProfanityValidator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ProfanityFilterConfig {
    @Bean
    fun profanityValidator(): ProfanityValidator {
        val trie = ProfanityTrie.create(
            customWords = emptyList(),
            excludeWords = emptyList()
        )
        
        return ProfanityValidator(trie, emptySet())
    }
}