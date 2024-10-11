package rbt.vacationtracker.extensions

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import rbt.vacationtracker.error.DatabaseError
import rbt.vacationtracker.error.NonTransientError
import rbt.vacationtracker.error.TransientError

fun <T, ID, V> JpaRepository<T, ID>.toResult(lambda: () -> V): Result<V, DatabaseError> =
    runCatching {
        lambda()
    }.mapError { e ->
        when (e) {
            is LockTimeoutException -> TransientError(e.message ?: "", e)
            is OptimisticLockException -> TransientError(e.message ?: "", e)
            is PessimisticLockException -> TransientError(e.message ?: "", e)
            is QueryTimeoutException -> TransientError(e.message ?: "", e)
            is TransactionRequiredException -> TransientError(e.message ?: "", e)
            else -> NonTransientError(e.message ?: "", e)
        }
    }
