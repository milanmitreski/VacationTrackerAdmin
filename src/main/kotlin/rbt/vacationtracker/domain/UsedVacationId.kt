package rbt.vacationtracker.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.Embedded
import java.io.Serializable
import java.time.LocalDate

@Embeddable
class UsedVacationId(
    @Embedded
    var vacationId: VacationId,
    @Column(name = "begin_date")
    var beginDate: LocalDate,
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UsedVacationId

        if (vacationId != other.vacationId) return false
        if (beginDate != other.beginDate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = vacationId.hashCode()
        result = 31 * result + beginDate.hashCode()
        return result
    }
}
