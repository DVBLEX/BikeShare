package com.beskyd.ms_control.business.general;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;

@Service
public class PdfBuilderService {

    public ByteArrayOutputStream parse(String emailHtml) throws DocumentException, IOException {
        Document document = new Document();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, out);
        document.open();
        XMLWorkerHelper.getInstance().parseXHtml(writer, document,
                new ByteArrayInputStream(emailHtml.getBytes()));
        document.close();
        return out;
    }

    public void writePdf(String emailHtml, HttpServletResponse response, Principal principal) throws DocumentException, IOException {
        ByteArrayOutputStream out = parse(emailHtml);

        byte[] pdfBytes = out.toByteArray();

        response.setHeader("Content-Disposition", "inline");
        response.setHeader("Content-Type", MediaType.APPLICATION_PDF_VALUE);
        response.setContentLength(pdfBytes.length);
        FileCopyUtils.copy(pdfBytes, response.getOutputStream());
    }

}
