package rbt.vacationtracker.utils

enum class FileEnum(
    val fileName: String,
) {
    IMPORT_EMPLOYEE_VALID_CSV(generatePath("test_import_employee_valid.csv")),
    IMPORT_EMPLOYEE_BAD_CSV_FORMAT_ERROR_CSV(generatePath("test_import_employee_bad_csv_format_error.csv")),
    IMPORT_VACATION_VALID_CSV(generatePath("test_import_vacation_valid.csv")),
    IMPORT_VACATION_BAD_CSV_FORMAT_ERROR_CSV(generatePath("test_import_vacation_bad_csv_format_error.csv")),
    IMPORT_VACATION_NOT_FOUND_ERROR_CSV(generatePath("test_import_vacation_not_found_error.csv")),
    IMPORT_USED_VACATION_VALID_CSV(generatePath("test_import_used_vacation_valid.csv")),
    IMPORT_USED_VACATION_BAD_CSV_FORMAT_ERROR_CSV(generatePath("test_import_used_vacation_bad_csv_format_error.csv")),
    IMPORT_USED_VACATION_NOT_FOUND_ERROR_CSV(generatePath("test_import_used_vacation_not_found_error.csv")),
    SECURITY_CONFIG_AUTHENTICATION_ERROR_CSV(generatePath("test_security_config_authentication_error.csv")),
    ;

    companion object {
        fun getFilePath(fileEnum: FileEnum): String = fileEnum.fileName
    }
}

private fun generatePath(fileName: String): String = "src/test/resources/csv/$fileName"
