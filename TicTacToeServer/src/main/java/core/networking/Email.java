/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.networking;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

/**
 *
 * @author Josua Frank
 */
public class Email {

    private static Properties mailServerProperties;
    private static Session getMailSession;
    private static MimeMessage generateMailMessage;
    private String email;
    private String password;

    public Email(String pEmail, String pPassword) {
        this.email = pEmail;
        this.password = pPassword;
    }

    /**
     * Bastelt den Emailinhalt aufgrund der Sprache und dem Code zusammen und
     * schickt diese dann an den Empfänger
     *
     * @param pReceiver Der Empfänger der Email
     * @param pLanguage Die Sprache des Empfängers
     * @param pCode Sein Emailcode
     */
    public void sendConfirmationMail(String pReceiver, String pLanguage, String pCode) {
        while (pCode.length() < 4) {
            pCode = "0".concat(pCode);
        }
        String subject = "", content = "";
        switch (pLanguage) {
            case "en":
                subject = "email-verification for TicTacToe";
                content = "Hello,<br><br>please enter this code: <b>" + pCode + "</b> in your launcher.<br>If you haven't signed up, just ignore this eail.<br><br>Kind regards<br>The TicTacToe-team";
                break;
            case "de":
                subject = "E-Mailverifikation für TicTacToe";
                content = "Hallo,<br><br>bitte gib diesen Code: <b>" + pCode + "</b> in dem Launcher ein.<br>Wenn du dich nicht registriert haben solltest, kannst du diese E-Mail ignorieren.<br><br>Beste Gruesse<br>Das TicTacToe-Team";
                break;
        }
        sendMail(pReceiver, subject, content);
    }

    public void sendNewPasswordMail(String pReceiver, String pLanguage, String pCode) {
        while (pCode.length() < 7) {
            pCode = "0".concat(pCode);
        }
        String subject = "", content = "";
        switch (pLanguage) {
            case "en":
                subject = "new password for TicTacToe";
                content = "Hello,<br><br>please enter this code: <b>" + pCode + "</b> in your launcher.<br>If you haven't requested a new password, just ignore this eail.<br><br>Kind regards<br>The TicTacToe-team";
                break;
            case "de":
                subject = "Neues Passwort für TicTacToe";
                content = "Hallo,<br><br>bitte gib diesen Code: <b>" + pCode + "</b> in dem Launcher ein.<br>Wenn du kein neues Passwort beantragt haben solltest, kannst du diese E-Mail ignorieren.<br><br>Beste Gruesse<br>Das TicTacToe-Team";
                break;
        }
        sendMail(pReceiver, subject, content);
    }

    /**
     * Sendet Emails
     *
     * @param pReceiver Der Emailempfänger
     * @param pSubject Der Betreff der Email
     * @param pContent Der Inhalt der Email
     */
    private void sendMail(String pReceiver, String pSubject, String pContent) {

        try {
            //1st ===> setup Mail Server Properties..
            mailServerProperties = System.getProperties();
            mailServerProperties.put("mail.smtp.user", "username");
            mailServerProperties.put("mail.smtp.host", "smtp.gmail.com");
            mailServerProperties.put("mail.smtp.port", "25");
//                mailServerProperties.put("mail.debug", "true");
            mailServerProperties.put("mail.smtp.auth", "true");
            mailServerProperties.put("mail.smtp.starttls.enable", "true");
            mailServerProperties.put("mail.smtp.EnableSSL.enable", "true");

            mailServerProperties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            mailServerProperties.setProperty("mail.smtp.socketFactory.fallback", "false");
            mailServerProperties.setProperty("mail.smtp.port", "465");
            mailServerProperties.setProperty("mail.smtp.socketFactory.port", "465");
            //Mail Server Properties have been setup successfully..

            //2nd ===> get Mail Session..
            getMailSession = Session.getDefaultInstance(mailServerProperties, null);
            generateMailMessage = new MimeMessage(getMailSession);
            generateMailMessage.setFrom(new InternetAddress(email));
            generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(pReceiver));
            //generateMailMessage.addRecipient(Message.RecipientType.CC, new InternetAddress("test2@crunchify.com"));
            generateMailMessage.setSubject(pSubject);
            generateMailMessage.setContent(pContent, "text/html");
            //Mail Session has been created successfully..

            // Step3
            //3rd ===> Get Session and Send mail
            Transport transport = getMailSession.getTransport("smtp");

            // Enter your correct gmail UserID and Password
            // if you have 2FA enabled then provide App Specific Password
            transport.connect("smtp.gmail.com", email, password);
            transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
            transport.close();
            //===> Your Java Program has just sent an Email successfully. Check your email..
        } catch (AddressException ex) {
            System.err.println("Adressfehler beim Senden der Bestätigungsmail: " + ex);
            ex.printStackTrace(System.err);
        } catch (MessagingException ex) {
            System.err.println("Nachrichtsfehler beim Senden der Bestätigungsmail: " + ex);
            ex.printStackTrace(System.err);
        } catch (Exception ex) {
            System.err.println("Konnte Email nicht senden: " + ex);
        }
    }
}
