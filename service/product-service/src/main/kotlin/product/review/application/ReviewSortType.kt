package product.review.application

enum class ReviewSortType(val description: String) {
    RECOMMENDED("추천순 (도움돼요 > 사진 > 최신)"),
    LATEST("최신순"),
    RATING_HIGH("별점 높은 순"),
    RATING_LOW("별점 낮은 순"),
    MOST_HELPFUL("도움돼요 많은 순");

    companion object {
        fun from(sort: String?): ReviewSortType =
            when (sort?.lowercase()) {
                "latest" -> LATEST
                "rating_high" -> RATING_HIGH
                "rating_low" -> RATING_LOW
                "most_helpful" -> MOST_HELPFUL
                "recommended" -> RECOMMENDED
                else -> RECOMMENDED
            }
    }

}