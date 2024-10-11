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
import rbt.vacationtracker.domain.Vacation
import rbt.vacationtracker.domain.VacationId
import rbt.vacationtracker.dto.VacationResponse
import rbt.vacationtracker.error.BadCSVFormatError
import rbt.vacationtracker.error.Error
import rbt.vacationtracker.error.NotFoundError
import rbt.vacationtracker.error.TransientError
import rbt.vacationtracker.extensions.executeTransaction
import rbt.vacationtracker.repository.implementation.EmployeeRepositoryImpl
import rbt.vacationtracker.repository.implementation.VacationRepositoryImpl
import rbt.vacationtracker.service.implementation.VacationServiceImpl
import rbt.vacationtracker.service.transactionManager.ServiceTransactionManager
import rbt.vacationtracker.utils.FileEnum

@ContextConfiguration(classes = [VacationServiceTest::class])
@ExtendWith(MockKExtension::class)
class VacationServiceTest {
    @InjectMockKs
    private lateinit var vacationService: VacationServiceImpl

    @MockK
    private lateinit var vacationRepository: VacationRepositoryImpl

    @MockK
    private lateinit var employeeRepository: EmployeeRepositoryImpl

    @MockK
    private lateinit var serviceTransactionManager: ServiceTransactionManager

    @BeforeAll
    fun setup() {
        mockkStatic("rbt.vacationtracker.extensions.ServiceTransactionManagerExtensionKt")
    }

    @Test
    fun `should interact with repository to save vacations`() {
        val vacation = slot<Vacation>()
        val employeeId = slot<String>()
        val lambda = slot<() -> Result<List<VacationResponse>, Error>>()

        every { employeeRepository.findEmployeeById(capture(employeeId)) } answers { Ok(Employee(employeeId.captured, "testPassword")) }
        every { vacationRepository.save(capture(vacation)) } answers { Ok(vacation.captured) }
        every { serviceTransactionManager.executeTransaction(capture(lambda)) } answers { lambda.captured.invoke() }

        val csv = FileUtils.readFile(FileEnum.getFilePath(FileEnum.IMPORT_VACATION_VALID_CSV))
        val result = vacationService.addVacationsCSV(csv, 2019)

        verify { employeeRepository.findEmployeeById("test1@rbt.rs") }
        verify {
            vacationRepository.save(
                Vacation(
                    VacationId(
                        "test1@rbt.rs",
                        2019,
                    ),
                    20,
                    0,
                    Employee(
                        "test1@rbt.rs",
                        "testPassword",
                    ),
                ),
            )
        }
        confirmVerified(employeeRepository, vacationRepository)

        result.isOk shouldBe true
        result shouldBe Ok(listOf(VacationResponse("test1@rbt.rs", 2019, 20, 0)))
    }

    @Test
    fun `should return bad csv format error`() {
        val lambda = slot<() -> Result<List<VacationResponse>, Error>>()

        every { serviceTransactionManager.executeTransaction(capture(lambda)) } answers { lambda.captured.invoke() }

        val csv = FileUtils.readFile(FileEnum.getFilePath(FileEnum.IMPORT_VACATION_BAD_CSV_FORMAT_ERROR_CSV))
        val result = vacationService.addVacationsCSV(csv, 2019)

        result.isErr shouldBe true
        result.error.shouldBeTypeOf<BadCSVFormatError>()
        result.error.message shouldBe "Invalid header or invalid CSV format, line: test1@rbt.rs,twenty"
    }

    @Test
    fun `should return database error on save`() {
        val employeeId = slot<String>()
        val lambda = slot<() -> Result<List<VacationResponse>, Error>>()

        every { employeeRepository.findEmployeeById(capture(employeeId)) } answers { Ok(Employee(employeeId.captured, "testPassword")) }
        every { vacationRepository.save(any()) } answers { Err(TransientError("Database error", PersistenceException("Database error"))) }
        every { serviceTransactionManager.executeTransaction(capture(lambda)) } answers { lambda.captured.invoke() }

        val csv = FileUtils.readFile(FileEnum.getFilePath(FileEnum.IMPORT_VACATION_VALID_CSV))
        val result = vacationService.addVacationsCSV(csv, 2019)

        verify { employeeRepository.findEmployeeById("test1@rbt.rs") }
        verify {
            vacationRepository.save(
                Vacation(
                    VacationId(
                        "test1@rbt.rs",
                        2019,
                    ),
                    20,
                    0,
                    Employee(
                        "test1@rbt.rs",
                        "testPassword",
                    ),
                ),
            )
        }
        confirmVerified(employeeRepository, vacationRepository)

        result.isErr shouldBe true
        result.error.shouldBeTypeOf<TransientError>()
        result.error.message shouldBe "Database error"
    }

    @Test
    fun `should return not found error on find`() {
        val employeeId = slot<String>()
        val lambda = slot<() -> Result<List<VacationResponse>, Error>>()

        every { employeeRepository.findEmployeeById(capture(employeeId)) } answers
            { Err(NotFoundError("Employee with id: ${employeeId.captured} not found", "Employee")) }
        every { serviceTransactionManager.executeTransaction(capture(lambda)) } answers { lambda.captured.invoke() }

        val csv = FileUtils.readFile(FileEnum.getFilePath(FileEnum.IMPORT_VACATION_NOT_FOUND_ERROR_CSV))
        val result = vacationService.addVacationsCSV(csv, 2019)

        verify { employeeRepository.findEmployeeById("test1@rbt.rs") }
        confirmVerified(employeeRepository)

        result.isErr shouldBe true
        result.error.shouldBeTypeOf<NotFoundError>()
        result.error.message shouldBe "Employee with id: test1@rbt.rs not found"
    }
}
