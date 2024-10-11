package rbt.vacationtracker.service

import com.github.michaelbull.result.Result
import rbt.vacationtracker.dto.VacationResponse
import rbt.vacationtracker.error.Error

interface VacationService {
    fun addVacationsCSV(
        csv: String,
        year: Int,
    ): Result<List<VacationResponse>, Error>
}
