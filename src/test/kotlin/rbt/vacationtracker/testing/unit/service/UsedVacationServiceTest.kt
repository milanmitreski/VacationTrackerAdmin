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
import rbt.vacationtracker.domain.*
import rbt.vacationtracker.dto.EmployeeResponse
import rbt.vacationtracker.dto.UsedVacationResponse
import rbt.vacationtracker.error.BadCSVFormatError
import rbt.vacationtracker.error.Error
import rbt.vacationtracker.error.NotFoundError
import rbt.vacationtracker.error.TransientError
import rbt.vacationtracker.extensions.executeTransaction
import rbt.vacationtracker.repository.implementation.UsedVacationRepositoryImpl
import rbt.vacationtracker.repository.implementation.VacationRepositoryImpl
import rbt.vacationtracker.service.implementation.UsedVacationServiceImpl
import rbt.vacationtracker.service.transactionManager.ServiceTransactionManager
import rbt.vacationtracker.utils.FileEnum
import java.time.LocalDate

@ContextConfiguration(classes = [(UsedVacationServiceTest::class)])
@ExtendWith(MockKExtension::class)
class UsedVacationServiceTest {
    @InjectMockKs
    private lateinit var usedVacationService: UsedVacationServiceImpl

    @MockK
    private lateinit var vacationRepository: VacationRepositoryImpl

    @MockK
    private lateinit var usedVacationRepository: UsedVacationRepositoryImpl

    @MockK
    private lateinit var serviceTransactionManager: ServiceTransactionManager

    @BeforeAll
    fun setup() {
        mockkStatic("rbt.vacationtracker.extensions.ServiceTransactionManagerExtensionKt")
    }

    @Test
    fun `should interact with repository to save used vacations`() {
        val usedVacation = slot<UsedVacation>()
        val vacationId = slot<VacationId>()
        val lambda = slot<() -> Result<List<UsedVacationResponse>, Error>>()

        every { vacationRepository.findVacationById(capture(vacationId)) } answers
            { Ok(Vacation(vacationId.captured, 20, 0, Employee(vacationId.captured.email, "testPassword"))) }
        every { usedVacationRepository.save(capture(usedVacation)) } answers { Ok(usedVacation.captured) }
        every { serviceTransactionManager.executeTransaction(capture(lambda)) } answers { lambda.captured.invoke() }

        val csv = FileUtils.readFile(FileEnum.getFilePath(FileEnum.IMPORT_USED_VACATION_VALID_CSV))
        val result = usedVacationService.addUsedVacationsCSV(csv)

        verify { vacationRepository.findVacationById(VacationId("test1@rbt.rs", 2019)) }
        verify {
            usedVacationRepository.save(
                UsedVacation(
                    UsedVacationId(
                        VacationId("test1@rbt.rs", 2019),
                        LocalDate.of(2019, 11, 11),
                    ),
                    LocalDate.of(2019, 11, 20),
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
                ),
            )
        }
        confirmVerified(vacationRepository, usedVacationRepository)

        result.isOk shouldBe true
        result shouldBe Ok(listOf(UsedVacationResponse("test1@rbt.rs", 2019, LocalDate.of(2019, 11, 11), LocalDate.of(2019, 11, 20))))
    }

    @Test
    fun `should return bad csv format error`() {
        val lambda = slot<() -> Result<List<UsedVacationResponse>, Error>>()

        every { serviceTransactionManager.executeTransaction(capture(lambda)) } answers { lambda.captured.invoke() }

        val csv = FileUtils.readFile(FileEnum.getFilePath(FileEnum.IMPORT_USED_VACATION_BAD_CSV_FORMAT_ERROR_CSV))
        val result = usedVacationService.addUsedVacationsCSV(csv)

        result.isErr shouldBe true
        result.error.shouldBeTypeOf<BadCSVFormatError>()
        result.error.message shouldBe "Invalid header or invalid CSV format, line: test1@rbt.rs,Monday, November 11, 2019"
    }

    @Test
    fun `should return database error on save`() {
        val vacationId = slot<VacationId>()
        val lambda = slot<() -> Result<List<EmployeeResponse>, Error>>()

        every { vacationRepository.findVacationById(capture(vacationId)) } answers
            { Ok(Vacation(vacationId.captured, 20, 0, Employee(vacationId.captured.email, "testPassword"))) }
        every { usedVacationRepository.save(any()) } answers
            { Err(TransientError("Database error", PersistenceException("Database error"))) }
        every { serviceTransactionManager.executeTransaction(capture(lambda)) } answers { lambda.captured.invoke() }

        val csv = FileUtils.readFile(FileEnum.getFilePath(FileEnum.IMPORT_USED_VACATION_VALID_CSV))
        val result = usedVacationService.addUsedVacationsCSV(csv)

        verify { vacationRepository.findVacationById(VacationId("test1@rbt.rs", 2019)) }
        verify {
            usedVacationRepository.save(
                UsedVacation(
                    UsedVacationId(
                        VacationId("test1@rbt.rs", 2019),
                        LocalDate.of(2019, 11, 11),
                    ),
                    LocalDate.of(2019, 11, 20),
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
                ),
            )
        }
        confirmVerified(vacationRepository, usedVacationRepository)

        result.isErr shouldBe true
        result.error.shouldBeTypeOf<TransientError>()
        result.error.message shouldBe "Database error"
    }

    @Test
    fun `should return not found error on find`() {
        val vacationId = slot<VacationId>()
        val lambda = slot<() -> Result<List<EmployeeResponse>, Error>>()

        every { vacationRepository.findVacationById(capture(vacationId)) } answers
            { Err(NotFoundError("Employee with id: ${vacationId.captured} not found", "Employee")) }
        every { serviceTransactionManager.executeTransaction(capture(lambda)) } answers { lambda.captured.invoke() }

        val csv = FileUtils.readFile(FileEnum.getFilePath(FileEnum.IMPORT_USED_VACATION_NOT_FOUND_ERROR_CSV))
        val result = usedVacationService.addUsedVacationsCSV(csv)

        verify { vacationRepository.findVacationById(VacationId("test1@rbt.rs", 2019)) }
        confirmVerified(vacationRepository)

        result.isErr shouldBe true
        result.error.shouldBeTypeOf<NotFoundError>()
        result.error.message shouldBe "Employee with id: ${VacationId("test1@rbt.rs", 2019)} not found"
    }
}
