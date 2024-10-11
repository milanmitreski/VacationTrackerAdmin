package rbt.vacationtracker.service.implementation

import com.github.michaelbull.result.*
import org.springframework.stereotype.Service
import rbt.vacationtracker.domain.VacationId
import rbt.vacationtracker.dto.UsedVacationResponse
import rbt.vacationtracker.dto.toResponse
import rbt.vacationtracker.dto.toUsedVacation
import rbt.vacationtracker.error.Error
import rbt.vacationtracker.extensions.executeTransaction
import rbt.vacationtracker.mapper.csv.UsedVacationCsvMapper
import rbt.vacationtracker.repository.UsedVacationRepository
import rbt.vacationtracker.repository.VacationRepository
import rbt.vacationtracker.service.UsedVacationService
import rbt.vacationtracker.service.transactionManager.ServiceTransactionManager
import rbt.vacationtracker.utils.CSVParser

@Service
class UsedVacationServiceImpl(
    private val vacationRepository: VacationRepository,
    private val usedVacationRepository: UsedVacationRepository,
    private val serviceTransactionManagerService: ServiceTransactionManager,
) : UsedVacationService {
    private val usedVacationCsvMapper: UsedVacationCsvMapper = UsedVacationCsvMapper()

    override fun addUsedVacationsCSV(csv: String): Result<List<UsedVacationResponse>, Error> =
        serviceTransactionManagerService.executeTransaction {
            binding {
                val rowList = CSVParser.parseCSV(csv).bind()
                val responseList = mutableListOf<UsedVacationResponse>()
                rowList.forEach {
                    val usedVacationModel = usedVacationCsvMapper.csvLineToUsedVacationModel(it).bind()
                    val vacation = vacationRepository.findVacationById(VacationId(usedVacationModel.email, usedVacationModel.year)).bind()
                    val usedVacation = usedVacationRepository.save(usedVacationModel.toUsedVacation(vacation)).bind()
                    responseList.add(usedVacation.toResponse())
                }
                responseList.toList()
            }
        }
}
