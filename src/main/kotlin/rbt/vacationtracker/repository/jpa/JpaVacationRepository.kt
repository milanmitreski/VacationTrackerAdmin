package rbt.vacationtracker.repository.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import rbt.vacationtracker.domain.Vacation
import rbt.vacationtracker.domain.VacationId

interface JpaVacationRepository : JpaRepository<Vacation, VacationId> {
    @Query(
        """
            SELECT new kotlin.Pair(v.vacationId.year, COUNT(v.vacationId.email))
            FROM Vacation v
            GROUP BY v.vacationId.year 
        """,
    )
    fun findEmployeesByYear(): List<Pair<Int, Int>>
}
