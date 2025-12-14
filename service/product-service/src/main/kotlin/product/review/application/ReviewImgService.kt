package product.review.application

import org.springframework.stereotype.Service
import product.review.domain.repository.reviewImage.ReviewImgRepository

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

}