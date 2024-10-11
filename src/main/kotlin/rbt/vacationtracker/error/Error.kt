package rbt.vacationtracker.error

sealed interface Error {
    val message: String
}

sealed interface RepositoryError : Error

data class NotFoundError(
    override val message: String,
    val table: String,
) : RepositoryError

data class TransactionError(
    override val message: String,
    val operation: String,
) : RepositoryError

sealed interface DatabaseError : RepositoryError {
    val cause: Throwable
}

data class TransientError(
    override val message: String,
    override val cause: Throwable,
) : DatabaseError

data class NonTransientError(
    override val message: String,
    override val cause: Throwable,
) : DatabaseError

sealed interface AuthenticationError : Error

data class BadCredentialsError(
    override val message: String,
    val authType: String,
) : AuthenticationError

sealed interface CSVError : Error

data class BadCSVFormatError(
    override val message: String,
) : CSVError

data class CSVDataFormatError(
    override val message: String,
    val data: String,
    val expectedFormat: String,
) : CSVError

data class MalformedCSVError(
    override val message: String,
) : CSVError
