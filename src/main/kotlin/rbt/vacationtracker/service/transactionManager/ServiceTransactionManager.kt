package rbt.vacationtracker.service.transactionManager

import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.DefaultTransactionDefinition

@Component
class ServiceTransactionManager(
    private val transactionManager: PlatformTransactionManager,
) {
    val definition = DefaultTransactionDefinition()

    fun getTransaction(): TransactionStatus = transactionManager.getTransaction(definition)

    fun commit(status: TransactionStatus) = transactionManager.commit(status)

    fun rollback(status: TransactionStatus) = transactionManager.rollback(status)
}
