package rbt.vacationtracker.domain

import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinColumns
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "used_vacation")
class UsedVacation(
    @EmbeddedId
    var usedVacationId: UsedVacationId,
    @Column(name = "end_date")
    var endDate: LocalDate,
    @MapsId("vacationId")
    @JoinColumns(
        value = [
            JoinColumn(
                name = "email",
                referencedColumnName = "email",
            ),
            JoinColumn(
                name = "year",
                referencedColumnName = "year",
            ),
        ],
    )
    @ManyToOne
    var vacation: Vacation,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UsedVacation

        return usedVacationId == other.usedVacationId
    }

    override fun hashCode(): Int = usedVacationId.hashCode()
}
