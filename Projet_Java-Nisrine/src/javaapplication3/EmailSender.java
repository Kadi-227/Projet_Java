package javaapplication3;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailSender {
    public static void sendVerificationEmail(String to, String code) throws MessagingException {
        String from = "fathinisrine18072004@gmail.com";
        String password = "hcoz nuya kenr bbtr"; // mot de passe d'application (pas le vrai !)

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(
                Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject("Code de vérification UNILIB");
        message.setText("Bonjour,\n\nVoici votre code de vérification : " + code + "\n\nUNILIB");

        Transport.send(message);
    }
}