package product.review.application

import error.errorcode.ReviewErrorCode
import error.exception.BusinessException
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import product.review.domain.entity.ReviewImg
import product.review.domain.repository.reviewImage.ReviewImgRepository
import review.ReviewAddApi

@Service
class ReviewImgService(
    private val imgRepository: ReviewImgRepository
) {

    suspend fun getImages(
        reviewIds: List<Long>
    ): Map<Long, List<String>> {
        if (reviewIds.isEmpty()) return emptyMap()

        val images = imgRepository.findAllByReviewIds(reviewIds)
            .groupBy { it.reviewId }
            .mapValues { (_, list) ->
                list.sortedBy { img -> img.displayOrder }
                    .map { img -> img.imgUrl }
            }

        return images
    }

    suspend fun addImages(
        reviewId: Long,
        images: List<ReviewAddApi.Request.ImageRequest>
    ) {
        if (images.isEmpty()) return

        val orders = images.map { it.displayOrder }
        if (!orders.all { it in 1..10 }) {
            // displayOrder 범위 오류
            throw BusinessException(ReviewErrorCode.R_008)
        }
        if (orders.distinct().size != orders.size) {
            // displayOrder 중복
            throw BusinessException(ReviewErrorCode.R_009)
        }

        val entities = images.map { req ->
            ReviewImg(
                reviewId = reviewId,
                imgUrl = req.imgUrl,
                displayOrder = req.displayOrder
            )
        }

        val saved = imgRepository.saveAll(entities).toList()

        if (saved.size != entities.size) {
            throw BusinessException(ReviewErrorCode.R_010)
        }
    }

}