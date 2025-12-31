package product.product.elasticsearch.bootstrap

import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import product.product.elasticsearch.service.ProductEsSyncService

@Profile("local")
@Component
class ProductEsInitialLoadRunner(
    private val productSyncService: ProductEsSyncService
) : ApplicationRunner {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun run(args: ApplicationArguments) = runBlocking {
        // 기본 동작: 로컬에서 앱 뜰 때 전체 적재 1회
        // 옵션으로 끄고/켜기 가능하게 플래그 제공
        val enabled = args.containsOption("es.initial-load") || args.getOptionValues("es.initial-load") != null
        val disabled = args.containsOption("es.skip-initial-load")

        if (disabled) {
            log.info("[ES Initial Load] skipped (es.skip-initial-load)")
            return@runBlocking
        }

        // enabled 플래그가 없으면 기본적으로 실행하고 싶다면 아래 조건을 반대로 바꾸면 됨.
        // 지금 구현은 "기본 실행"으로 두고, skip 플래그만 제공.
        log.info("[ES Initial Load] start (local profile)")

        // pageSize는 상황에 맞게 조절 가능
        val pageSize = args.getOptionValues("es.page-size")?.firstOrNull()?.toIntOrNull() ?: 2000

        // 실제 초기 적재 실행
        productSyncService.initialLoad(pageSize = pageSize)

        log.info("[ES Initial Load] done (pageSize={})", pageSize)
    }
}