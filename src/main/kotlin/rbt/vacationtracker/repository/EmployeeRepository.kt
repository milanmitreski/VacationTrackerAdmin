package rbt.vacationtracker.repository

import com.github.michaelbull.result.Result
import rbt.vacationtracker.domain.Employee
import rbt.vacationtracker.error.RepositoryError

interface EmployeeRepository {
    fun save(employee: Employee): Result<Employee, RepositoryError>

    fun findEmployeeById(id: String): Result<Employee, RepositoryError>

    fun findAll(): Result<List<Employee>, RepositoryError>

    fun deleteAll(): Result<Unit, RepositoryError>
}
