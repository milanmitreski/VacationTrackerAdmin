package rbt.vacationtracker.testing.unit.utils

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.test.context.ContextConfiguration
import rbt.vacationtracker.error.MalformedCSVError
import rbt.vacationtracker.utils.CSVParser

@ContextConfiguration(classes = [(CSVParserTest::class)])
class CSVParserTest {
    @Test
    fun `should parse csv`() {
        val result =
            CSVParser.parseCSV(
                "Col1,Col2,Col3\n" +
                    "val1,val2,val3\n" +
                    "val4,val5,val6\n",
            )
        val expected =
            Ok(
                listOf(
                    mapOf(Pair("Col1", "val1"), Pair("Col2", "val2"), Pair("Col3", "val3")),
                    mapOf(Pair("Col1", "val4"), Pair("Col2", "val5"), Pair("Col3", "val6")),
                ),
            )
        result shouldBe expected
    }

    @Test
    fun `should raise malformed csv error`() {
        val result =
            CSVParser.parseCSV(
                "BAD;\"Csv,format\n" +
                    "\n" +
                    "Error_should raise:)",
            )
        val expected = Err(MalformedCSVError("Unable to parse CSV file."))
        result shouldBe expected
    }
}
