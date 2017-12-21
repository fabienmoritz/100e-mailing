package le100e

import com.itextpdf.forms.PdfAcroForm
import com.itextpdf.io.font.FontConstants
import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import java.io.File
import java.nio.file.Files
import java.text.DecimalFormat
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


//val pdfFont = PdfFontFactory.createRegisteredFont("LiberationSans", PdfEncodings.CP1252, true)


annotation class Header(val name: String)


data class Receipt(
        @Header("Courriel")
        val email: String,
        @Header("Prénom")
        val firstname: String,
        @Header("Nom")
        val name: String,
        @Header("Date de virement")
        val paymentDate: LocalDate,
        @Header("Adresse")
        val address: String,
        @Header("Code postal")
        val postalCode: String,
        @Header("Ville")
        val city: String,
        @Header("Pays")
        val country: String,
        @Header("Montant total")
        val amount: Double,
        @Header("Montant (lettres)")
        val amountLetters: String,
        @Header("Numéro de reçu fiscal")
        val receiptNumber: String,
        @Header("Type de paiement")
        val paymentMethod: String) {

    fun toPdf(): File {
        var fontNormal = PdfFontFactory.createFont(StandardFonts.HELVETICA)
        var fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)


        val outputDir = File("pdf-output")
        if (! outputDir.exists()) {
            Files.createDirectory(outputDir.toPath())
        }

        val outputFile = File("pdf-output/100esinge-$receiptNumber.pdf")
        if (outputFile.exists()) {
            outputFile.delete()
        }

        val pdfDoc = PdfDocument(
                PdfReader("template-receipt2.pdf"),
                PdfWriter(outputFile))

        val form = PdfAcroForm.getAcroForm(pdfDoc, true)
        val fields = form.formFields
        println("-------------------------------------------------------------------------")
        println(this)
        val city = city.let {
            if (country.isBlank() || country.trim().toLowerCase() == "france") it
            else "$it (${country.toUpperCase()})"
        }
        mapOf(
                "firstname" to firstname.capitalize(),
                "name" to name.toUpperCase(),
                "date" to paymentDate.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRANCE)),
                "address" to address.split("\n").joinToString(", "),
                "postalCode" to postalCode,
                "city" to city,
                "amountLetters" to amountLetters,
                "receiptNumber" to receiptNumber,
                "paymentMethod" to paymentMethod,

                "firstname2" to firstname.capitalize(),
                "name2" to name.toUpperCase(),
                "address2" to address,
                "postalCode2" to postalCode,
                "city2" to city,
                "amountNumber2" to DecimalFormat("#,##0.00").format(amount),
                "amountLetters2" to "($amountLetters)").forEach { key, value ->
            fields[key]?.let {
                it.font = fontBold
                val field = it.setValue(value)
                field.isReadOnly = true
                field
            } ?: println("MANQUE : $key = $value")
        }



        pdfDoc.close()

        return File("pdf-output/100esinge-$receiptNumber.pdf")
    }

}