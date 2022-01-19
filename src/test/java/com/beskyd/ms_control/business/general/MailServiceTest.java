package com.beskyd.ms_control.business.general;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

public class MailServiceTest {

    private String receiver;
    private String title;
    private String text;
    private String html;
    private Map<String, String> systemParams = new HashMap<>();
    private MailService mailService;

    @Mock
    SystemParametersService systemParametersService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        receiver = "ivan.tsitserskyi@telclic.net";
        title = "testTitleAferRefactoring";
        text = "some test text after refactoring";
        html = "<b>V</b><i>iktor</i> (after <b>refactoring</b>)";

        systemParams.put("domain_link", "https://sandbox.telclic.net");
        systemParams.put("email_host", "mail.blacknight.com");
        systemParams.put("email_password", "iXxvU4kW");
        systemParams.put("email_port", "587");
        systemParams.put("email_starttls", "false");
        systemParams.put("email_username", "alerts@stock.telclic.net");
        systemParams.put("low_stock_percentage", "15");
        systemParams.put("pdf_text", "");
        systemParams.put("stock_request_generation_trigger", "10");

        mailService = new MailService(systemParametersService);
        when(systemParametersService.findAllParameters()).thenReturn(systemParams);
    }

    @Test
    @Ignore
    public void sendSimpleMessage() {
        mailService.sendSimpleMessage(receiver, title, text);
    }

    @Test
    @Ignore
    public void sendMessageWithAttachment() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] byteTestArray = {1, 2, 3, 4, 5, 6, 7, 8, 9, 0}; //can be visible "as is" in hex mode view
        byteArrayOutputStream.writeBytes(byteTestArray);
        mailService.sendMessageWithAttachment(receiver, title, text, "pdf", byteArrayOutputStream, "application/pdf");
    }

    @Test
    @Ignore
    public void sendHtmlMessage() {
        mailService.sendHtmlMessage(receiver, title, html);
    }
}
