package db.transactional

import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.asFlux
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.stereotype.Component
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait

@Component
class Transactional(
    connectionFactory: ConnectionFactory
) {
    private val manager = R2dbcTransactionManager(connectionFactory)

    suspend fun <T> invoke(action: suspend () -> T): T {
        val operator = TransactionalOperator.create(manager)
        return operator.executeAndAwait { action() }
    }

    fun <T : Any> flowIn(action: () -> Flow<T>): Flow<T> {
        val operator = TransactionalOperator.create(manager)
        return operator.transactional(action().asFlux()).asFlow()
    }
}