package rbt.vacationtracker.utils

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.opencsv.CSVReader
import rbt.vacationtracker.error.CSVError
import rbt.vacationtracker.error.MalformedCSVError
import java.io.StringReader

object CSVParser {
    fun parseCSV(csv: String): Result<List<Map<String, String>>, CSVError> {
        val lines =
            try {
                CSVReader(StringReader(csv)).readAll()
            } catch (e: Exception) {
                return Err(MalformedCSVError("Unable to parse CSV file."))
            }
        val header = lines[0]
        val data = lines.drop(1) // drop the header
        val mutableList = mutableListOf<Map<String, String>>()
        data.forEach { row ->
            mutableList.add(header.zip(row).toMap())
        }
        return Ok(mutableList.toList())
    }
}
