package com.beskyd.ms_control.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import static com.itextpdf.text.Element.*;

public class QRGeneratorImpl implements QRGenerator {
	private static final int QR_IMAGE_SIZE = 300;
	private static final int QR_SIZE = 1000;
	private final String header;
	private final String content;
	private final String footer;
	
	public QRGeneratorImpl(String header, String content, String footer) {
		this.header = header;
		this.content = content;
		this.footer = footer;
	}

	@Override
	public byte[] getPdfBytes() throws IOException, QRGenerationException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			Document document = new Document();
			PdfWriter writer = PdfWriter.getInstance(document, out);
			document.open();
			XMLWorkerHelper.getInstance().parseXHtml(writer, document, new ByteArrayInputStream("".getBytes()));
			document.add(addTitle());
			document.add(createQRImage());
			document.add(addFooter());
			document.addTitle(header);
			document.addHeader("", header);
			document.close();
		} catch (DocumentException e) {
			throw new QRGenerationException("Error during QR generation", e);
		}
		return out.toByteArray();
	}

	private Paragraph addTitle() {
		Paragraph title = new Paragraph(header, new Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 35));
		title.setAlignment(ALIGN_CENTER);
		return title;
	}
	
	private Paragraph addFooter() {
        Paragraph ftr = new Paragraph(footer, new Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 35));
        ftr.setAlignment(ALIGN_CENTER);
        
        return ftr;
    }

	private Image createQRImage() throws BadElementException {
		BarcodeQRCode barcodeQRCode = new BarcodeQRCode(content, QR_SIZE, QR_SIZE, null);
		Image codeQrImage = barcodeQRCode.getImage();
		codeQrImage.scaleAbsolute(QR_IMAGE_SIZE, QR_IMAGE_SIZE);
		codeQrImage.setAlignment(ALIGN_MIDDLE);
		return codeQrImage;
	}
}
