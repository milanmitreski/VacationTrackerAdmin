package rbt.vacationtracker.repository.implementation

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.toErrorIfNull
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import rbt.vacationtracker.domain.Vacation
import rbt.vacationtracker.domain.VacationId
import rbt.vacationtracker.error.NotFoundError
import rbt.vacationtracker.error.RepositoryError
import rbt.vacationtracker.extensions.toResult
import rbt.vacationtracker.repository.VacationRepository
import rbt.vacationtracker.repository.jpa.JpaVacationRepository

@Component
class VacationRepositoryImpl(
    private val jpaVacationRepository: JpaVacationRepository,
) : VacationRepository {
    override fun save(vacation: Vacation): Result<Vacation, RepositoryError> =
        jpaVacationRepository.toResult {
            jpaVacationRepository.save(vacation)
        }

    override fun findVacationById(id: VacationId): Result<Vacation, RepositoryError> =
        jpaVacationRepository
            .toResult {
                jpaVacationRepository.findByIdOrNull(id)
            }.toErrorIfNull {
                NotFoundError("Vacation with id: $id not found", "Vacation")
            }

    override fun findAll(): Result<List<Vacation>, RepositoryError> =
        jpaVacationRepository.toResult {
            jpaVacationRepository.findAll()
        }

    override fun deleteAll(): Result<Unit, RepositoryError> =
        jpaVacationRepository.toResult {
            jpaVacationRepository.deleteAll()
        }

    override fun numberOfEmployeesByYear(): Result<Map<Int, Int>, RepositoryError> =
        jpaVacationRepository.toResult {
            jpaVacationRepository.findEmployeesByYear().toMap()
        }

}
