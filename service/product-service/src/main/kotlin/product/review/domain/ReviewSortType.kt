package product.review.domain

enum class ReviewSortType(val description: String) {
    RECOMMENDED("추천순 (도움돼요 > 사진 > 최신)"),
    LATEST("최신순"),
    RATING_HIGH("별점 높은 순"),
    RATING_LOW("별점 낮은 순"),
    MOST_HELPFUL("도움돼요 많은 순");

    companion object {
        fun from(value: String?): ReviewSortType {
            if (value.isNullOrBlank()) {
                return RECOMMENDED
            }

            return entries.firstOrNull{
                it.name.equals(value, ignoreCase = true)
            } ?: RECOMMENDED
        }
    }
}