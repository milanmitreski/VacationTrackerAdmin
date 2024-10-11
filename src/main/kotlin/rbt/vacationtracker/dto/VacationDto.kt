package rbt.vacationtracker.dto

import rbt.vacationtracker.domain.Employee
import rbt.vacationtracker.domain.Vacation
import rbt.vacationtracker.domain.VacationId

class VacationResponse(
    val email: String,
    val year: Int,
    val vacationDays: Int,
    val daysUsed: Int,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VacationResponse

        if (email != other.email) return false
        if (year != other.year) return false

        return true
    }

    override fun hashCode(): Int {
        var result = email.hashCode()
        result = 31 * result + year
        return result
    }
}

fun Vacation.toResponse(): VacationResponse =
    VacationResponse(
        vacationId.email,
        vacationId.year,
        vacationDays,
        daysUsed,
    )

class VacationModel(
    val email: String,
    val year: Int,
    val vacationDays: Int,
    val daysUsed: Int,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VacationModel

        if (email != other.email) return false
        if (year != other.year) return false

        return true
    }

    override fun hashCode(): Int {
        var result = email.hashCode()
        result = 31 * result + year
        return result
    }
}

fun VacationModel.toVacation(employee: Employee): Vacation =
    Vacation(
        VacationId(
            email,
            year,
        ),
        vacationDays,
        daysUsed,
        employee,
    )
