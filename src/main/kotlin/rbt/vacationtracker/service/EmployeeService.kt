package rbt.vacationtracker.service

import com.github.michaelbull.result.Result
import rbt.vacationtracker.dto.EmployeeResponse
import rbt.vacationtracker.error.Error

interface EmployeeService {
    fun addEmployeesCSV(csv: String): Result<List<EmployeeResponse>, Error>
}
