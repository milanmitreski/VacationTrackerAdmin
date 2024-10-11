package rbt.vacationtracker.mapper.csv

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import rbt.vacationtracker.dto.VacationModel
import rbt.vacationtracker.error.BadCSVFormatError
import rbt.vacationtracker.error.CSVError

class VacationCsvMapper {
    fun csvLineToVacationModel(
        line: Map<String, String>,
        year: Int,
    ): Result<VacationModel, CSVError> {
        val email = line["Employee"]
        val days = line["Total vacation days"]

        if (email == null || days == null || days.toIntOrNull() == null || line.size != 2) {
            return Err(
                BadCSVFormatError(
                    "Invalid header or invalid CSV format, line: ${line.values.reduce { acc, s ->
                        "$acc,$s"
                    }}",
                ),
            )
        }

        return Ok(
            VacationModel(
                email,
                year,
                days.toInt(),
                0,
            ),
        )
    }
}
