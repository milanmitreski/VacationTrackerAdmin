package rbt.vacationtracker.mapper.csv

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import rbt.vacationtracker.dto.UsedVacationModel
import rbt.vacationtracker.error.BadCSVFormatError
import rbt.vacationtracker.error.CSVDataFormatError
import rbt.vacationtracker.error.CSVError
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class UsedVacationCsvMapper {
    fun csvLineToUsedVacationModel(line: Map<String, String>): Result<UsedVacationModel, CSVError> {
        val email = line["Employee"]
        val beginDateString = line["Vacation start date"]
        val endDateString = line["Vacation end date"]

        if (email == null || beginDateString == null || endDateString == null || line.size != 3) {
            return Err(
                BadCSVFormatError(
                    "Invalid header or invalid CSV format, line: ${
                        line.values.reduce { acc, s ->
                            "$acc,$s"
                        }
                    }",
                ),
            )
        }

        val datePattern = "EEEE, MMMM dd, yyyy"
        val formatter = DateTimeFormatter.ofPattern(datePattern)

        val beginDate =
            try {
                LocalDate.parse(beginDateString, formatter)
            } catch (e: DateTimeParseException) {
                return Err(
                    CSVDataFormatError(
                        "Wrong date format.",
                        beginDateString,
                        datePattern,
                    ),
                )
            }

        val endDate =
            try {
                LocalDate.parse(endDateString, formatter)
            } catch (e: DateTimeParseException) {
                return Err(
                    CSVDataFormatError(
                        "Wrong date format.",
                        endDateString,
                        datePattern,
                    ),
                )
            }

        return Ok(
            UsedVacationModel(
                email,
                beginDate.year,
                beginDate,
                endDate,
            ),
        )
    }
}
