package rbt.vacationtracker.service.implementation

import com.github.michaelbull.result.*
import org.springframework.stereotype.Service
import rbt.vacationtracker.dto.VacationResponse
import rbt.vacationtracker.dto.toResponse
import rbt.vacationtracker.dto.toVacation
import rbt.vacationtracker.error.Error
import rbt.vacationtracker.extensions.executeTransaction
import rbt.vacationtracker.mapper.csv.VacationCsvMapper
import rbt.vacationtracker.repository.EmployeeRepository
import rbt.vacationtracker.repository.VacationRepository
import rbt.vacationtracker.service.VacationService
import rbt.vacationtracker.service.transactionManager.ServiceTransactionManager
import rbt.vacationtracker.utils.CSVParser

@Service
class VacationServiceImpl(
    private val employeeRepository: EmployeeRepository,
    private val vacationRepository: VacationRepository,
    private val serviceTransactionManagerService: ServiceTransactionManager,
) : VacationService {
    private val vacationCsvMapper: VacationCsvMapper = VacationCsvMapper()

    override fun addVacationsCSV(
        csv: String,
        year: Int,
    ): Result<List<VacationResponse>, Error> =
        serviceTransactionManagerService.executeTransaction {
            binding {
                val rowList = CSVParser.parseCSV(csv).bind()
                val responseList = mutableListOf<VacationResponse>()
                rowList.forEach {
                    val vacationModel = vacationCsvMapper.csvLineToVacationModel(it, year).bind()
                    val employee = employeeRepository.findEmployeeById(vacationModel.email).bind()
                    val vacation = vacationRepository.save(vacationModel.toVacation(employee)).bind()
                    responseList.add(vacation.toResponse())
                }
                responseList.toList()
            }
        }
}
