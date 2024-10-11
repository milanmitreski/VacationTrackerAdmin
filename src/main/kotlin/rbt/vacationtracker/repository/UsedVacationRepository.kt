package rbt.vacationtracker.repository

import com.github.michaelbull.result.Result
import rbt.vacationtracker.domain.UsedVacation
import rbt.vacationtracker.domain.UsedVacationId
import rbt.vacationtracker.error.RepositoryError

interface UsedVacationRepository {
    fun save(usedVacation: UsedVacation): Result<UsedVacation, RepositoryError>

    fun findUsedVacationById(id: UsedVacationId): Result<UsedVacation, RepositoryError>

    fun findAll(): Result<List<UsedVacation>, RepositoryError>

    fun deleteAll(): Result<Unit, RepositoryError>
}
