package rbt.vacationtracker.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "employee")
class Employee(
    @Id
    @Column(name = "email")
    var email: String,
    @Column(name = "password")
    var password: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Employee

        return email == other.email
    }

    override fun hashCode(): Int = email.hashCode()
}
