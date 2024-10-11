@file:Suppress("ktlint:standard:no-wildcard-imports")

package rbt.vacationtracker.extensions

import com.github.michaelbull.result.*
import rbt.vacationtracker.error.Error
import rbt.vacationtracker.error.TransactionError
import rbt.vacationtracker.service.transactionManager.ServiceTransactionManager

fun <T> ServiceTransactionManager.executeTransaction(lambda: () -> Result<T, Error>): Result<T, Error> =
    getTransaction()
        .let { transaction ->
            lambda()
                .mapBoth(
                    { v ->
                        runCatching {
                            commit(transaction)
                        }.mapBoth(
                            { Ok(v) },
                            { Err(TransactionError(it.message ?: "", "COMMIT")) },
                        )
                    },
                    { e ->
                        runCatching {
                            rollback(transaction)
                        }.mapBoth(
                            { Err(e) },
                            { Err(TransactionError(it.message ?: "", "ROLLBACK")) },
                        )
                    },
                )
        }
