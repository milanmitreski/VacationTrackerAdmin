@file:Suppress("ktlint:standard:no-wildcard-imports")

package rbt.vacationtracker.controller

import com.github.michaelbull.result.mapBoth
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import rbt.vacationtracker.error.DatabaseError
import rbt.vacationtracker.repository.VacationRepository
import rbt.vacationtracker.service.EmployeeService
import rbt.vacationtracker.service.UsedVacationService
import rbt.vacationtracker.service.VacationService

@RestController
@RequestMapping("/import")
class ImportController(
    val employeeService: EmployeeService,
    val vacationService: VacationService,
    val usedVacationService: UsedVacationService,
    val vacationRepository: VacationRepository
) {
    @PostMapping(
        value = ["/employees"],
        consumes = ["text/csv"],
    )
    fun addEmployees(
        @RequestHeader("X-API-KEY") authorization: String,
        @RequestBody csv: String,
    ): ResponseEntity<Any> =
        employeeService
            .addEmployeesCSV(csv)
            .mapBoth(
                { v -> ResponseEntity.ok(v) },
                { e ->
                    when (e) {
                        is DatabaseError -> ResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR)
                        else -> ResponseEntity(e, HttpStatus.BAD_REQUEST)
                    }
                },
            )

    @PostMapping(
        value = ["/vacations/{year}"],
        consumes = ["text/csv"],
    )
    fun addVacations(
        @RequestHeader("X-API-KEY") authorization: String,
        @RequestBody csv: String,
        @PathVariable year: Int,
    ): ResponseEntity<Any> =
        vacationService
            .addVacationsCSV(csv, year)
            .mapBoth(
                { v -> ResponseEntity.ok(v) },
                { e ->
                    when (e) {
                        is DatabaseError -> ResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR)
                        else -> ResponseEntity(e, HttpStatus.BAD_REQUEST)
                    }
                },
            )

    @PostMapping(
        value = ["/usedVacations"],
        consumes = ["text/csv"],
    )
    fun addUsedVacations(
        @RequestHeader("X-API-KEY") authorization: String,
        @RequestBody csv: String,
    ): ResponseEntity<Any> =
        usedVacationService
            .addUsedVacationsCSV(csv)
            .mapBoth(
                { v -> ResponseEntity.ok(v) },
                { e ->
                    when (e) {
                        is DatabaseError -> ResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR)
                        else -> ResponseEntity(e, HttpStatus.BAD_REQUEST)
                    }
                },
            )

    @GetMapping(
        value = ["/test"]
    )
    fun test(
        @RequestHeader("X-API-KEY") authorization: String,
    ): ResponseEntity<Any> =
        vacationRepository
            .numberOfEmployeesByYear()
            .mapBoth(
            { v -> ResponseEntity.ok(v) },
            { e ->
                when (e) {
                    is DatabaseError -> ResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR)
                    else -> ResponseEntity(e, HttpStatus.BAD_REQUEST)
                }
            },
        )

}
