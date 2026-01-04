package com.bavarians.service.impl;

import com.bavarians.graphql.model.Pojazd;
import com.bavarians.graphql.repository.PojazdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

@Service
public class SendMailBavarians {
    public static final int DAYS = 7;
    @Autowired
    private PojazdRepository pojazdRepository;

    public void send(String to, String from, String host, String port, String ssl, String subject, String body, String bcc) {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.ssl.enable", ssl);
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                String username = System.getenv("SMTP_USERNAME");
                String password = System.getenv("SMTP_PASSWORD");
                if (username == null || password == null) {
                    throw new RuntimeException("SMTP credentials not configured. Set SMTP_USERNAME and SMTP_PASSWORD environment variables.");
                }
                return new PasswordAuthentication(username, password);
            }
        });
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            if (bcc != null) {
                message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc));
            }
            message.setSubject(subject);
            message.setText(body);
            System.out.println("sending...");
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    @Scheduled(cron = "0 1 1 * * *") //day
//    @Scheduled(cron = "0 * * * * *") //minute
    public void emailcron() {
        pojazdRepository.findAll().forEach(pojazd -> {
            if (pojazd != null && pojazd.isAktywnePowiadomienia()) {
                Date terminBadania = pojazd.getTerminBadania();
                Calendar przeglad = Calendar.getInstance();
                przeglad.setTime(terminBadania);
                przeglad.add(Calendar.DATE, DAYS);


                Date terminOc = pojazd.getTerminOC();
                Calendar oc = Calendar.getInstance();
                oc.setTime(terminOc);
                oc.add(Calendar.DATE, DAYS);
                String sub = "";
                boolean shouldSendBad = przeglad.getTime().compareTo(new Date()) > 0;
                if (shouldSendBad) {
                    sub = "Przypomnienie o terminie badania technicznego " + pojazd.getNumerRejestracyjny();
                    sendPowiadomienie(pojazd, sub);
                }
                boolean shouldSendOc = oc.getTime().compareTo(new Date()) > 0;
                if (shouldSendOc) {
                    sub = "Przypomnienie o terminie OC " + pojazd.getNumerRejestracyjny();
                    sendPowiadomienie(pojazd, sub);
                }
            }
        });
    }

    private void sendPowiadomienie(Pojazd pojazd, String sub) {
        String body = "Twoje terminy: \n\n";
        body = body.concat(pojazd.getMarka()).concat(" ");
        body = body.concat(pojazd.getModel()).concat(" ");
        body = body.concat(pojazd.getNumerRejestracyjny()).concat(" ");
        body = body.concat("\n\n");
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date terminOC = pojazd.getTerminOC();
        Date terminBadania = pojazd.getTerminBadania();
        body = body.concat("Badanie techniczne: ").concat(dateFormatter.format(terminBadania)).concat("\n");
        body = body.concat("OC: ").concat(dateFormatter.format(terminOC)).concat("\n");
        String from = "biuro@bavarians.pl";
        String host = "smtp.cal.pl";
        send(pojazd.getPowiadomieniaEmail(), from, host, "587", "false",
                sub, body, null);
    }

}