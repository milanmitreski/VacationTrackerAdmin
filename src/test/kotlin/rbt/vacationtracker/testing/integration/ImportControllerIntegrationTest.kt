@file:Suppress("ktlint:standard:no-wildcard-imports")

package rbt.vacationtracker.testing.integration

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.*
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.test.context.TestPropertySource
import org.springframework.web.util.DefaultUriBuilderFactory
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import rbt.vacationemployee.utils.FileUtils
import rbt.vacationtracker.VacationtrackerApplication
import rbt.vacationtracker.domain.Employee
import rbt.vacationtracker.domain.UsedVacationId
import rbt.vacationtracker.domain.Vacation
import rbt.vacationtracker.domain.VacationId
import rbt.vacationtracker.dto.EmployeeResponse
import rbt.vacationtracker.dto.UsedVacationResponse
import rbt.vacationtracker.dto.VacationResponse
import rbt.vacationtracker.error.*
import rbt.vacationtracker.repository.EmployeeRepository
import rbt.vacationtracker.repository.UsedVacationRepository
import rbt.vacationtracker.repository.VacationRepository
import rbt.vacationtracker.utils.FileEnum
import java.time.LocalDate

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [VacationtrackerApplication::class],
)
@TestPropertySource(
    locations = ["classpath:application-test.properties"],
)
@TestInstance(Lifecycle.PER_CLASS)
@Testcontainers
class ImportControllerIntegrationTest(
    @LocalServerPort
    private val port: Int,
) {
    @Autowired
    private lateinit var employeeRepository: EmployeeRepository

    @Autowired
    private lateinit var vacationRepository: VacationRepository

    @Autowired
    private lateinit var usedVacationRepository: UsedVacationRepository

    private val testRestTemplate: TestRestTemplate = TestRestTemplate()
    private val headers: HttpHeaders = HttpHeaders()

    // Test setup
    companion object {
        @Container
        @ServiceConnection
        val postgresql: PostgreSQLContainer<*> =
            PostgreSQLContainer("postgres:latest")
                .withDatabaseName("test")
                .withUsername("test")
                .withPassword("test")
                .withInitScript("test-schema.sql")
                .apply { start() }

        @AfterAll
        fun afterAll() {
            postgresql.stop()
        }
    }

    @BeforeAll
    fun setUp() {
        testRestTemplate.setUriTemplateHandler(DefaultUriBuilderFactory("http://localhost:$port"))
        headers.set("X-API-KEY", "testkey")
        headers.set("Content-Type", "text/csv")
    }

    @BeforeEach
    fun cleanUpDB() {
        usedVacationRepository.deleteAll()
        vacationRepository.deleteAll()
        employeeRepository.deleteAll()
    }

    @Nested
    inner class ImportEmployee {
        @Test
        fun `should successfully import employees`() {
            val body = FileUtils.readFile(FileEnum.getFilePath(FileEnum.IMPORT_EMPLOYEE_VALID_CSV))
            val request = HttpEntity(body, headers)
            val response =
                testRestTemplate
                    .exchange(
                        "/import/employees",
                        HttpMethod.POST,
                        request,
                        object : ParameterizedTypeReference<List<EmployeeResponse>>() {},
                    )
            val expectedResponseBody = listOf(EmployeeResponse("test1@rbt.rs"))
            val expectedResponseStatusCode = HttpStatus.OK

            // response asserts
            response shouldNotBe null
            response.statusCode shouldBe expectedResponseStatusCode
            response.body shouldBe expectedResponseBody

            // database asserts
            employeeRepository.findAll().value.size shouldBe expectedResponseBody.size
            employeeRepository.findEmployeeById("test1@rbt.rs").value shouldNotBe null
        }
    }

    @Nested
    inner class ImportVacation {
        @Test
        fun `should successfully import vacations`() {
            // Prep
            val employee = Employee("test1@rbt.rs", "testPassword")
            employeeRepository.save(employee)

            // Request
            val body = FileUtils.readFile(FileEnum.getFilePath(FileEnum.IMPORT_VACATION_VALID_CSV))
            val year = 2019
            val request = HttpEntity(body, headers)
            val response =
                testRestTemplate
                    .exchange(
                        "/import/vacations/$year",
                        HttpMethod.POST,
                        request,
                        object : ParameterizedTypeReference<List<VacationResponse>>() {},
                    )
            val expectedResponseBody = listOf(VacationResponse("test1@rbt.rs", year, 20, 0))
            val expectedResponseStatusCode = HttpStatus.OK

            // response asserts
            response shouldNotBe null
            response.statusCode shouldBe expectedResponseStatusCode
            response.body shouldBe expectedResponseBody

            // database asserts
            vacationRepository.findAll().value.size shouldBe expectedResponseBody.size
            vacationRepository.findVacationById(VacationId("test1@rbt.rs", year)).value shouldNotBe null
        }

        @Test
        fun `should raise not found error`() {
            val body = FileUtils.readFile(FileEnum.getFilePath(FileEnum.IMPORT_VACATION_NOT_FOUND_ERROR_CSV))
            val year = 2019
            val request = HttpEntity(body, headers)
            val response =
                testRestTemplate
                    .exchange(
                        "/import/vacations/$year",
                        HttpMethod.POST,
                        request,
                        NotFoundError::class.java,
                    )
            val expectedResponseBody = NotFoundError("Employee with id: test1@rbt.rs not found", "Employee")
            val expectedResponseStatusCode = HttpStatus.BAD_REQUEST

            // response asserts
            response shouldNotBe null
            response.statusCode shouldBe expectedResponseStatusCode
            response.body shouldBe expectedResponseBody

            // database asserts
            vacationRepository.findAll().value.isEmpty() shouldBe true
        }
    }

    @Nested
    inner class ImportUsedVacation {
        @Test
        fun `should successfully import vacations`() {
            // Prep
            val employee = Employee("test1@rbt.rs", "testPassword")
            employeeRepository.save(employee)
            val vacation = Vacation(VacationId("test1@rbt.rs", 2019), 20, 0, employee)
            vacationRepository.save(vacation)

            // Request
            val body = FileUtils.readFile(FileEnum.getFilePath(FileEnum.IMPORT_USED_VACATION_VALID_CSV))
            val request = HttpEntity(body, headers)
            val response =
                testRestTemplate
                    .exchange(
                        "/import/usedVacations",
                        HttpMethod.POST,
                        request,
                        object : ParameterizedTypeReference<List<UsedVacationResponse>>() {},
                    )
            val expectedResponseBody =
                listOf(UsedVacationResponse("test1@rbt.rs", 2019, LocalDate.of(2019, 11, 11), LocalDate.of(2019, 11, 20)))
            val expectedResponseStatusCode = HttpStatus.OK

            // response asserts
            response shouldNotBe null
            response.statusCode shouldBe expectedResponseStatusCode
            response.body shouldBe expectedResponseBody

            // database asserts
            usedVacationRepository.findAll().value.size shouldBe expectedResponseBody.size
            usedVacationRepository.findUsedVacationById(
                UsedVacationId(VacationId("test1@rbt.rs", 2019), LocalDate.of(2019, 11, 11)),
            ) shouldNotBe
                null
        }

        @Test
        fun `should raise not found error`() {
            val body = FileUtils.readFile(FileEnum.getFilePath(FileEnum.IMPORT_USED_VACATION_NOT_FOUND_ERROR_CSV))
            val request = HttpEntity(body, headers)
            val response =
                testRestTemplate
                    .exchange(
                        "/import/usedVacations",
                        HttpMethod.POST,
                        request,
                        NotFoundError::class.java,
                    )
            val expectedResponseBody = NotFoundError("Vacation with id: ${VacationId("test1@rbt.rs", 2019)} not found", "Vacation")
            val expectedResponseStatusCode = HttpStatus.BAD_REQUEST

            // response asserts
            response shouldNotBe null
            response.statusCode shouldBe expectedResponseStatusCode
            response.body shouldBe expectedResponseBody

            // database asserts
            usedVacationRepository.findAll().value.isEmpty() shouldBe true
        }

        @Test
        fun `should raise transaction error on save because data violate check`() {
            val employee = Employee("test1@rbt.rs", "testPassword")
            employeeRepository.save(employee)
            val vacation = Vacation(VacationId("test1@rbt.rs", 2019), 1, 0, employee)
            vacationRepository.save(vacation)

            val body = FileUtils.readFile(FileEnum.getFilePath(FileEnum.IMPORT_USED_VACATION_VALID_CSV))
            val request = HttpEntity(body, headers)
            val response =
                testRestTemplate
                    .exchange(
                        "/import/usedVacations",
                        HttpMethod.POST,
                        request,
                        TransactionError::class.java,
                    )
            val expectedResponseStatusCode = HttpStatus.BAD_REQUEST

            response shouldNotBe null
            response.statusCode shouldBe expectedResponseStatusCode
            response.body shouldNotBe null
            response.body!!.operation shouldBe "COMMIT"
        }
    }

    @Nested
    inner class SecurityConfig {
        @Test
        fun `should return unauthorized request`() {
            val body = FileUtils.readFile(FileEnum.getFilePath(FileEnum.SECURITY_CONFIG_AUTHENTICATION_ERROR_CSV))
            headers.set("X-API-KEY", "invalidapikey")
            val request = HttpEntity(body, headers)
            val response =
                testRestTemplate
                    .exchange(
                        "/import/employees",
                        HttpMethod.POST,
                        request,
                        BadCredentialsError::class.java,
                    )
            val expectedResponseBody = BadCredentialsError("Invalid token", "X-API-KEY")
            val expectedResponseCode = HttpStatus.UNAUTHORIZED

            response shouldNotBe null
            response.statusCode shouldBe expectedResponseCode
            response.body shouldBe expectedResponseBody
        }
    }
}
