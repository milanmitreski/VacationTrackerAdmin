@file:Suppress("ktlint:standard:no-wildcard-imports")

package rbt.vacationemployee.utils

import java.io.File
import java.util.*

object FileUtils {
    fun readFile(filePath: String): String {
        val read = StringBuilder()
        Scanner(File(filePath)).use { scanner ->
            while (scanner.hasNextLine()) {
                read.append(scanner.nextLine()).append("\n")
            }
        }
        return read.toString()
    }
}
