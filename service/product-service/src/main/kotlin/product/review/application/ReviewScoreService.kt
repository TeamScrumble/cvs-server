package product.review.application

import error.errorcode.ReviewErrorCode
import error.exception.BusinessException
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import product.review.domain.entity.ReviewAspectOption
import product.review.domain.entity.ReviewScore
import product.review.domain.repository.ReviewAspectOptionRepository
import product.review.domain.repository.ReviewAspectRepository
import product.review.domain.repository.ReviewScoreRepository
import review.ReviewAddApi.Request.ScoreRequest

@Service
class ReviewScoreService(
    private val scoreRepository: ReviewScoreRepository,
    private val optionRepository: ReviewAspectOptionRepository,
    private val aspectRepository: ReviewAspectRepository
) {

    suspend fun addScores(reviewId: Long, scores: List<ScoreRequest>) {
        validateScores(scores)

        scores.forEach { s ->
            scoreRepository.save(
                ReviewScore(
                    reviewId = reviewId,
                    optionId = s.optionId
                )
            )
        }
    }

    private suspend fun validateScores(scores: List<ScoreRequest>) {
//        val aspectIds = scores.map { it.aspectId }.toSet()
        val optionIds = scores.map { it.optionId }.toSet()

//        val aspects = aspectRepository.findAllById(aspectIds)
//            .toList()
//            .associateBy { it.id }
        val options = optionRepository.findAllById(optionIds)
            .toList()
            .associateBy { it.id }

        scores.forEach { s ->
//            val aspect = aspects[s.aspectId]
//                ?: throw BusinessException(ReviewErrorCode.R_004) // 존재하지 않는 평가 항목
            val option = options[s.optionId]
                ?: throw BusinessException(ReviewErrorCode.R_005) // 존재하지 않는 옵션

//            if (option.aspectId != aspect.id) {
//                throw BusinessException(ReviewErrorCode.R_006) // 항목-옵션 불일치
//            }
        }
    }

}