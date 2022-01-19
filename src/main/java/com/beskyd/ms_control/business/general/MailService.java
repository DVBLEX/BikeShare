package com.beskyd.ms_control.business.general;

import java.io.ByteArrayOutputStream;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);

    private final SystemParametersService systemParamsService;

    MailService(SystemParametersService systemParamsService) {
        this.systemParamsService = systemParamsService;
    }

    private Properties getProperties() {

        var systemParameters = systemParamsService.findAllParameters();

        Properties emailProps = new Properties();
        emailProps.put("mail.smtp.host", systemParameters.get("email_host"));
        emailProps.put("mail.smtp.port", systemParameters.get("email_port"));
        emailProps.put("mail.smtp.auth", "true"); //"true"
        //emailProps.put("mail.smtp.socketFactory.port", emailConfig.getSmtpSocketFactoryPort());
        //emailProps.put("mail.smtp.socketFactory.class", emailConfig.getSmtpSocketFactoryClass());
        //emailProps.put("mail.smtp.socketFactory.fallback", emailConfig.getSmtpSocketFactoryFallback());
        emailProps.put("mail.smtp.starttls.enable", systemParameters.get("email_host"));
        //emailProps.put("mail.smtp.ssl.protocols", "TLSv1.2");

        return emailProps;
    }

    public void sendSimpleMessage(String receiver, String title, String text) {
        var systemParameters = systemParamsService.findAllParameters();
        try {
            LOGGER.debug("Sending...");
            Message msg = this.prepareMessage(systemParameters.get("email_username"), systemParameters.get("email_password"), receiver, title);
            msg.setText(text);
            Transport.send(msg);
            LOGGER.debug("Email sent!");
        } catch (Exception e) {
            LOGGER.error("The email was not sent.");
            LOGGER.error("Error message: {}", e.getMessage());
        }
    }

    public void sendMessageWithAttachment(String receiver, String title, String text, String attachmentName, ByteArrayOutputStream byteStream, String attachmentType) {
        var systemParameters = systemParamsService.findAllParameters();
        try {
            LOGGER.debug("Sending...");
            Message msg = prepareMessage(systemParameters.get("email_username"), systemParameters.get("email_password"), receiver, title);
            prepareMessageWithMultipart(msg, text, attachmentName, byteStream, attachmentType);
            Transport.send(msg);
            LOGGER.debug("Email sent!");
        } catch (Exception e) {
            LOGGER.error("The email was not sent.");
            LOGGER.error("Error message: {}", e.getMessage());
        }
    }

    public void sendHtmlMessage(String receiver, String title, String html) {
        var systemParameters = systemParamsService.findAllParameters();
        try {
            LOGGER.debug("Sending...");
            Message msg = prepareMessage(systemParameters.get("email_username"), systemParameters.get("email_password"), receiver, title);
            prepareMultipartMessage(msg, html);
            Transport.send(msg);
            LOGGER.debug("Html Email sent!");
        } catch (Exception e) {
            LOGGER.error("The email was not sent.");
            LOGGER.error("Error message: {}", e.getMessage());
        }
    }

    private InternetAddress[] validateReceivers(String receiver) {
        InternetAddress[] addressTo = null;

        try {
            addressTo = InternetAddress.parse(receiver);
            for (InternetAddress address : addressTo) {
                address.validate();
            }

        } catch (Exception e) {
            LOGGER.error("Error Validating address. message: {}", e.getMessage());
        }

        return addressTo;
    }

    private Session getSession(String hostEmail, String pass) {
        Session session = Session.getInstance(getProperties(), new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(hostEmail, pass);
            }
        });
        return session;
    }

    private Message prepareMessage(String email, String password, String receiver, String title) throws Exception {
        Message msg = new MimeMessage(this.getSession(email, password));
        InternetAddress addressFrom = new InternetAddress(email);
        msg.setFrom(addressFrom);
        msg.setRecipients(Message.RecipientType.TO, validateReceivers(receiver));
        msg.setSubject(title);
        return msg;
    }

    private void prepareMessageWithMultipart(Message message, String text, String attachmentName, ByteArrayOutputStream byteStream, String attachmentType) throws MessagingException {
        Multipart multipart = prepareMultipartMessage(message, text);
        getAttachmentMultipart(multipart, attachmentName, byteStream, attachmentType);
        message.setContent(multipart);
    }

    private Multipart prepareMultipartMessage(Message message, String html) throws MessagingException {
        Multipart multipart = new MimeMultipart();
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(html, "text/html");
        multipart.addBodyPart(messageBodyPart);
        message.setContent(multipart);
        return multipart;
    }

    private void getAttachmentMultipart(Multipart multipart, String attachmentName, ByteArrayOutputStream byteStream, String attachmentType) throws MessagingException{
        BodyPart messageAttPart = new MimeBodyPart();
        messageAttPart.setFileName(attachmentName);
        DataSource source = new ByteArrayDataSource(byteStream.toByteArray(), attachmentType);
        messageAttPart.setDataHandler(new DataHandler(source));
        multipart.addBodyPart(messageAttPart);
    }
}
