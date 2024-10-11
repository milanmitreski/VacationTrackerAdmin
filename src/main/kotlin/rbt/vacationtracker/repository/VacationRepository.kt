package rbt.vacationtracker.repository

import com.github.michaelbull.result.Result
import rbt.vacationtracker.domain.Vacation
import rbt.vacationtracker.domain.VacationId
import rbt.vacationtracker.error.RepositoryError

interface VacationRepository {
    fun save(vacation: Vacation): Result<Vacation, RepositoryError>

    fun findVacationById(id: VacationId): Result<Vacation, RepositoryError>

    fun findAll(): Result<List<Vacation>, RepositoryError>

    fun deleteAll(): Result<Unit, RepositoryError>

    fun numberOfEmployeesByYear(): Result<Map<Int, Int>, RepositoryError>
}
