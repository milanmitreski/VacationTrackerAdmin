package rbt.vacationtracker.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
class VacationId(
    @Column(name = "email")
    var email: String,
    @Column(name = "year")
    var year: Int,
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VacationId

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
