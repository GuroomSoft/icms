package com.guroomsoft.icms.common.service;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.font.FontProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class PdfService {
    public static final String FONT_PATH = "fonts/NANUMGOTHIC.TTF";

    public byte[] convertHtmlToPdf(String htmlContent) throws IOException {
        ConverterProperties properties = new ConverterProperties();

        PdfFont font = PdfFontFactory.createFont(FONT_PATH, PdfEncodings.IDENTITY_H);

        FontProvider fontProvider = new DefaultFontProvider(false, false, false);
        FontProgram fontProgram = FontProgramFactory.createFont(FONT_PATH);
        fontProvider.addFont(fontProgram);
        properties.setFontProvider(fontProvider);

        List<IElement> elements = HtmlConverter.convertToElements(htmlContent, properties);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfDocument pdf = new PdfDocument(new PdfWriter(outputStream));
        Document document = new Document(pdf);

        document.setMargins(10,10,10,10);

        for(IElement element:elements) {
            document.add((IBlockElement) element);
        }

        document.close();
        return outputStream.toByteArray();
    }
}
