package le100e

import java.io.File
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart



class GmailSender(
        val username: String,
        val password: String
) {

    fun sendMail(
            mailTo: String,
            mailSubject: String,
            mailText: String,
            pdfFile: File) {

        val config = createConfiguration()

        // Creates a mail session. We need to supply username and
        // password for Gmail authentication.
        val session = Session.getInstance(config, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(
                        username,
                        password
                )
            }
        })


        val mail = MimeMessage(session)
        mail.addRecipient(Message.RecipientType.TO, InternetAddress(mailTo))
        mail.subject = mailSubject

        //val mailBody = "Test email. " + "<br><br> Regards, <br>le 100e singe"


        mail.setFrom(InternetAddress("$username@gmail.com"))
        mail.sentDate = Date()


        val multipart = MimeMultipart()
        val messageBodyPart = MimeBodyPart()

        messageBodyPart.setText(mailText, "utf-8", "html")
        multipart.addBodyPart(messageBodyPart)

        val attachmentBodyPart = MimeBodyPart()
        attachmentBodyPart.attachFile(pdfFile, "application/pdf", null)
        multipart.addBodyPart(attachmentBodyPart)

        mail.setContent(multipart)

/*
        // Creates email message
        val message = MimeMessage(session)
        message.sentDate = Date()
        message.setFrom(InternetAddress(mailFrom))
        message.setRecipient(Message.RecipientType.TO, InternetAddress(mailTo))
        message.subject = mailSubject
        message.setText(mailText)
*/
        // Send a message
        Transport.send(mail, username, password)
    }

    private fun createConfiguration(): Properties {
        return Properties().apply {
                put("mail.smtp.host", "smtp.gmail.com")
                put("mail.smtp.auth", "true")
                put("mail.smtp.port", "465")
                put("mail.smtp.socketFactory.port", "465")
                put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
        }
    }

}