package rbt.vacationtracker.dto

import rbt.vacationtracker.domain.UsedVacation
import rbt.vacationtracker.domain.UsedVacationId
import rbt.vacationtracker.domain.Vacation
import rbt.vacationtracker.domain.VacationId
import java.time.LocalDate

class UsedVacationResponse(
    val email: String,
    val year: Int,
    val beginDate: LocalDate,
    val endDate: LocalDate,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UsedVacationResponse

        if (email != other.email) return false
        if (year != other.year) return false
        if (beginDate != other.beginDate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = email.hashCode()
        result = 31 * result + year
        result = 31 * result + beginDate.hashCode()
        return result
    }
}

fun UsedVacation.toResponse(): UsedVacationResponse =
    UsedVacationResponse(
        usedVacationId.vacationId.email,
        usedVacationId.vacationId.year,
        usedVacationId.beginDate,
        endDate,
    )

class UsedVacationModel(
    val email: String,
    val year: Int,
    val beginDate: LocalDate,
    val endDate: LocalDate,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UsedVacationModel

        if (email != other.email) return false
        if (year != other.year) return false
        if (beginDate != other.beginDate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = email.hashCode()
        result = 31 * result + year
        result = 31 * result + beginDate.hashCode()
        return result
    }
}

fun UsedVacationModel.toUsedVacation(vacation: Vacation): UsedVacation =
    UsedVacation(
        UsedVacationId(
            VacationId(
                email,
                year,
            ),
            beginDate,
        ),
        endDate,
        vacation,
    )
