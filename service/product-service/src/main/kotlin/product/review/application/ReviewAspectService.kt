package product.review.application

import error.errorcode.ReviewErrorCode
import error.exception.BusinessException
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

    suspend fun getAspects() = aspectRepository.findAllOrderByIdAsc()

    suspend fun getOptions(aspectIds: List<Long>) = optionRepository
        .findAllByAspectIdInOrderByAspectAndDisplay(aspectIds)

    data class SummaryOptionMeta(
        val aspectId: Long,
        val aspectTitle: String,
        val aspectQuestion: String,
        val optionId: Long,
        val optionText: String,
        val displayOrder: Int
    )

    suspend fun getSummaryOptionMeta(
        optionIds: Set<Long>
    ): List<SummaryOptionMeta> {
        if(optionIds.isEmpty()) return emptyList()

        val options = optionRepository
            .findAllByIdInOrderByDisplayOrderAsc(optionIds)

        val aspectIds = options.map { it.aspectId }.toSet()
        val aspects = aspectRepository.findAllByIdInOrderByIdAsc(aspectIds)
        val aspectMap = aspects.associateBy { it.id }

        return options.map { option ->
            val aspect = aspectMap[option.aspectId]
                ?: throw BusinessException(ReviewErrorCode.R_004)

            SummaryOptionMeta(
                aspectId = aspect.id,
                aspectTitle = aspect.title,
                aspectQuestion = aspect.question,
                optionId = option.id,
                optionText = option.optionText,
                displayOrder = option.displayOrder
            )
        }
    }

}