package rbt.vacationtracker.mapper.csv

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import rbt.vacationtracker.dto.EmployeeModel
import rbt.vacationtracker.error.BadCSVFormatError
import rbt.vacationtracker.error.CSVError

class EmployeeCsvMapper {
    fun csvLineToEmployeeModel(line: Map<String, String>): Result<EmployeeModel, CSVError> {
        val email = line["Employee Email"]
        val password = line["Employee Password"]

        if (email == null || password == null || line.size != 2) {
            return Err(
                BadCSVFormatError(
                    "Invalid header or invalid CSV format, line: ${line.values.reduce { acc, s ->
                        "$acc,$s"
                    }}",
                ),
            )
        }

        return Ok(
            EmployeeModel(
                email,
                password,
            ),
        )
    }
}
