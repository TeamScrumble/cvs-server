package review

import io.swagger.v3.oas.annotations.tags.Tag

@Tag(name = "Review", description = "상품 리뷰 API")
interface ReviewApi :
    ReviewAddApi,
    ReviewGetApi,
    ReviewListApi,
    ReviewDeleteApi,
    ReviewSummaryGetApi,
    ReviewAspectGetApi
