package product.review.application

import org.springframework.stereotype.Service
import product.review.domain.repository.reviewAspect.ReviewAspectOptionRepository
import product.review.domain.repository.reviewAspect.ReviewAspectRepository
import review.ReviewGetApi.Response.ScoreResponse

@Service
class ReviewAspectService(
    private val aspectRepository: ReviewAspectRepository,
    private val optionRepository: ReviewAspectOptionRepository
) {

    suspend fun getOptionMeta(
        optionIds: Set<Long>
    ): Map<Long, ScoreResponse> {
        // 옵션 조회
        val options = optionRepository
            .findAllByIdInOrderByDisplayOrderAsc(optionIds)
        // 옵션들이 참조하는 aspect 조회
        val aspectIds = options.map { it.aspectId }.toSet()
        val aspects = aspectRepository.findAllByIdInOrderByIdAsc(aspectIds)

        val aspectMap = aspects.associateBy { it.id }

        return options.associate { option ->
            val aspect = aspectMap[option.aspectId]!!
            option.id to ScoreResponse(
                aspectId = aspect.id,
                optionId = option.id,
                aspectTitle = aspect.title,
                optionName = option.optionText
            )
        }
    }

}