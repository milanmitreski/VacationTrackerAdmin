@file:Suppress("ktlint:standard:no-wildcard-imports")

package rbt.vacationtracker.service.implementation

import com.github.michaelbull.result.*
import org.springframework.stereotype.Service
import rbt.vacationtracker.dto.EmployeeModel
import rbt.vacationtracker.dto.EmployeeResponse
import rbt.vacationtracker.dto.toEmployee
import rbt.vacationtracker.dto.toResponse
import rbt.vacationtracker.error.Error
import rbt.vacationtracker.extensions.executeTransaction
import rbt.vacationtracker.mapper.csv.EmployeeCsvMapper
import rbt.vacationtracker.repository.EmployeeRepository
import rbt.vacationtracker.service.EmployeeService
import rbt.vacationtracker.service.transactionManager.ServiceTransactionManager
import rbt.vacationtracker.utils.CSVParser
import rbt.vacationtracker.utils.Encoder

@Service
class EmployeeServiceImpl(
    private val employeeRepository: EmployeeRepository,
    private val serviceTransactionManagerService: ServiceTransactionManager,
) : EmployeeService {
    private val employeeCsvMapper: EmployeeCsvMapper = EmployeeCsvMapper()

    override fun addEmployeesCSV(csv: String): Result<List<EmployeeResponse>, Error> =
        serviceTransactionManagerService
            .executeTransaction {
                binding {
                    val rowList = CSVParser.parseCSV(csv).bind()
                    val responseList = mutableListOf<EmployeeResponse>()
                    rowList.forEach {
                        val employeeModel = employeeCsvMapper.csvLineToEmployeeModel(it).bind()
                        val employeeModelEncoded = encodeEmployeePassword(employeeModel)
                        val employee = employeeRepository.save(employeeModelEncoded.toEmployee()).bind()
                        responseList.add(employee.toResponse())
                    }
                    responseList.toList()
                }
            }

    private fun encodeEmployeePassword(model: EmployeeModel): EmployeeModel = EmployeeModel(model.email, Encoder.encode(model.password))
}
