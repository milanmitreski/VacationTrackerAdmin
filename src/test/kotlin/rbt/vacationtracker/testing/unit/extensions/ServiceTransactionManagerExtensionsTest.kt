package rbt.vacationtracker.testing.unit.extensions

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.UnexpectedRollbackException
import org.springframework.transaction.support.SimpleTransactionStatus
import rbt.vacationtracker.error.BadCSVFormatError
import rbt.vacationtracker.error.TransactionError
import rbt.vacationtracker.extensions.executeTransaction
import rbt.vacationtracker.service.transactionManager.ServiceTransactionManager

@ContextConfiguration(classes = [ServiceTransactionManagerExtensionsTest::class])
@ExtendWith(MockKExtension::class)
class ServiceTransactionManagerExtensionsTest {
    @InjectMockKs
    private lateinit var serviceTransactionManager: ServiceTransactionManager

    @MockK
    private lateinit var transactionManager: PlatformTransactionManager

    @BeforeAll
    fun setup() {
        mockkStatic("rbt.vacationtracker.extensions.ServiceTransactionManagerExtensionKt")
    }

    @Test
    fun `should successfully commit`() {
        val transaction = slot<TransactionStatus>()

        every { transactionManager.getTransaction(any()) } answers { SimpleTransactionStatus() }
        every { transactionManager.commit(capture(transaction)) } answers {}

        val result = serviceTransactionManager.executeTransaction { Ok("Valid value") }

        verify { transactionManager.getTransaction(any()) }
        verify { transactionManager.commit(any()) }
        confirmVerified(transactionManager)

        result.isOk shouldBe true
        result shouldBe Ok("Valid value")
    }

    @Test
    fun `should raise transaction error on commit`() {
        val transaction = slot<TransactionStatus>()

        every { transactionManager.getTransaction(any()) } answers { SimpleTransactionStatus() }
        every { transactionManager.commit(capture(transaction)) } answers { throw UnexpectedRollbackException("Transaction error") }

        val result = serviceTransactionManager.executeTransaction { Ok("Valid value") }

        verify { transactionManager.getTransaction(any()) }
        verify { transactionManager.commit(any()) }
        confirmVerified(transactionManager)

        result.isErr shouldBe true
        result shouldBe Err(TransactionError("Transaction error", "COMMIT"))
    }

    @Test
    fun `should successfully rollback`() {
        val transaction = slot<TransactionStatus>()

        every { transactionManager.getTransaction(any()) } answers { SimpleTransactionStatus() }
        every { transactionManager.rollback(capture(transaction)) } answers {}

        // Error is a sealed class, so there is no possibility to instantiate an anonymous error
        val result = serviceTransactionManager.executeTransaction { Err(BadCSVFormatError("Error")) }

        verify { transactionManager.getTransaction(any()) }
        verify { transactionManager.rollback(any()) }
        confirmVerified(transactionManager)

        result.isErr shouldBe true
        result.error.shouldBeTypeOf<BadCSVFormatError>()
        result.error.message shouldBe "Error"
    }

    @Test
    fun `should raise transaction error on rollback`() {
        val transaction = slot<TransactionStatus>()

        every { transactionManager.getTransaction(any()) } answers { SimpleTransactionStatus() }
        every { transactionManager.rollback(capture(transaction)) } answers { throw UnexpectedRollbackException("Transaction error") }

        val result = serviceTransactionManager.executeTransaction { Err(BadCSVFormatError("Error")) }

        verify { transactionManager.getTransaction(any()) }
        verify { transactionManager.rollback(any()) }
        confirmVerified(transactionManager)

        result.isErr shouldBe true
        result shouldBe Err(TransactionError("Transaction error", "ROLLBACK"))
    }
}
