package product.review.application

import org.springframework.stereotype.Service
import product.review.domain.entity.ReviewScore
import product.review.domain.repository.reviewScore.ReviewScoreRepository
import review.ReviewAddApi.Request.ScoreRequest
import review.ReviewGetApi.Response.ScoreResponse

@Service
class ReviewScoreService(
    private val scoreRepository: ReviewScoreRepository,
    private val aspectService: ReviewAspectService,
) {

    suspend fun addScores(
        reviewId: Long,
        scores: List<ScoreRequest>
    ) {

        scores.forEach { s ->
            scoreRepository.save(
                ReviewScore(
                    reviewId = reviewId,
                    optionId = s.optionId
                )
            )
        }
    }

    suspend fun getScores(
        reviewIds: List<Long>
    ): Map<Long, List<ScoreResponse>> {
        val scores = scoreRepository.findAllByReviewIds(reviewIds)

        val meta = aspectService.getOptionMeta(
            scores.map { it.optionId }.toSet()
        )

        return scores.groupBy { it.reviewId }
            .mapValues { (_, list) ->
                list.mapNotNull { meta[it.optionId] }
            }
    }

}