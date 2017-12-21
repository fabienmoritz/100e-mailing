package le100e

import java.util.Locale


val senderGmailAccount = "lecentiemesinge"
val applicationKey = "REPLACE BY GMAIL PASSWORD OR APPLICATION PASSWORD"



fun main(vararg args: String) {

    Locale.setDefault(Locale.FRANCE)

    val receipts = ReceiptCsvParser.parse("RecusFiscauxFab.csv")

    val (noAddress, good) = receipts.partition { it.city.isBlank() }

    // Email information such as from, to, subject and contents.
    val mailSubject = "Reçu fiscal campagne Ulule"
    val mailText = """<p>Bonsoir,</p>
            <p>
                L'envoi précédent du reçu fiscal a rencontré quelques soucis techniques chez certains.
                C'est maintenant réparé, merci de ne prendre en compte que cette version.
            </p>
            <p>
                Bonne réception
            </p>
            <p>
                Le collectif du 100e Singe
            </p>
            """

    val gmail = GmailSender(senderGmailAccount, applicationKey)

    var doSend = true
    good.forEach {
        val pdf = it.toPdf()
        if (doSend) {
            try {
                val mailTo = it.email //"$senderGmailAccount@gmail.com" //it.email //FIXME
                gmail.sendMail(mailTo, mailSubject, mailText, pdf)
                //doSend = false
            } catch (e: Throwable) {
                println("============ECHEC MAIL============= $it")
                e.printStackTrace()
            }
        }

    }
    println("///////////////// A FINI \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\")
    //println(good.joinToString("\n") { it.toPdf().absolutePath })

}