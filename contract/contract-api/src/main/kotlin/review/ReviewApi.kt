package review

import io.swagger.v3.oas.annotations.tags.Tag

@Tag(name = "Review", description = "상품 리뷰 API")
interface ReviewApi :
    ReviewAddApi,
    ReviewGetApi,
    ReviewListApi,
    ReviewSummaryGetApi,
    ReviewAspectGetApi
{

    companion object {
        const val PATH = "/api/product/review"
    }

}