package rbt.vacationtracker.repository.jpa

import org.springframework.data.jpa.repository.JpaRepository
import rbt.vacationtracker.domain.Employee

interface JpaEmployeeRepository : JpaRepository<Employee, String>
