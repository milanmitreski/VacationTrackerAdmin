package rbt.vacationtracker.service

import com.github.michaelbull.result.Result
import rbt.vacationtracker.dto.UsedVacationResponse
import rbt.vacationtracker.error.Error

interface UsedVacationService {
    fun addUsedVacationsCSV(csv: String): Result<List<UsedVacationResponse>, Error>
}
