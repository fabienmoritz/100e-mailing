package le100e

import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import java.io.File
import java.io.FileReader
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.reflect.jvm.jvmErasure


object ReceiptCsvParser {
    data class Field(val attribute: String, val header: String)

    fun parse(csvFile: String): List<Receipt> {
        val csvReader = CSVReaderBuilder(FileReader(File("./src/main/resources/$csvFile")))
                .withCSVParser(CSVParserBuilder()
                        .withSeparator(';')
                        .build())
                .build()
        val lines = csvReader.readAll()
        println(lines.subList(0, 2))
        val headers = lines.first()

        val constr = Receipt::class.constructors.first()
        val fields = constr.parameters.map { Field(it.name!!, it.annotations.filterIsInstance(Header::class.java).first().name) }
        val fieldIndexes = headers
                .mapIndexedNotNull { index, header ->
                    if (header in fields.map { it.header }) header to index
                    else null
                }.toMap()
        val receipts = lines.drop(1).map { line ->
            val args = fieldIndexes.map { (header, index) ->
                val param = constr.parameters.first { it.name == fields.first { it.header == header }.attribute }
                val stringValue = line[index]
                val value = when (param.type.jvmErasure) {
                    String::class -> stringValue
                    LocalDate::class -> LocalDate.from(DateTimeFormatter.ofPattern("yyyy-MM-dd").parse(stringValue))
                    Double::class -> stringValue.toDouble()
                    else -> throw Exception("what ? ${param.type}")
                }
                param to value
            }.toMap()
            constr.callBy(args)
        }

        return receipts
    }
}