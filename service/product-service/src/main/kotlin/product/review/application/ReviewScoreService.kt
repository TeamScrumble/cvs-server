package product.review.application

import org.springframework.stereotype.Service
import product.review.domain.entity.ReviewScore
import product.review.domain.repository.reviewScore.ReviewScoreRepository
import review.ReviewAddApi.Request.ScoreRequest
import review.ReviewGetApi.Response.ScoreResponse
import review.ReviewSummaryGetApi

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

    suspend fun getAspectStatsForSummary(
        productId: Long
    ): List<ReviewSummaryGetApi.Response.AspectStat> {
        val stats = scoreRepository.findStatsByProductId(productId)
        if (stats.isEmpty()) return emptyList()

        // 옵션별 count
        val countMap = stats.associate { it.optionId to it.count }
        val aspects = aspectService.getAspects()
        if (aspects.isEmpty()) return emptyList()

        val aspectIds = aspects.map { it.id }
        val options = aspectService.getOptions(aspectIds)
        val optionsByAspectId = options.groupBy { it.aspectId }

        return aspects.map { aspect ->
            val optionStats = optionsByAspectId[aspect.id].orEmpty().map { o ->
                ReviewSummaryGetApi.Response.OptionStat(
                    optionId = o.id,
                    optionText = o.optionText,
                    count = countMap[o.id] ?: 0L
                )
            }

            ReviewSummaryGetApi.Response.AspectStat(
                aspectId = aspect.id,
                title = aspect.title,
                question = aspect.question,
                options = optionStats
            )
        }
    }

}