@file:Suppress("ktlint:standard:no-wildcard-imports")

package rbt.vacationtracker.testing.unit.service

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import jakarta.persistence.PersistenceException
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ContextConfiguration
import rbt.vacationemployee.utils.FileUtils
import rbt.vacationtracker.domain.Employee
import rbt.vacationtracker.dto.EmployeeResponse
import rbt.vacationtracker.error.BadCSVFormatError
import rbt.vacationtracker.error.Error
import rbt.vacationtracker.error.TransientError
import rbt.vacationtracker.extensions.executeTransaction
import rbt.vacationtracker.repository.implementation.EmployeeRepositoryImpl
import rbt.vacationtracker.service.implementation.EmployeeServiceImpl
import rbt.vacationtracker.service.transactionManager.ServiceTransactionManager
import rbt.vacationtracker.utils.Encoder
import rbt.vacationtracker.utils.FileEnum

@ContextConfiguration(classes = [EmployeeServiceTest::class])
@ExtendWith(MockKExtension::class)
class EmployeeServiceTest {
    @InjectMockKs
    private lateinit var employeeService: EmployeeServiceImpl

    @MockK
    private lateinit var employeeRepository: EmployeeRepositoryImpl

    @MockK
    private lateinit var serviceTransactionManager: ServiceTransactionManager

    @BeforeAll
    fun setup() {
        mockkStatic("rbt.vacationtracker.extensions.ServiceTransactionManagerExtensionKt")
    }

    @Test
    fun `should interact with repository to save employees`() {
        val employee = slot<Employee>()
        val lambda = slot<() -> Result<List<EmployeeResponse>, Error>>()

        every { employeeRepository.save(capture(employee)) } answers { Ok(employee.captured) }
        every { serviceTransactionManager.executeTransaction(capture(lambda)) } answers { lambda.captured.invoke() }

        val csv = FileUtils.readFile(FileEnum.getFilePath(FileEnum.IMPORT_EMPLOYEE_VALID_CSV))
        val result = employeeService.addEmployeesCSV(csv)

        verify {
            employeeRepository.save(
                match { it.email == "test1@rbt.rs" && Encoder.check("testPassword", it.password) },
            )
        }
        confirmVerified(employeeRepository)

        result.isOk shouldBe true
        result shouldBe Ok(listOf(EmployeeResponse("test1@rbt.rs")))
    }

    @Test
    fun `should return bad csv format error`() {
        val lambda = slot<() -> Result<List<EmployeeResponse>, Error>>()

        every { serviceTransactionManager.executeTransaction(capture(lambda)) } answers { lambda.captured.invoke() }

        val csv = FileUtils.readFile(FileEnum.getFilePath(FileEnum.IMPORT_EMPLOYEE_BAD_CSV_FORMAT_ERROR_CSV))
        val result = employeeService.addEmployeesCSV(csv)

        result.isErr shouldBe true
        result.error.shouldBeTypeOf<BadCSVFormatError>()
        result.error.message shouldBe "Invalid header or invalid CSV format, line: test1@rbt.rs"
    }

    @Test
    fun `should return database error`() {
        val lambda = slot<() -> Result<List<EmployeeResponse>, Error>>()

        every { employeeRepository.save(any()) } answers { Err(TransientError("Database error", PersistenceException("Database error"))) }
        every { serviceTransactionManager.executeTransaction(capture(lambda)) } answers { lambda.captured.invoke() }

        val csv = FileUtils.readFile(FileEnum.getFilePath(FileEnum.IMPORT_EMPLOYEE_VALID_CSV))
        val result = employeeService.addEmployeesCSV(csv)

        result.isErr shouldBe true
        result.error.shouldBeTypeOf<TransientError>()
        result.error.message shouldBe "Database error"
    }
}
