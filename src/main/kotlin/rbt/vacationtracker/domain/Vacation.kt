package rbt.vacationtracker.domain

import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "vacation")
class Vacation(
    @EmbeddedId
    var vacationId: VacationId,
    @Column(name = "vacation_days")
    var vacationDays: Int,
    @Column(name = "days_used")
    var daysUsed: Int,
    @JoinColumn(
        name = "email",
        referencedColumnName = "email",
        updatable = false,
        insertable = false,
    )
    @ManyToOne
    var employee: Employee,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Vacation

        return vacationId == other.vacationId
    }

    override fun hashCode(): Int = vacationId.hashCode()
}
