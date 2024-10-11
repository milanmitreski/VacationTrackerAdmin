package rbt.vacationtracker.dto

import rbt.vacationtracker.domain.Employee

class EmployeeResponse(
    val email: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EmployeeResponse

        return email == other.email
    }

    override fun hashCode(): Int = email.hashCode()
}

fun Employee.toResponse(): EmployeeResponse = EmployeeResponse(email)

class EmployeeModel(
    val email: String,
    val password: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EmployeeModel

        return email == other.email
    }

    override fun hashCode(): Int = email.hashCode()
}

fun EmployeeModel.toEmployee(): Employee = Employee(email, password)
