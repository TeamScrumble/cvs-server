package product.common.valid.dto

data class ProfanityResult(
    val hasBadWord: Boolean = false,
    val badWords: List<String> = emptyList()
)
