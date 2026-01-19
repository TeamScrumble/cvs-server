package product.profanity.presentation.rest

import ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import product.profanity.valid.ProfanityFilterService
import validation.ProfanityGetApi

@RestController
class ProfanityController(
    private val profanityFilterService: ProfanityFilterService
) : ProfanityGetApi {

    @GetMapping(ProfanityGetApi.PATH)
    override suspend fun get(
        @RequestParam keyword: String
    ): ApiResponse<ProfanityGetApi.Response> {
        val result = profanityFilterService.check(keyword)

        return ApiResponse.Success(
            ProfanityGetApi.Response(result.hasBadWord, result.badWords)
        )
    }
}