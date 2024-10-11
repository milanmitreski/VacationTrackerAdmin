package rbt.vacationtracker.repository.implementation

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.toErrorIfNull
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import rbt.vacationtracker.domain.Employee
import rbt.vacationtracker.error.NotFoundError
import rbt.vacationtracker.error.RepositoryError
import rbt.vacationtracker.extensions.toResult
import rbt.vacationtracker.repository.EmployeeRepository
import rbt.vacationtracker.repository.jpa.JpaEmployeeRepository

@Component
class EmployeeRepositoryImpl(
    private val jpaEmployeeRepository: JpaEmployeeRepository,
) : EmployeeRepository {
    override fun save(employee: Employee): Result<Employee, RepositoryError> =
        jpaEmployeeRepository.toResult {
            jpaEmployeeRepository.save(employee)
        }

    override fun findEmployeeById(id: String): Result<Employee, RepositoryError> =
        jpaEmployeeRepository
            .toResult {
                jpaEmployeeRepository.findByIdOrNull(id)
            }.toErrorIfNull {
                NotFoundError("Employee with id: $id not found", "Employee")
            }

    override fun findAll(): Result<List<Employee>, RepositoryError> =
        jpaEmployeeRepository.toResult {
            jpaEmployeeRepository.findAll()
        }

    override fun deleteAll(): Result<Unit, RepositoryError> =
        jpaEmployeeRepository.toResult {
            jpaEmployeeRepository.deleteAll()
        }
}
