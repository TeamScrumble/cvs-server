package product.common.valid.dto

data class ProfanityResult(
    val isFiltered: Boolean = false,
    val filteredWords: List<String> = emptyList()
)
