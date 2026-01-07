package product.review.application

import org.springframework.stereotype.Service
import product.review.domain.repository.review.ReviewRepository
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneId

@Service
class ReviewWritePolicyService(
    private val reviewRepository: ReviewRepository,
    private val clock: Clock = Clock.system(ZoneId.of("Asia/Seoul"))
) {

    data class Eligibility(
        val canWrite: Boolean,
        val nextWritableDate: LocalDate
    )

    suspend fun getEligibility(
        productId: Long,
        memberId: Long,
        today: LocalDate = LocalDate.now(clock)
    ): Eligibility {
        // 마지막 작성 시각이 없으면 최초 작성
        val lastCreatedAt = reviewRepository.findLastCreatedAt(productId, memberId)
            ?: return Eligibility(
                canWrite = true,
                nextWritableDate = today
            )

        // 마지막 작성 날짜만 고려
        val lastDate = lastCreatedAt.toLocalDate()
        // 마지막 작성일로부터 1개월 후 작성 가능
        val nextWritableAt = lastDate.plusMonths(1)

        return Eligibility(
            canWrite = !nextWritableAt.isAfter(today),
            nextWritableDate = nextWritableAt
        )
    }

}