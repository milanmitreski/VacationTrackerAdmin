package rbt.vacationtracker.repository.jpa

import org.springframework.data.jpa.repository.JpaRepository
import rbt.vacationtracker.domain.UsedVacation
import rbt.vacationtracker.domain.UsedVacationId

interface JpaUsedVacationRepository : JpaRepository<UsedVacation, UsedVacationId>
