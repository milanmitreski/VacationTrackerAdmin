package rbt.vacationtracker.repository.implementation

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.toErrorIfNull
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import rbt.vacationtracker.domain.UsedVacation
import rbt.vacationtracker.domain.UsedVacationId
import rbt.vacationtracker.error.NotFoundError
import rbt.vacationtracker.error.RepositoryError
import rbt.vacationtracker.extensions.toResult
import rbt.vacationtracker.repository.UsedVacationRepository
import rbt.vacationtracker.repository.jpa.JpaUsedVacationRepository

@Component
class UsedVacationRepositoryImpl(
    private val jpaUsedVacationRepository: JpaUsedVacationRepository,
) : UsedVacationRepository {
    override fun save(usedVacation: UsedVacation): Result<UsedVacation, RepositoryError> =
        jpaUsedVacationRepository.toResult {
            jpaUsedVacationRepository.save(usedVacation)
        }

    override fun findUsedVacationById(id: UsedVacationId): Result<UsedVacation, RepositoryError> =
        jpaUsedVacationRepository
            .toResult {
                jpaUsedVacationRepository.findByIdOrNull(id)
            }.toErrorIfNull {
                NotFoundError("UsedVacation with id: $id not found", "UsedVacation")
            }

    override fun findAll(): Result<List<UsedVacation>, RepositoryError> =
        jpaUsedVacationRepository.toResult {
            jpaUsedVacationRepository.findAll()
        }

    override fun deleteAll(): Result<Unit, RepositoryError> =
        jpaUsedVacationRepository.toResult {
            jpaUsedVacationRepository.deleteAll()
        }
}
